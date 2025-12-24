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
import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.math.min


public interface Shape {
    public val min: V3
    public val max: V3

    public val center: V3
        get() = min.middlePoint(max)

    public fun contains(v3: V3): Boolean
    public fun distanceSquared(to: V3): Double
    public fun intersects(min: V3, max: V3): Boolean

    public fun rotated(rotation: Quaternion, around: V3 = V3.ZERO): Shape
    public fun translated(offset: V3): Shape
    public fun scaled(factor: Double): Shape
}


@Serializable
public open class CuboidShape(
    override val min: V3,
    override val max: V3
) : Shape {

    override fun contains(v3: V3): Boolean {
        return v3.x >= min.x && v3.x <= max.x &&
                v3.y >= min.y && v3.y <= max.y &&
                v3.z >= min.z && v3.z <= max.z
    }

    override fun distanceSquared(to: V3): Double {
        val dx = maxOf(0.0, min.x - to.x, to.x - max.x)
        val dy = maxOf(0.0, min.y - to.y, to.y - max.y)
        val dz = maxOf(0.0, min.z - to.z, to.z - max.z)
        return (dx * dx + dy * dy + dz * dz)
    }

    override fun intersects(min: V3, max: V3): Boolean {
        val overlapX = this.min.x <= max.x && this.max.x >= min.x
        val overlapY = this.min.y <= max.y && this.max.y >= min.y
        val overlapZ = this.min.z <= max.z && this.max.z >= min.z

        return overlapX && overlapY && overlapZ
    }

    override fun translated(offset: V3): Shape {
        return CuboidShape(
            min = V3(min.x + offset.x, min.y + offset.y, min.z + offset.z),
            max = V3(max.x + offset.x, max.y + offset.y, max.z + offset.z)
        )
    }

    override fun rotated(rotation: Quaternion, around: V3): Shape {
        // Get all 8 vertices of the cuboid
        val vertices = listOf(
            V3(min.x, min.y, min.z),
            V3(max.x, min.y, min.z),
            V3(min.x, max.y, min.z),
            V3(max.x, max.y, min.z),
            V3(min.x, min.y, max.z),
            V3(max.x, min.y, max.z),
            V3(min.x, max.y, max.z),
            V3(max.x, max.y, max.z)
        )

        // Rotate each vertex around the 'around' point
        val rotatedVertices = vertices.map { rotation * (it - around) + around }

        // Compute new min and max in one pass
        val first = rotatedVertices.first()
        val (newMin, newMax) = rotatedVertices.drop(1).fold(first to first) { (curMin, curMax), v ->
            V3(min(curMin.x, v.x), min(curMin.y, v.y), min(curMin.z, v.z)) to
                    V3(max(curMax.x, v.x), max(curMax.y, v.y), max(curMax.z, v.z))
        }

        return CuboidShape(newMin, newMax)
    }

    override fun scaled(factor: Double): Shape {
        // Scale min/max relative to the center
        val c = center
        val newMin = V3(
            c.x + (min.x - c.x) * factor,
            c.y + (min.y - c.y) * factor,
            c.z + (min.z - c.z) * factor
        )
        val newMax = V3(
            c.x + (max.x - c.x) * factor,
            c.y + (max.y - c.y) * factor,
            c.z + (max.z - c.z) * factor
        )
        return CuboidShape(newMin, newMax)
    }

}

