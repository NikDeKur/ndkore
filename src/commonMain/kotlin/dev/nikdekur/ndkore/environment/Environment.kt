/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.environment

import kotlin.apply
import kotlin.collections.set

/**
 * Represents the environment of the application.
 *
 * The boot environment is a key-value store that contains
 * the configuration of the application at startup.
 */
public interface Environment {

    /**
     * Gets the value of the specified key.
     *
     * @param key the key
     * @return the value, or `null` if the key is not present
     */
    public fun getValue(key: String): String?

    /**
     * Request a value to be entered.
     *
     * @param key the key shortly describing the value
     * @return the value, or `null` if the value can't be requested
     */
    public fun requestValue(key: String, description: String): String?

    public object Empty : Environment {
        override fun getValue(key: String): String? = null
        override fun requestValue(key: String, description: String): String? = null
    }
}

public class EnvironmentBuilder {
    private val values = mutableMapOf<String, String>()
    private val requests = mutableMapOf<String, String>()

    public fun value(key: String, value: String) {
        values[key] = value
    }

    public fun request(key: String, value: String) {
        requests[key] = value
    }

    public fun build(): Environment {
        return object : Environment {
            override fun getValue(key: String) = values[key]
            override fun requestValue(key: String, description: String) = requests[key]
        }
    }
}

public inline fun environment(block: EnvironmentBuilder.() -> Unit): Environment {
    return EnvironmentBuilder().apply(block).build()
}