package dev.nikdekur.ndkore.module

enum class TaskMoment(val isBefore: Boolean) {
    BEFORE_LOAD(true),
    AFTER_LOAD(false),

    BEFORE_UNLOAD(true),
    AFTER_UNLOAD(false),

    ;

    val isAfter: Boolean
        get() = !isBefore
}