@Serializable
public open class ExclusiveCuboidShape(
    override val min: V3,
    override val max: V3
) : Shape {

    override fun contains(v3: V3): Boolean {
        return v3.x >= min.x && v3.x < max.x &&
                v3.y >= min.y && v3.y < max.y &&
                v3.z >= min.z && v3.z < max.z
    }

    override fun distanceSquared(to: V3): Double {
        val clampedX = to.x.coerceIn(min.x, max.x - 1e-9)
        val clampedY = to.y.coerceIn(min.y, max.y - 1e-9)
        val clampedZ = to.z.coerceIn(min.z, max.z - 1e-9)

        val dx = clampedX - to.x
        val dy = clampedY - to.y
        val dz = clampedZ - to.z
        return dx * dx + dy * dy + dz * dz
    }

    override fun intersects(min: V3, max: V3): Boolean {
        return this.min.x < max.x && this.max.x > min.x &&
                this.min.y < max.y && this.max.y > min.y &&
                this.min.z < max.z && this.max.z > min.z
    }

    override fun translated(offset: V3): Shape {
        return CuboidShape(
            min = V3(min.x + offset.x, min.y + offset.y, min.z + offset.z),
            max = V3(max.x + offset.x, max.y + offset.y, max.z + offset.z)
        )
    }

    override fun rotated(rotation: Quaternion, around: V3): Shape {
        // Get all 8 vertices of the cuboid
        val vertices = listOf(
            V3(min.x, min.y, min.z),
            V3(max.x, min.y, min.z),
            V3(min.x, max.y, min.z),
            V3(max.x, max.y, min.z),
            V3(min.x, min.y, max.z),
            V3(max.x, min.y, max.z),
            V3(min.x, max.y, max.z),
            V3(max.x, max.y, max.z)
        )

        // Rotate each vertex around the 'around' point
        val rotatedVertices = vertices.map { rotation * (it - around) + around }

        // Compute new min and max in one pass
        val first = rotatedVertices.first()
        val (newMin, newMax) = rotatedVertices.drop(1).fold(first to first) { (curMin, curMax), v ->
            V3(min(curMin.x, v.x), min(curMin.y, v.y), min(curMin.z, v.z)) to
                    V3(max(curMax.x, v.x), max(curMax.y, v.y), max(curMax.z, v.z))
        }

        return CuboidShape(newMin, newMax)
    }

    override fun scaled(factor: Double): Shape {
        // Scale min/max relative to the center
        val c = center
        val newMin = V3(
            c.x + (min.x - c.x) * factor,
            c.y + (min.y - c.y) * factor,
            c.z + (min.z - c.z) * factor
        )
        val newMax = V3(
            c.x + (max.x - c.x) * factor,
            c.y + (max.y - c.y) * factor,
            c.z + (max.z - c.z) * factor
        )
        return CuboidShape(newMin, newMax)
    }

}


