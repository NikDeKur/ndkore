package dev.nikdekur.ndkore.list

import dev.nikdekur.ndkore.ext.*
import java.util.*

open class RunnableList : LinkedList<() -> Unit>(), MutableList<() -> Unit> {
    @Synchronized
    operator fun invoke() {
        mutableForEach { it() }
    }
}

open class SafeRunnableList : LinkedList<() -> Unit>(), MutableList<() -> Unit> {
    @Synchronized
    fun invoke(onError: (() -> Unit, Throwable) -> Unit = { _, _ ->}) {
        return mutableSafeForEach(onError) { it() }
    }
}

open class ConsumersList<O> : LinkedList<(O) -> Unit>(), MutableList<(O) -> Unit> {
    @Synchronized
    operator fun invoke(o: O) {
        mutableForEach { it(o) }
    }
}

open class SafeConsumersList<O> : LinkedList<(O) -> Unit>(), MutableList<(O) -> Unit> {
    @Synchronized
    fun invoke(o: O, onError: ((O) -> Unit, Throwable) -> Unit) {
        mutableSafeForEach(onError) { it(o) }
    }
}

open class BiConsumersList<O1, O2> : LinkedList<(O1, O2) -> Unit>(), MutableList<(O1, O2) -> Unit> {
    @Synchronized
    operator fun invoke(o1: O1, o2: O2) {
        mutableForEach { it(o1, o2) }
    }
}

open class SafeBiConsumersList<O1, O2> : LinkedList<(O1, O2) -> Unit>(), MutableList<(O1, O2) -> Unit> {
    @Synchronized
    fun invoke(o1: O1, o2: O2, onError: ((O1, O2) -> Unit, Throwable) -> Unit) {
        mutableSafeForEach(onError) { it(o1, o2) }
    }
}

