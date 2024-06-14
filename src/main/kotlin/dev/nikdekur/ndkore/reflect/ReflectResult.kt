package dev.nikdekur.ndkore.reflect

sealed interface ReflectResult {

    class Success(val result: Any?): ReflectResult
    class ExceptionFail(val exception: Throwable?): ReflectResult
    data object FAIL: ReflectResult
}