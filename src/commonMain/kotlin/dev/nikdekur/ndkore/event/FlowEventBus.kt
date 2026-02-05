@file:Suppress("UNCHECKED_CAST")
@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.nikdekur.ndkore.event

import co.touchlab.stately.collections.ConcurrentMutableMap
import dev.nikdekur.ndkore.event.error.ErrorHandler
import dev.nikdekur.ndkore.event.timeout.TimeoutHandler
import dev.nikdekur.ndkore.ext.addById
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.KClass

public open class FlowEventBus(
    public override val id: String,
    public val eventsFlow: MutableSharedFlow<Event>,
    public val defaultDispatcher: CoroutineDispatcher,
    public val timeoutHandler: TimeoutHandler,
    public val errorHandler: ErrorHandler
) : MutableEventBus {
    protected val logger: KLogger = KotlinLogging.logger("FlowEventBus-$id")

    protected val scope: CoroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher)
    protected val eventHandlers: MutableMap<String, ActiveEventHandler> = ConcurrentMutableMap()
    protected val events: SharedFlow<Event> = eventsFlow.asSharedFlow()

    override suspend fun <T : Event> registerListener(
        eventType: KClass<T>,
        handler: EventHandler<T>,
        concurrency: Int,
        dispatcher: CoroutineDispatcher,
        listenerId: String
    ): ActiveEventHandler {
        require(concurrency >= 1)
        require(!eventHandlers.containsKey(listenerId)) {
            "EventHandler with id `$listenerId` is already registered in EventBus `$id`"
        }

        val ready = CompletableDeferred<Unit>()

        val job = scope.launch(dispatcher, start = CoroutineStart.UNDISPATCHED) {
            events
                .onSubscription { ready.complete(Unit) }
                .filterIsInstance(eventType)
                .flatMapMerge(concurrency) { event ->
                    flow<Unit> {
                        try {
                            handler.onEvent(event)
                        } catch (t: Throwable) {
                            if (t is CancellationException) throw t
                            try {
                                errorHandler.onException(this@FlowEventBus, event, t)
                            } catch (eh: Throwable) {
                                logger.error(eh) { "ErrorHandler failed" }
                            }
                        }
                    }
                }
                .collect()
        }

        job.invokeOnCompletion {
            eventHandlers.remove(listenerId)
        }

        val activeHandler = object : ActiveEventHandler {
            override val id = listenerId
            override val eventType = eventType
            override val isActive: Boolean
                get() = job.isActive

            override suspend fun awaitReady() {
                ready.await()
            }

            override fun cancel() {
                job.cancel("ActiveEventHandler-$id cancelled")
            }
        }

        eventHandlers.addById(activeHandler)

        return activeHandler
    }

    override fun removeHandler(handlerId: String): ActiveEventHandler? {
        val handler = eventHandlers.remove(handlerId)
        handler?.cancel()
        return handler
    }

    override fun removeAllHandlers(type: KClass<out Event>): Int {
        val toCancel = eventHandlers.values.filter { it.eventType == type }
        toCancel.forEach { it.cancel() }
        return toCancel.size
    }

    override suspend fun post(event: Event) {
        try {
            timeoutHandler.withTimeout(event) {
                eventsFlow.emit(event)
            }
        } catch (e: TimeoutCancellationException) {
            errorHandler.onTimeout(this@FlowEventBus, event)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            errorHandler.onException(this@FlowEventBus, event, e)
        }
    }

    override fun postAsync(event: Event) {
        scope.launch {
            try {
                post(event)
            } catch (e: Exception) {
                logger.error(e) { "Error async posting event ${event::class.simpleName} in EventBus `$id`" }
            }
        }
    }

    override fun shutdown() {
        scope.cancel("EventBus-$id shutdown")
    }
}