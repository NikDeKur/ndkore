package dev.nikdekur.ndkore.event.timeout

import dev.nikdekur.ndkore.event.Event

/**
 * Interface for handling timeouts in event processing.
 *
 * This interface defines a method to handle timeouts for events.
 * The implementation is responsible for deciding when and how to apply a timeout.
 */
public interface TimeoutHandler {
    public suspend fun withTimeout(event: Event, block: suspend () -> Unit)
}