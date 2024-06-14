package dev.nikdekur.ndkore.module

enum class ModuleState {
    LOADING,
    LOADED,
    UNLOADED,
    UNLOADING

    ;

    fun toMoment(): TaskMoment {
        return when (this) {
            LOADING -> TaskMoment.BEFORE_LOAD
            LOADED -> TaskMoment.AFTER_LOAD
            UNLOADING -> TaskMoment.BEFORE_UNLOAD
            UNLOADED -> TaskMoment.AFTER_UNLOAD
        }
    }
}