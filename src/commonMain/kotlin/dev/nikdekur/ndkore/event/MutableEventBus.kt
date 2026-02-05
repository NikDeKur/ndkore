package dev.nikdekur.ndkore.event

import kotlin.reflect.KClass

public interface MutableEventBus : EventBus {

    public fun removeHandler(handlerId: String): ActiveEventHandler?
    public fun removeAllHandlers(type: KClass<out Event>): Int

    public suspend fun post(event: Event)
    public fun postAsync(event: Event)

    public fun shutdown()
}