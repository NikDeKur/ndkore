package dev.nikdekur.ndkore.spatial

import kotlin.math.absoluteValue

enum class CardinalDirection(val azimuth: Int) {
    NORTH(0),
    EAST(90),
    SOUTH(180),
    WEST(270);

    companion object {
        fun getByString(name: String): CardinalDirection? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }

        fun getByIndex(index: Int): CardinalDirection? {

            return if (index in 0 until entries.size) entries[index] else null
        }

        fun getByAzimuth(azimuth: Int): CardinalDirection {
            val adjustedAzimuth = (azimuth + 360) % 360 // Normalize to [0, 360) range
            return entries.minByOrNull {
                (it.azimuth - adjustedAzimuth).absoluteValue
            } ?: NORTH
        }
    }
}