/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.placeholder

/**
 * A functional interface for dynamically retrieving values based on a given key.
 *
 * ## Overview
 * The `Placeholder` interface is designed to handle scenarios where the list of placeholders is not known
 * in advance, and their values need to be determined dynamically at runtime based on the provided key.
 *
 * This interface is ideal for cases where placeholder values cannot be predefined and must be resolved
 * on-the-fly during execution.
 *
 * ## Key Features
 * - **Dynamic Value Resolution**: Retrieves values dynamically for keys,
 * enabling flexible and runtime-dependent behavior.
 * - **On-Demand Evaluation**: Decisions about values are made only when the key is requested, making it suitable
 *   for systems with unpredictable or variable input.
 *
 * ## Practical Use Cases
 * - **Dynamic Template Rendering**: Resolving placeholders in templates where values depend on runtime logic.
 * - **Event-Driven Systems**: Handling dynamic events or inputs that determine placeholder values.
 * - **Extensible Frameworks**: Supporting pluggable modules or dynamic configurations where values cannot
 *   be statically defined.
 *
 * ## Examples
 *
 * ### Example 1: Using a Lambda for Dynamic Resolution
 * ```kotlin
 * val dynamicPlaceholder = Placeholder { key ->
 *     when (key) {
 *         "currentTime" -> System.currentTimeMillis()
 *         "randomNumber" -> (1..100).random()
 *         else -> null
 *     }
 * }
 * println(dynamicPlaceholder.getPlaceholder("currentTime")) // Output: 1691234567890 (current timestamp)
 * ```
 *
 * ### Example 2: Integrating with Dynamic Systems
 * ```kotlin
 * val placeholder = Placeholder { key ->
 *     fetchValueFromDatabaseOrApi(key) // Custom logic to dynamically resolve values
 * }
 * println(placeholder.getPlaceholder("userName")) // Output depends on the external source
 * ```
 */
public fun interface Placeholder {

    /**
     * Dynamically retrieves the value associated with the specified key.
     *
     * @param key The key for which the value should be resolved dynamically.
     * @return The dynamically resolved value associated with the key, or `null` if no value can be determined.
     */
    public fun getPlaceholder(key: String): Any?
}