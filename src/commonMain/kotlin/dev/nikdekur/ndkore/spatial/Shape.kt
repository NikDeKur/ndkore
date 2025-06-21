/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.spatial

import dev.nikdekur.ndkore.ext.pow
import dev.nikdekur.ndkore.ext.sqrt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


public interface Shape {
    public val min: Point
    public val max: Point

    public val center: Point
        get() = min.middlePoint(max)

    public fun contains(point: Point): Boolean
    public fun distanceSquared(to: Point): Double
    public fun intersects(min: Point, max: Point): Boolean
}


public interface CuboidShape : Shape {
    override fun contains(point: Point): Boolean {
        return point.x >= min.x && point.x <= max.x &&
                point.y >= min.y && point.y <= max.y &&
                point.z >= min.z && point.z <= max.z
    }

    override fun distanceSquared(to: Point): Double {
        val dx = maxOf(0.0, min.x - to.x, to.x - max.x)
        val dy = maxOf(0.0, min.y - to.y, to.y - max.y)
        val dz = maxOf(0.0, min.z - to.z, to.z - max.z)
        return (dx * dx + dy * dy + dz * dz)
    }

    override fun intersects(min: Point, max: Point): Boolean {
        val overlapX = this.min.x <= max.x && this.max.x >= min.x
        val overlapY = this.min.y <= max.y && this.max.y >= min.y
        val overlapZ = this.min.z <= max.z && this.max.z >= min.z

        return overlapX && overlapY && overlapZ
    }
}

public interface ExclusiveCuboidShape : Shape {

    override fun contains(point: Point): Boolean {
        return point.x >= min.x && point.x < max.x &&
                point.y >= min.y && point.y < max.y &&
                point.z >= min.z && point.z < max.z
    }

    override fun distanceSquared(to: Point): Double {
        val clampedX = to.x.coerceIn(min.x, max.x - 1e-9)
        val clampedY = to.y.coerceIn(min.y, max.y - 1e-9)
        val clampedZ = to.z.coerceIn(min.z, max.z - 1e-9)

        val dx = clampedX - to.x
        val dy = clampedY - to.y
        val dz = clampedZ - to.z
        return dx * dx + dy * dy + dz * dz
    }

    override fun intersects(min: Point, max: Point): Boolean {
        return this.min.x < max.x && this.max.x > min.x &&
                this.min.y < max.y && this.max.y > min.y &&
                this.min.z < max.z && this.max.z > min.z
    }
}


public interface CircleShape : Shape {
    public val height: Double
    public val radius: Double
    abstract override val center: Point

    override val min: Point
        get() = Point(center.x - radius, center.y, center.z - radius)

    override val max: Point
        get() = Point(center.x + radius, center.y + height, center.z + radius)

    override fun contains(point: Point): Boolean {
        val dx = center.x - point.x
        val dz = center.z - point.z
        return dx * dx + dz * dz <= radius.pow() && point.y in center.y..(center.y + height)
    }

    override fun distanceSquared(to: Point): Double {
        val dx = to.x - center.x
        val dz = to.z - center.z
        val horizontalDistanceSquared = dx * dx + dz * dz
        val horizontalDistance = horizontalDistanceSquared.sqrt()
        val verticalDistance = when {
            to.y < center.y -> center.y - to.y
            to.y > center.y + height -> to.y - (center.y + height)
            else -> 0.0
        }
        val distSquared = verticalDistance.pow()
        return if (horizontalDistance <= radius)
            distSquared
        else
            horizontalDistanceSquared + distSquared
    }

    override fun intersects(min: Point, max: Point): Boolean {
        val closestX = minOf(max.x, maxOf(min.x, center.x))
        val closestZ = minOf(max.z, maxOf(min.z, center.z))
        val dx = closestX - center.x
        val dz = closestZ - center.z
        return dx * dx + dz * dz <= radius.pow() && center.y + height >= min.y && center.y <= max.y
    }
}


public interface SphereShape : Shape {
    public val radius: Double
    abstract override val center: Point

    override val min: Point
        get() = Point(center.x - radius, center.y - radius, center.z - radius)

    override val max: Point
        get() = Point(center.x + radius, center.y + radius, center.z + radius)

    override fun contains(point: Point): Boolean {
        return center.distanceSquared(point) <= radius.pow()
    }

    override fun distanceSquared(to: Point): Double {
        val dx = to.x - center.x
        val dy = to.y - center.y
        val dz = to.z - center.z
        val distanceSquaredToCenter = dx * dx + dy * dy + dz * dz
        return if (distanceSquaredToCenter <= radius.pow()) {
            0.0
        } else {
            (distanceSquaredToCenter.sqrt() - radius).pow()
        }
    }

    override fun intersects(min: Point, max: Point): Boolean {
        val dx = maxOf(min.x - center.x, center.x - max.x)
        val dy = maxOf(min.y - center.y, center.y - max.y)
        val dz = maxOf(min.z - center.z, center.z - max.z)
        val distanceSquaredToSurface = dx * dx + dy * dy + dz * dz
        return distanceSquaredToSurface <= radius.pow()
    }
}


@Serializable
@SerialName("cuboid")
public data class CuboidShapeData(
    public override val min: SimplePoint,
    public override val max: SimplePoint,
) : CuboidShape


@Serializable
@SerialName("circle")
public data class CircleShapeData(
    public override val center: SimplePoint,
    public override val radius: Double,
    public override val height: Double,
) : CircleShape


@Serializable
@SerialName("sphere")
public data class SphereShapeData(
    public override val center: SimplePoint,
    public override val radius: Double,
) : SphereShape