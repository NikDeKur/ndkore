package dev.nikdekur.ndkore.event.error

import dev.nikdekur.ndkore.event.Event
import dev.nikdekur.ndkore.event.EventBus
import io.github.oshai.kotlinlogging.KotlinLogging

public class LoggingErrorHandler : ErrorHandler {

    private val logger = KotlinLogging.logger {}

    override suspend fun onTimeout(bus: EventBus, event: Event) {
        logger.warn { "Handler for ${event::class.simpleName} timed out in EventBus `${bus.id}`" }
    }

    override suspend fun onException(bus: EventBus, event: Event, throwable: Throwable) {
        logger.error(throwable) { "Handler for ${event::class.simpleName} threw an exception in EventBus `${bus.id}`" }
    }
}