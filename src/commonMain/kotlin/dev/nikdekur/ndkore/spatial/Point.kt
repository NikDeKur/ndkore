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

/**
 * Data class representing a point in 3D space with integer coordinates.
 *
 * @property x The x-coordinate of the point.
 * @property y The y-coordinate of the point.
 * @property z The z-coordinate of the point.
 */
public interface Point : Comparable<Point> {

    public val x: Double
    public val y: Double
    public val z: Double

    /**
     * Adds the coordinates of another point to this point.
     *
     * @param other The point to add.
     * @return A new point representing the result of the addition.
     */
    public operator fun plus(other: Point): Point = SimplePoint(x + other.x, y + other.y, z + other.z)

    /**
     * Subtracts the coordinates of another point from this point.
     *
     * @param other The point to subtract.
     * @return A new point representing the result of the subtraction.
     */
    public operator fun minus(other: Point): Point = SimplePoint(x - other.x, y - other.y, z - other.z)

    /**
     * Multiplies the coordinates of this point by a scalar value.
     *
     * @param other The scalar value to multiply by.
     * @return A new point representing the result of the multiplication.
     */
    public operator fun times(other: Double): Point = SimplePoint(x * other, y * other, z * other)

    /**
     * Divides the coordinates of this point by a scalar value.
     *
     * @param other The scalar value to divide by.
     * @return A new point representing the result of the division.
     */
    public operator fun div(other: Double): Point = SimplePoint(x / other, y / other, z / other)

    /**
     * Calculates the squared length (magnitude) of this point from the origin in 3D space.
     *
     * @return The squared length of this point.
     */
    public fun lengthSquared(): Double {
        return (x * x + y * y + z * z)
    }


    public override fun compareTo(other: Point): Int {
        return when {
            x != other.x -> x.compareTo(other.x)
            y != other.y -> y.compareTo(other.y)
            else -> z.compareTo(other.z)
        }
    }


    public companion object {

        @JvmField
        public val ZERO: Point = SimplePoint(0.0, 0.0, 0.0)
    }
}

public fun Point(
    x: Double,
    y: Double,
    z: Double
): Point = SimplePoint(x, y, z)

@Serializable(with = SimplePoint.Serializer::class)
public data class SimplePoint(
    override val x: Double,
    override val y: Double,
    override val z: Double,
) : Point {

    public object Serializer : KSerializer<SimplePoint> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SimplePoint") {
            element<Double>("x")
            element<Double>("y")
            element<Double>("z")
        }

        override fun serialize(encoder: Encoder, value: SimplePoint) {
            encoder.encodeStructure(descriptor) {
                encodeDoubleElement(descriptor, 0, value.x)
                encodeDoubleElement(descriptor, 1, value.y)
                encodeDoubleElement(descriptor, 2, value.z)
            }
        }

        override fun deserialize(decoder: Decoder): SimplePoint {
            return try {
                val stringValue = decoder.decodeString()
                parseFromString(stringValue)
            } catch (e: Exception) {
                e.printStackTrace()
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

                    SimplePoint(x, y, z)
                }
            }
        }

        private fun parseFromString(value: String): SimplePoint {
            val parts = value.split(", ", ",", " ", ";", "|", ":", "\t")
            require(parts.size == 3) { "Invalid format for Point string. But was ${parts.size}. String: $value" }
            val x = parts[0].toDouble()
            val y = parts[1].toDouble()
            val z = parts[2].toDouble()
            return SimplePoint(x, y, z)
        }
    }
}


/**
 * Calculates the squared distance between this point and another point in 3D space.
 *
 * @param point The other point.
 * @return The squared distance between the two points.
 */
public inline fun Point.distanceSquared(point: Point): Double {
    return distanceSquared(x, y, z, point.x, point.y, point.z)
}


/**
 * Calculates the middle point between two given points.
 *
 * @receiver The point to calculate the middle point from.
 * @param point The second point.
 * @return A new point representing the middle point between the two given points.
 */
public inline fun Point.middlePoint(point: Point): Point {
    return Point(mean(x, point.x), mean(y, point.y), mean(z, point.z))
}


public inline fun Point.add(x: Double, y: Double, z: Double): Point {
    return Point(this.x + x, this.y + y, this.z + z)
}

public inline fun Point.subtract(x: Double, y: Double, z: Double): Point {
    return Point(this.x - x, this.y - y, this.z - z)
}

public inline fun Point.multiply(x: Double, y: Double, z: Double): Point {
    return Point(this.x * x, this.y * y, this.z * z)
}

public inline fun Point.divide(x: Double, y: Double, z: Double): Point {
    return Point(this.x / x, this.y / y, this.z / z)
}