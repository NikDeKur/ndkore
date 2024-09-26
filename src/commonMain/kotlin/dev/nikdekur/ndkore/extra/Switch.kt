/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.extra

public open class Switch(public val default: Boolean = false) {

    public open var value: Boolean = default
    public inline fun get(): Boolean = value
    public inline fun set(value: Boolean) {
        this.value = value
    }

    public inline fun switch() {
        value = !value
    }

    public inline fun getAndSwitch(): Boolean {
        return value.also { switch() }
    }

    public inline fun getAndSet(value: Boolean): Boolean {
        val c = this.value
        this.value = value
        return c
    }

    public inline fun on() {
        value = true
    }

    public inline fun off() {
        value = false
    }

    public inline operator fun not(): Unit = switch()
    public inline operator fun invoke(): Boolean = value


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