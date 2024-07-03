/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

package dev.nikdekur.ndkore.events

/**
 * Represents an event's priority in execution.
 *
 *
 * Listeners with lower priority are called first
 * will listeners with higher priority are called last.
 *
 *
 * Listeners are called in the following order:
 * [.LOWEST] -- [.LOW] -- [.NORMAL] -- [.HIGH] -- [.HIGHEST] -- [.MONITOR]
 */
enum class EventPriority(val slot: Int) {
    /**
     * Event call is of very low importance and should be run first, to allow
     * other plugins to further customise the outcome
     */
    LOWEST(0),

    /**
     * Event call is of low importance
     */
    LOW(1),

    /**
     * Event call is neither important nor unimportant, and may be run
     * normally
     */
    NORMAL(2),

    /**
     * Event call is of high importance
     */
    HIGH(3),

    /**
     * Event call is critical and must have the final say in what happens
     * to the event
     */
    HIGHEST(4),

    /**
     * Event is listened to purely for monitoring the outcome of an event.
     *
     *
     * No modifications to the event should be made under this priority
     */
    MONITOR(5)
}