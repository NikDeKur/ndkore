@file:JvmName("CollectionsJvmKt")
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import java.util.concurrent.ConcurrentHashMap

public inline fun <T> ConcurrentHashSet(): MutableSet<T> = ConcurrentHashMap.newKeySet()
