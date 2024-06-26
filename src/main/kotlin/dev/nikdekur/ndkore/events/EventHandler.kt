package dev.nikdekur.ndkore.events


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class EventHandler(
    /**
     * Define the priority of the event.
     *
     *
     * First priority to the last priority executed:
     *
     *  1. LOWEST
     *  1. LOW
     *  1. NORMAL
     *  1. HIGH
     *  1. HIGHEST
     *  1. MONITOR
     *
     *
     * @return the priority
     */
    val priority: EventPriority = EventPriority.NORMAL,

    /**
     * Define if the handler ignores a cancelled event.
     *
     *
     * If ignoreCancelled is true and the event is cancelled, the method is
     * not called. Otherwise, the method is always called.
     *
     * @return whether cancelled events should be ignored
     */
    val ignoreCancelled: Boolean = false,
)
