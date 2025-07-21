package dev.nikdekur.ndkore.event

import dev.nikdekur.ndkore.`interface`.Unique
import kotlin.reflect.KClass

public interface EventBus : Unique<String> {
    public fun <T : Event> on(eventType: KClass<T>, handler: EventHandler<T>)
    public fun <T : Event> removeEventHandler(eventType: KClass<T>, handler: EventHandler<T>): Boolean

    public suspend fun post(event: Event)
    public fun postAsync(event: Event)

    public fun stop()
}

public inline fun <reified T : Event> EventBus.on(noinline handle: suspend (T) -> Unit) {
    val handler = EventHandler<T> { event -> handle(event) }
    on(T::class, handler)
}