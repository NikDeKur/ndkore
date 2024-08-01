/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import org.jetbrains.annotations.Contract
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*


@Contract("null -> null")
inline fun Any.serialize(): String {
    try {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ObjectOutputStream(byteArrayOutputStream).use {
            it.writeObject(this)
        }
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
    } catch (e: Exception) {
        e.printStackAndThrow()
    }
}

inline fun Any?.serialise(): String {
    if (this == null) return "null"
    return this.serialize()
}
inline fun String.deSerialize(): Any {
    try {
        val data = Base64.getDecoder().decode(this)
        ObjectInputStream(ByteArrayInputStream(data)).use { objectInputStream -> return objectInputStream.readObject() }
    } catch (e: Exception) {
        e.printStackAndThrow()
    }
}