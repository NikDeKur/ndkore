package dev.nikdekur.ndkore.event.error

import dev.nikdekur.ndkore.event.Event
import dev.nikdekur.ndkore.event.EventBus

/**
 * # ErrorHandler
 *
 * An interface for handling errors in the event system.
 */
public interface ErrorHandler {

    public suspend fun onTimeout(bus: EventBus, event: Event)

    public suspend fun onException(bus: EventBus, event: Event, throwable: Throwable)
}