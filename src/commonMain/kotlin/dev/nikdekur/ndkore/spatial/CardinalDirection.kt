/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.spatial

/**
 * # Cardinal Direction
 *
 * An enum representing the four primary cardinal directions, each associated with a specific azimuth value.
 *
 * This enum provides functionality to retrieve a direction based on various parameters:
 * - String name
 * - Index
 * - Azimuth value
 *
 * ### Example usage:
 * ```
 * val direction = CardinalDirection.getByString("north") // Returns CardinalDirection.NORTH
 * val directionByIndex = CardinalDirection.getByIndex(1) // Returns CardinalDirection.EAST
 * val directionByAzimuth = CardinalDirection.getByAzimuth(85) // Returns CardinalDirection.EAST
 * ```
 *
 * @property azimuth The azimuth value corresponding to the cardinal direction, in degrees.
 */
public enum class CardinalDirection(
    val azimuth: Int
) {
    /** North direction with an azimuth of 0 degrees. */
    NORTH(0),

    /** East direction with an azimuth of 90 degrees. */
    EAST(90),

    /** South direction with an azimuth of 180 degrees. */
    SOUTH(180),

    /** West direction with an azimuth of 270 degrees. */
    WEST(270);

    public companion object {
        /**
         * Retrieves a [CardinalDirection] based on the provided string name.
         *
         * The name is case-insensitive.
         *
         * @param name The name of the cardinal direction.
         * @return The corresponding [CardinalDirection], or null if no match is found.
         *
         * Example:
         * ```
         * val direction = CardinalDirection.getByString("north") // Returns CardinalDirection.NORTH
         * ```
         */
        public fun getByString(name: String): CardinalDirection? {
            return enumValueOf<CardinalDirection>(name.uppercase())
        }

        /**
         * Retrieves a [CardinalDirection] based on the provided index.
         *
         * The index corresponds to the ordinal value of the enum constants (0-based).
         *
         * @param index The index of the cardinal direction.
         * @return The corresponding [CardinalDirection], or null if the index is out of bounds.
         *
         * Example:
         * ```
         * val direction = CardinalDirection.getByIndex(1) // Returns CardinalDirection.EAST
         * ```
         */
        public fun getByIndex(index: Int): CardinalDirection? {
            return if (index in 0 until entries.size) entries[index] else null
        }

        /**
         * Retrieves a [CardinalDirection] that is closest to the provided azimuth value.
         *
         * @param azimuth The azimuth value in degrees.
         * @return The [CardinalDirection] closest to the provided azimuth.
         *
         * Example:
         * ```
         * val direction = CardinalDirection.getByAzimuth(85) // Returns CardinalDirection.EAST
         * ```
         */
        public fun getByAzimuth(azimuth: Int): CardinalDirection {
            return when {
                azimuth < 45 -> NORTH
                azimuth < 135 -> EAST
                azimuth < 225 -> SOUTH
                azimuth < 315 -> WEST
                else -> NORTH
            }
        }
    }
}