@Serializable
public open class CylinderShape(
    public val downCenter: V3,
    public val radius: Double,
    public val height: Double
) : Shape {
    override val min: V3
        get() = V3(downCenter.x - radius, downCenter.y, downCenter.z - radius)

    override val max: V3
        get() = V3(downCenter.x + radius, downCenter.y + height, downCenter.z + radius)

    override fun contains(v3: V3): Boolean {
        val dx = downCenter.x - v3.x
        val dz = downCenter.z - v3.z
        return dx * dx + dz * dz <= radius.pow() && v3.y in downCenter.y..(downCenter.y + height)
    }

    override fun distanceSquared(to: V3): Double {
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

    override fun intersects(min: V3, max: V3): Boolean {
        val closestX = minOf(max.x, maxOf(min.x, downCenter.x))
        val closestZ = minOf(max.z, maxOf(min.z, downCenter.z))
        val dx = closestX - downCenter.x
        val dz = closestZ - downCenter.z
        return dx * dx + dz * dz <= radius.pow() && downCenter.y + height >= min.y && downCenter.y <= max.y
    }

    override fun translated(offset: V3): Shape {
        return CylinderShape(
            downCenter = downCenter + offset,
            radius = radius,
            height = height
        )
    }

    override fun rotated(rotation: Quaternion, around: V3): Shape {
        val newDownCenter = rotation * (downCenter - around) + around
        return CylinderShape(
            downCenter = newDownCenter,
            radius = radius,
            height = height
        )
    }

    override fun scaled(factor: Double): Shape {
        return CylinderShape(
            downCenter = downCenter,
            radius = radius * factor,
            height = height * factor
        )
    }

}


@Serializable
public open class SphereShape(
    override val center: V3,
    public val radius: Double
) : Shape {

    override val min: V3
        get() = V3(center.x - radius, center.y - radius, center.z - radius)

    override val max: V3
        get() = V3(center.x + radius, center.y + radius, center.z + radius)

    override fun contains(v3: V3): Boolean {
        return center.distanceSquared(v3) <= radius.pow()
    }

    override fun distanceSquared(to: V3): Double {
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

    override fun intersects(min: V3, max: V3): Boolean {
        val closestX = center.x.coerceIn(min.x, max.x)
        val closestY = center.y.coerceIn(min.y, max.y)
        val closestZ = center.z.coerceIn(min.z, max.z)

        val dx = center.x - closestX
        val dy = center.y - closestY
        val dz = center.z - closestZ

        return dx * dx + dy * dy + dz * dz <= radius * radius
    }

    override fun translated(offset: V3): Shape {
        return SphereShape(
            center = center + offset,
            radius = radius
        )
    }

    override fun rotated(rotation: Quaternion, around: V3): Shape {
        val newCenter = rotation * (center - around) + around
        return SphereShape(
            center = newCenter,
            radius = radius
        )
    }

    override fun scaled(factor: Double): Shape {
        return SphereShape(
            center = center,
            radius = radius * factor
        )
    }

}

@Serializable
public class PointsShape(
    public val points: List<V3>
) : Shape {

    override val min: V3
    override val max: V3

    // Precomputed set for fast membership checks in contains().
    // Single allocation at construction, no changes afterward.
    private val pointsSet: Set<V3>

    init {
        require(points.isNotEmpty()) { "PointsShape requires at least one point" }

        val first = points[0]
        var minX = first.x
        var minY = first.y
        var minZ = first.z
        var maxX = first.x
        var maxY = first.y
        var maxZ = first.z

        // Single pass to compute bounding box.
        val size = points.size
        for (i in 1 until size) {
            val p = points[i]
            val px = p.x
            val py = p.y
            val pz = p.z

            if (px < minX) minX = px
            if (py < minY) minY = py
            if (pz < minZ) minZ = pz

            if (px > maxX) maxX = px
            if (py > maxY) maxY = py
            if (pz > maxZ) maxZ = pz
        }

        min = V3(minX, minY, minZ)
        max = V3(maxX, maxY, maxZ)

        // HashSet is cheap to build once and gives O(1) contains on hot path.
        pointsSet = HashSet<V3>(points.size).also { set ->
            for (i in 0 until size) {
                set.add(points[i])
            }
        }
    }

    override fun contains(v3: V3): Boolean {
        return pointsSet.contains(v3)
    }

    override fun distanceSquared(to: V3): Double {
        var best = Double.POSITIVE_INFINITY

        val size = points.size
        for (i in 0 until size) {
            val d2 = points[i].distanceSquared(to)
            if (d2 < best) {
                if (d2 == 0.0) {
                    // Early exit: cannot get better than zero.
                    return 0.0
                }
                best = d2
            }
        }

        return best
    }

    override fun intersects(min: V3, max: V3): Boolean {
        // 1) Fast reject by AABB of this shape.
        if (this.max.x < min.x || this.min.x > max.x) return false
        if (this.max.y < min.y || this.min.y > max.y) return false
        if (this.max.z < min.z || this.min.z > max.z) return false

        // 2) Check if at least one point lies inside the given AABB.
        val size = points.size
        val minX = min.x
        val minY = min.y
        val minZ = min.z
        val maxX = max.x
        val maxY = max.y
        val maxZ = max.z

        for (i in 0 until size) {
            val p = points[i]
            val px = p.x
            val py = p.y
            val pz = p.z

            if (px in minX..maxX &&
                py in minY..maxY &&
                pz in minZ..maxZ
            ) {
                return true
            }
        }

        return false
    }

    override fun translated(offset: V3): Shape {
        val size = points.size
        val dx = offset.x
        val dy = offset.y
        val dz = offset.z

        val newPoints = ArrayList<V3>(size)
        for (i in 0 until size) {
            val p = points[i]
            newPoints.add(V3(p.x + dx, p.y + dy, p.z + dz))
        }

        return PointsShape(newPoints)
    }

    override fun rotated(rotation: Quaternion, around: V3): Shape {
        // We keep the standard pattern: rotation * (p - around) + around.
        // This part is not on the hot path in most use cases; correctness first.
        val size = points.size
        val newPoints = ArrayList<V3>(size)

        for (i in 0 until size) {
            val p = points[i]
            // (p - around)
            val local = V3(p.x - around.x, p.y - around.y, p.z - around.z)
            // rotation * local
            val rotatedLocal = rotation * local
            // + around
            newPoints.add(rotatedLocal + around)
        }

        return PointsShape(newPoints)
    }

    override fun scaled(factor: Double): Shape {
        // Scale points around the geometric center (same semantics as CuboidShape).
        val c = center
        val cx = c.x
        val cy = c.y
        val cz = c.z

        val size = points.size
        val newPoints = ArrayList<V3>(size)

        for (i in 0 until size) {
            val p = points[i]
            val px = p.x
            val py = p.y
            val pz = p.z

            // (p - c) * factor + c, but expanded to avoid temporary V3 allocations.
            val nx = cx + (px - cx) * factor
            val ny = cy + (py - cy) * factor
            val nz = cz + (pz - cz) * factor

            newPoints.add(V3(nx, ny, nz))
        }

        return PointsShape(newPoints)
    }
}
