package dev.nikdekur.ndkore.event

import dev.nikdekur.ndkore.`interface`.Unique
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KClass

public interface EventBus : Unique<String> {
    public suspend fun <T : Event> registerListener(
        eventType: KClass<T>,
        handler: EventHandler<T>,
        concurrency: Int = 1,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        listenerId: String = "Listener-${eventType.simpleName}-${handler.hashCode()}"
    ): ActiveEventHandler
}

public interface ActiveEventHandler : Unique<String> {
    public val eventType: KClass<out Event>
    public val isActive: Boolean

    public suspend fun awaitReady()

    public fun cancel()
}

public suspend inline fun <reified T : Event> EventBus.on(
    noinline handler: suspend (event: T) -> Unit,
    concurrency: Int = 1,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    listenerId: String = "Listener-${T::class.simpleName}-${handler.hashCode()}"
): ActiveEventHandler = registerListener(T::class, handler, concurrency, dispatcher, listenerId)