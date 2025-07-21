package dev.nikdekur.ndkore.event

public fun interface EventHandler<T : Event> {
    public suspend fun onEvent(event: T)
}