package dev.nikdekur.ndkore.interfaces

interface Cancellable {

    /**
     * Whether the operation is cancelled.
     */
    var isCancel: Boolean

    /**
     * Cancels the operation.
     *
     * @see isCancel
     */
    fun cancel() {
        isCancel = true
    }
}
