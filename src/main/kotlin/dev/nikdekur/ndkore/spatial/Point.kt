/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.spatial

/**
 * Data class representing a point in 3D space with integer coordinates.
 *
 * @property x The x-coordinate of the point.
 * @property y The y-coordinate of the point.
 * @property z The z-coordinate of the point.
 */
data class Point(var x: Int, var y: Int, var z: Int) : Cloneable, Comparable<Point> {

    /**
     * Creates and returns a copy of this point.
     *
     * @return A clone of this point.
     */
    override fun clone() = Point(x, y, z)

    /**
     * Adds the coordinates of another point to this point.
     *
     * @param other The point to add.
     * @return A new point representing the result of the addition.
     */
    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y, z + other.z)

    /**
     * Subtracts the coordinates of another point from this point.
     *
     * @param other The point to subtract.
     * @return A new point representing the result of the subtraction.
     */
    operator fun minus(other: Point): Point = Point(x - other.x, y - other.y, z - other.z)

    /**
     * Multiplies the coordinates of this point by a scalar value.
     *
     * @param other The scalar value to multiply by.
     * @return A new point representing the result of the multiplication.
     */
    operator fun times(other: Int): Point = Point(x * other, y * other, z * other)

    /**
     * Divides the coordinates of this point by a scalar value.
     *
     * @param other The scalar value to divide by.
     * @return A new point representing the result of the division.
     */
    operator fun div(other: Int): Point = Point(x / other, y / other, z / other)

    /**
     * Calculates the squared length (magnitude) of this point from the origin in 3D space.
     *
     * @return The squared length of this point.
     */
    fun lengthSquared(): Int {
        return (x * x + y * y + z * z)
    }

    /**
     * Calculates the squared length (magnitude) of this point from the origin in 2D space (ignoring the y-coordinate).
     *
     * @return The squared length of this point in 2D.
     */
    fun lengthSquared2D(): Int {
        return (x * x + z * z)
    }

    /**
     * Calculates the squared distance between this point and another point in 3D space.
     *
     * @param point The other point.
     * @return The squared distance between the two points.
     */
    inline fun distanceSquared(point: Point): Double {
        return distanceSquared(x, y, z, point.x, point.y, point.z)
    }

    override fun compareTo(other: Point): Int {
        return when {
            x != other.x -> x - other.x
            y != other.y -> y - other.y
            else -> z - other.z
        }
    }

    companion object {

        @JvmField
        val ZERO = Point(0, 0, 0)


        /**
         * Calculates the middle point between two given points.
         *
         * @param point1 The first point.
         * @param point2 The second point.
         * @return A new point representing the middle point between the two given points.
         */
        inline fun middlePoint(point1: Point, point2: Point): Point {
            return Point((point1.x + point2.x) / 2, (point1.y + point2.y) / 2, (point1.z + point2.z) / 2)
        }
    }
}

/**
 * Calculates the squared distance between two points given by their individual coordinates in 3D space.
 *
 * @param x1 The x-coordinate of the first point.
 * @param y1 The y-coordinate of the first point.
 * @param z1 The z-coordinate of the first point.
 * @param x2 The x-coordinate of the second point.
 * @param y2 The y-coordinate of the second point.
 * @param z2 The z-coordinate of the second point.
 * @return The squared distance between the two points.
 */
inline fun distanceSquared(
    x1: Int, y1: Int, z1: Int,
    x2: Int, y2: Int, z2: Int,
): Double {
    val a = x1 - x2
    val b = y1 - y2
    val c = z1 - z2
    return (a * a + b * b + c * c).toDouble()
}