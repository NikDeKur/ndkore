/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.spatial

import dev.nikdekur.ndkore.ext.distanceSquared
import dev.nikdekur.ndkore.ext.mean
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlin.jvm.JvmField
import kotlin.math.sqrt

/**
 * Data class representing a vector in 3D space with double coordinates.
 *
 * @property x The x-coordinate of the vector.
 * @property y The y-coordinate of the vector.
 * @property z The z-coordinate of the vector.
 */
@Serializable(with = V3.Serializer::class)
public open class V3(
    public open val x: Double,
    public open val y: Double,
    public open val z: Double,
) : Comparable<V3> {

    /**
     * Adds the coordinates of another vector to this vector.
     *
     * @param other The vector to add.
     * @return A new vector representing the result of the addition.
     */
    public operator fun plus(other: V3): V3 = V3(x + other.x, y + other.y, z + other.z)

    /**
     * Subtracts the coordinates of another vector from this vector.
     *
     * @param other The vector to subtract.
     * @return A new vector representing the result of the subtraction.
     */
    public operator fun minus(other: V3): V3 = V3(x - other.x, y - other.y, z - other.z)

    /**
     * Multiplies the coordinates of this vector by the coordinates of another vector.
     *
     * @param other The vector to multiply by.
     * @return A new vector representing the result of the multiplication.
     */
    public operator fun times(other: V3): V3 = V3(x * other.x, y * other.y, z * other.z)

    /**
     * Multiplies the coordinates of this vector by a scalar value.
     *
     * @param other The scalar value to multiply by.
     * @return A new vector representing the result of the multiplication.
     */
    public operator fun times(other: Double): V3 = V3(x * other, y * other, z * other)

    /**
     * Divides the coordinates of this vector by the coordinates of another vector.
     *
     * @param other The vector to divide by.
     * @return A new vector representing the result of the division.
     */
    public operator fun div(other: V3): V3 = V3(x / other.x, y / other.y, z / other.z)

    /**
     * Divides the coordinates of this vector by a scalar value.
     *
     * @param other The scalar value to divide by.
     * @return A new vector representing the result of the division.
     */
    public operator fun div(other: Double): V3 = V3(x / other, y / other, z / other)

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param other The other vector.
     * @return The dot product of the two vectors.
     */
    public fun cross(other: V3): V3 {
        return V3(
            x = y * other.z - z * other.y,
            y = z * other.x - x * other.z,
            z = x * other.y - y * other.x
        )
    }

    /**
     * Calculates the squared length (magnitude) of this vector from the origin in 3D space.
     *
     * @return The squared length of this vector.
     */
    public fun lengthSquared(): Double {
        return (x * x + y * y + z * z)
    }


    /**
     * Calculates the length (magnitude) of this vector from the origin in 3D space.
     *
     * @return The length of this vector.
     */
    public fun length(): Double {
        return sqrt(lengthSquared())
    }

    /**
     * Normalizes this vector to have a length of 1, maintaining its direction.
     *
     * @return A new vector representing the normalized version of this vector.
     */
    public fun normalized(): V3 = this * (1.0 / length())


    public override fun compareTo(other: V3): Int {
        return when {
            x != other.x -> x.compareTo(other.x)
            y != other.y -> y.compareTo(other.y)
            else -> z.compareTo(other.z)
        }
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is V3) return false

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun toString(): String {
        return "V3(x=$x, y=$y, z=$z)"
    }


    public companion object {
        @JvmField
        public val ZERO: V3 = V3(0.0, 0.0, 0.0)
    }

    public object Serializer : KSerializer<V3> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("dev.nikdekur.ndkore.spatial.V3") {
            element<Double>("x")
            element<Double>("y")
            element<Double>("z")
        }

        override fun serialize(encoder: Encoder, value: V3) {
            encoder.encodeStructure(descriptor) {
                encodeDoubleElement(descriptor, 0, value.x)
                encodeDoubleElement(descriptor, 1, value.y)
                encodeDoubleElement(descriptor, 2, value.z)
            }
        }

        override fun deserialize(decoder: Decoder): V3 {
            return try {
                val stringValue = decoder.decodeString()
                parseFromString(stringValue)
            } catch (e: Exception) {
                decoder.decodeStructure(descriptor) {
                    var x = 0.0
                    var y = 0.0
                    var z = 0.0

                    while (true) {
                        when (val index = decodeElementIndex(descriptor)) {
                            0 -> x = decodeDoubleElement(descriptor, 0)
                            1 -> y = decodeDoubleElement(descriptor, 1)
                            2 -> z = decodeDoubleElement(descriptor, 2)
                            CompositeDecoder.DECODE_DONE -> break
                            else -> error("Unexpected index: $index")
                        }
                    }

                    V3(x, y, z)
                }
            }
        }

        private fun parseFromString(value: String): V3 {
            val parts = value.split(", ", ",", " ", ";", "|", ":", "\t")
            require(parts.size == 3) { "Invalid format for Point string. But was ${parts.size}. String: $value" }
            val x = parts[0].toDouble()
            val y = parts[1].toDouble()
            val z = parts[2].toDouble()
            return V3(x, y, z)
        }
    }
}


/**
 * Calculates the squared distance between this vector and another vector in 3D space.
 *
 * @param v3 The other vector.
 * @return The squared distance between the two vectors.
 */
public inline fun V3.distanceSquared(v3: V3): Double {
    return distanceSquared(x, y, z, v3.x, v3.y, v3.z)
}


/**
 * Calculates the middle vector between two given vectors.
 *
 * @receiver The vector to calculate the middle vector from.
 * @param v3 The second vector.
 * @return A new vector representing the middle vector between the two given vectors.
 */
public inline fun V3.middlePoint(v3: V3): V3 {
    return V3(mean(x, v3.x), mean(y, v3.y), mean(z, v3.z))
}


public inline fun V3.add(x: Double, y: Double, z: Double): V3 {
    return V3(this.x + x, this.y + y, this.z + z)
}

public inline fun V3.subtract(x: Double, y: Double, z: Double): V3 {
    return V3(this.x - x, this.y - y, this.z - z)
}

public inline fun V3.multiply(x: Double, y: Double, z: Double): V3 {
    return V3(this.x * x, this.y * y, this.z * z)
}

public inline fun V3.multiply(scalar: Double): V3 {
    return V3(this.x * scalar, this.y * scalar, this.z * scalar)
}

public inline fun V3.divide(x: Double, y: Double, z: Double): V3 {
    return V3(this.x / x, this.y / y, this.z / z)
}