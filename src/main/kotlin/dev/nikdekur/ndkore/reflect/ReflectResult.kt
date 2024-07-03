package dev.nikdekur.ndkore.reflect

open class ReflectResult(val value: Any?) {
    //
    data object Missing : ReflectResult(null)
}