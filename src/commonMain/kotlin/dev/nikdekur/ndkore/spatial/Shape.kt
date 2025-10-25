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

    public fun offset(point: Point): Shape
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


public interface CylinderShape : Shape {
    public val height: Double
    public val radius: Double
    public val downCenter: Point

    override val min: Point
        get() = Point(downCenter.x - radius, downCenter.y, downCenter.z - radius)

    override val max: Point
        get() = Point(downCenter.x + radius, downCenter.y + height, downCenter.z + radius)

    override fun contains(point: Point): Boolean {
        val dx = downCenter.x - point.x
        val dz = downCenter.z - point.z
        return dx * dx + dz * dz <= radius.pow() && point.y in downCenter.y..(downCenter.y + height)
    }

    override fun distanceSquared(to: Point): Double {
        val dx = to.x - downCenter.x
        val dz = to.z - downCenter.z
        val horizontalDistanceSquared = dx * dx + dz * dz
        val horizontalDistance = horizontalDistanceSquared.sqrt()
        val verticalDistance = when {
            to.y < downCenter.y -> downCenter.y - to.y
            to.y > downCenter.y + height -> to.y - (downCenter.y + height)
            else -> 0.0
        }
        val distSquared = verticalDistance.pow()
        return if (horizontalDistance <= radius)
            distSquared
        else
            horizontalDistanceSquared + distSquared
    }

    override fun intersects(min: Point, max: Point): Boolean {
        val closestX = minOf(max.x, maxOf(min.x, downCenter.x))
        val closestZ = minOf(max.z, maxOf(min.z, downCenter.z))
        val dx = closestX - downCenter.x
        val dz = closestZ - downCenter.z
        return dx * dx + dz * dz <= radius.pow() && downCenter.y + height >= min.y && downCenter.y <= max.y
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
    public override val min: Point,
    public override val max: Point,
) : CuboidShape {
    override fun offset(point: Point): Shape {
        return CuboidShapeData(
            min = Point(min.x + point.x, min.y + point.y, min.z + point.z),
            max = Point(max.x + point.x, max.y + point.y, max.z + point.z)
        )
    }
}


@Serializable
@SerialName("cylinder")
public data class CylinderShapeData(
    @SerialName("center")
    public override val downCenter: Point,
    public override val radius: Double,
    public override val height: Double,
) : CylinderShape {
    override fun offset(point: Point): Shape {
        return CylinderShapeData(
            downCenter = Point(downCenter.x + point.x, downCenter.y + point.y, downCenter.z + point.z),
            radius = radius,
            height = height
        )
    }
}


@Serializable
@SerialName("sphere")
public data class SphereShapeData(
    public override val center: Point,
    public override val radius: Double,
) : SphereShape {
    override fun offset(point: Point): Shape {
        return SphereShapeData(
            center = Point(center.x + point.x, center.y + point.y, center.z + point.z),
            radius = radius
        )
    }
}