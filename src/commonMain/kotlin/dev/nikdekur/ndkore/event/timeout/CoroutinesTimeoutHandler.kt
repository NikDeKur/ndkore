package dev.nikdekur.ndkore.event.timeout

import dev.nikdekur.ndkore.event.Event
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration

/**
 * # CoroutinesTimeoutHandler
 *
 * An implementation of [TimeoutHandler] that uses `kotlinx.coroutines.withTimeout` to handle timeouts.
 *
 * @param timeout The timeout duration.
 */
public class CoroutinesTimeoutHandler(
    public val timeout: Duration
) : TimeoutHandler {
    override suspend fun withTimeout(
        event: Event,
        block: suspend () -> Unit
    ) {
        withTimeout(timeout) {
            block()
        }
    }
}