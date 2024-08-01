/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.extra

open class Switch(val default: Boolean = false) {

    open var value: Boolean = default
    inline fun get() = value
    inline fun set(value: Boolean) {
        this.value = value
    }

    inline fun switch() {
        value = !value
    }

    inline fun getAndSwitch(): Boolean {
        return value.also { switch() }
    }

    inline fun getAndSet(value: Boolean): Boolean {
        val c = this.value
        this.value = value
        return c
    }

    inline fun on() {
        value = true
    }
    inline fun off() {
        value = false
    }

    inline operator fun not() = switch()
    inline operator fun invoke() = value


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Switch) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "Switch(value=$value, default=$default)"
    }
}