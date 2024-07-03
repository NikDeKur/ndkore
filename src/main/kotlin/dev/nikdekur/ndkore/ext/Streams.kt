/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

inline fun <T, K, V> Stream<T>.toMap(key: Function<T, K>, value: Function<T, V>): MutableMap<K, V> {
    return collect(Collectors.toMap(key, value))
}
inline fun <T, K, V> Stream<T>.toConcurrentMap(key: Function<T, K>, value: Function<T, V>): MutableMap<K, V> {
    return collect(Collectors.toConcurrentMap(key, value))
}
inline fun <T, K, V> Stream<T>.toUnmodifiableMap(key: Function<T, K>, value: Function<T, V>): Map<K, V> {
    return collect(Collectors.toMap(key, value))
}


