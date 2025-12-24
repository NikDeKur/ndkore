@file:Suppress("UNCHECKED_CAST")

package dev.nikdekur.ndkore.event

import co.touchlab.stately.collections.ConcurrentMutableList
import co.touchlab.stately.collections.ConcurrentMutableMap
import dev.nikdekur.ndkore.event.error.ErrorHandler
import dev.nikdekur.ndkore.event.timeout.TimeoutHandler
import dev.nikdekur.ndkore.map.MutableListsMap
import dev.nikdekur.ndkore.map.add
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlin.reflect.KClass

public open class FlowEventBus(
    public override val id: String,
    public val timeoutHandler: TimeoutHandler,
    public val errorHandler: ErrorHandler
) : EventBus {
    protected val logger: KLogger = KotlinLogging.logger("FlowEventBus-$id")

    protected val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    protected val handlers: MutableListsMap<KClass<*>, EventHandler<out Event>> = ConcurrentMutableMap()

    override fun <T : Event> on(eventType: KClass<T>, handler: EventHandler<T>) {
        handlers.add(
            key = eventType,
            value = handler,
            listGen = ::ConcurrentMutableList
        )
    }

    override fun <T : Event> removeEventHandler(eventType: KClass<T>, handler: EventHandler<T>): Boolean {
        return handlers[eventType]?.remove(handler) == true
    }

    override suspend fun post(event: Event) {
        val type = event::class

        @Suppress("UNCHECKED_CAST")
        val list = handlers[type] as? List<EventHandler<Event>> ?: return

        // Guarantees that all launched coroutines will complete before returning
        supervisorScope {
            val deferreds = list.map { handler ->
                async {
                    try {
                        timeoutHandler.withTimeout(event) {
                            handler.onEvent(event)
                        }
                    } catch (e: TimeoutCancellationException) {
                        errorHandler.onTimeout(this@FlowEventBus, event)
                    } catch (e: Exception) {
                        errorHandler.onException(this@FlowEventBus, event, e)
                    }
                }
            }
            deferreds.awaitAll()
        }
    }

    override fun postAsync(event: Event) {
        scope.launch(Dispatchers.Default) {
            try {
                post(event)
            } catch (e: Exception) {
                logger.error(e) { "Error async posting event ${event::class.simpleName} in EventBus `$id`" }
            }
        }
    }

    override fun stop() {
        handlers.clear()
    }
}
