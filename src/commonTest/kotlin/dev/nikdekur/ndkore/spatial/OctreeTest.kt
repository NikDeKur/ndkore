/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.spatial

import dev.nikdekur.ndkore.ext.sqrt
import dev.nikdekur.ndkore.`interface`.Unique
import dev.nikdekur.ndkore.spatial.octree.OctreeImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object CuboidShape : Shape<CuboidBuilding> {
    override fun getMinPoint(o: CuboidBuilding): Point {
        return o.min
    }

    override fun getMaxPoint(o: CuboidBuilding): Point {
        return o.max
    }

    override fun contains(
        obj: CuboidBuilding,
        point: Point,
    ): Boolean {
        val min = obj.min
        val max = obj.max
        return point.x >= min.x && point.x <= max.x &&
                point.y >= min.y && point.y <= max.y &&
                point.z >= min.z && point.z <= max.z
    }

    override fun distanceSquared(
        from: CuboidBuilding,
        to: Point,
    ): Double {
        val dx = maxOf(0, from.min.x - to.x, to.x - from.max.x)
        val dy = maxOf(0, from.min.y - to.y, to.y - from.max.y)
        val dz = maxOf(0, from.min.z - to.z, to.z - from.max.z)
        return (dx * dx + dy * dy + dz * dz).toDouble()
    }

    override fun intersects(
        obj: CuboidBuilding,
        min: Point,
        max: Point,
    ): Boolean {
        val min2 = obj.min
        val max2 = obj.max
        return min.x <= max2.x && max.x >= min2.x &&
                min.y <= max2.y && max.y >= min2.y &&
                min.z <= max2.z && max.z >= min2.z
    }
}

object SphereShape : Shape<SphereBuilding> {
    override fun getMinPoint(o: SphereBuilding): Point {
        return o.min
    }

    override fun getMaxPoint(o: SphereBuilding): Point {
        return o.max
    }

    override fun contains(
        obj: SphereBuilding,
        point: Point,
    ): Boolean {
        val center = obj.center
        val dx = point.x - center.x
        val dy = point.y - center.y
        val dz = point.z - center.z
        return dx * dx + dy * dy + dz * dz <= obj.radiusSquared
    }

    override fun distanceSquared(
        from: SphereBuilding,
        to: Point,
    ): Double {

        val center = from.center
        val dx = to.x - center.x
        val dy = to.y - center.y
        val dz = to.z - center.z
        val distanceSquaredToCenter = dx * dx + dy * dy + dz * dz
        return if (distanceSquaredToCenter <= from.radiusSquared) {
            0.0
        } else {
            val distanceToSurface = distanceSquaredToCenter.sqrt() - from.radius
            distanceToSurface * distanceToSurface
        }
    }

    override fun intersects(
        obj: SphereBuilding,
        min: Point,
        max: Point,
    ): Boolean {
        val center = obj.center
        val dx = maxOf(min.x - center.x, center.x - max.x)
        val dy = maxOf(min.y - center.y, center.y - max.y)
        val dz = maxOf(min.z - center.z, center.z - max.z)
        val distanceSquaredToSurface = dx * dx + dy * dy + dz * dz
        return distanceSquaredToSurface <= obj.radiusSquared
    }

}

interface Building : Unique<String> {
    val min: Point
    val max: Point
}

data class CuboidBuilding(
    override val min: Point,
    override val max: Point,
    override val id: String,
) : Building

data class SphereBuilding(
    val center: Point,
    val radius: Int,
    override val id: String,
) : Building {
    override val min = Point(center.x - radius, center.y - radius, center.z - radius)
    override val max = Point(center.x + radius, center.y + radius, center.z + radius)

    val radiusSquared = radius * radius
}


@Suppress("UNCHECKED_CAST")
class OctreeTest {


    @Test
    fun testCube() {
        val octree = OctreeImpl<Building>()

        val building = CuboidBuilding(Point(0, 0, 0), Point(10, 10, 10), "10x10x10 Cube")
        octree.insert(building, CuboidShape as Shape<Building>)

        val point = Point(5, 5, 5)
        val found = octree.find(point)
        assertEquals(1, found.size)
        assertEquals(building, found.first())

        val point2 = Point(10, 10, 10)
        val found2 = octree.find(point2)
        assertEquals(1, found2.size)
        assertEquals(building, found2.first())

        val point3 = Point(15, 15, 15)
        val found3 = octree.find(point3)
        assertEquals(0, found3.size)

        val nearby1 = octree.findNearby(point3, 8.67)
        assertEquals(1, nearby1.size)
        assertEquals(building, nearby1.first())

        val region1Min = Point(0, 0, 0)
        val region1Max = Point(1, 1, 1)
        val inRegion1 = octree.findInRegion(region1Min, region1Max)
        assertEquals(1, inRegion1.size)
        assertEquals(building, inRegion1.first())

        val region2Min = Point(10, 10, 10)
        val region2Max = Point(11, 11, 11)
        val inRegion2 = octree.findInRegion(region2Min, region2Max)
        assertEquals(1, inRegion2.size)
        assertEquals(building, inRegion2.first())

        val region3Min = Point(11, 11, 11)
        val region3Max = Point(12, 12, 12)
        val inRegion3 = octree.findInRegion(region3Min, region3Max)
        assertEquals(0, inRegion3.size)
    }

    @Test
    fun testSphere() {
        val octree = OctreeImpl<Building>()

        val building = SphereBuilding(Point(10, 10, 10), 5, "Sphere")
        octree.insert(building, SphereShape as Shape<Building>)

        val point = Point(10, 10, 10)
        val found = octree.find(point)
        assertEquals(1, found.size)
        assertEquals(building, found.first())

        val point2 = Point(12, 12, 12)
        val found2 = octree.find(point2)
        assertEquals(1, found2.size)
        assertEquals(building, found2.first())

        val point3 = Point(15, 15, 15)
        val found3 = octree.find(point3)
        assertEquals(0, found3.size)

        val nearby1 = octree.findNearby(point3, 5.0)
        assertEquals(1, nearby1.size)
        assertEquals(building, nearby1.first())

        val region1Min = Point(10, 10, 10)
        val region1Max = Point(11, 11, 11)
        val inRegion1 = octree.findInRegion(region1Min, region1Max)
        assertEquals(1, inRegion1.size)
        assertEquals(building, inRegion1.first())

        val region2Min = Point(10, 10, 10)
        val region2Max = Point(11, 11, 11)
        val inRegion2 = octree.findInRegion(region2Min, region2Max)
        assertEquals(1, inRegion2.size)
        assertEquals(building, inRegion2.first())

        val region3Min = Point(13, 13, 13)
        val region3Max = Point(14, 14, 14)
        val inRegion3 = octree.findInRegion(region3Min, region3Max)
        assertEquals(0, inRegion3.size)
    }

    @Test
    fun testMultipleCuboidElements() {
        val octree = OctreeImpl<Building>()

        val building1 = CuboidBuilding(Point(0, 0, 0), Point(10, 10, 10), "Building 1")
        val building2 = CuboidBuilding(Point(5, 5, 5), Point(15, 15, 15), "Building 2")
        octree.insert(building1, CuboidShape as Shape<Building>)
        octree.insert(building2, CuboidShape as Shape<Building>)

        val point1 = Point(5, 5, 5)
        val found1 = octree.find(point1)
        assertEquals(2, found1.size)
        assertTrue(found1.contains(building1))
        assertTrue(found1.contains(building2))

        val regionMin = Point(0, 0, 0)
        val regionMax = Point(20, 20, 20)
        val inRegion = octree.findInRegion(regionMin, regionMax)
        assertEquals(2, inRegion.size)
        assertTrue(inRegion.contains(building1))
        assertTrue(inRegion.contains(building2))
    }

    @Test
    fun testMultipleSphereElements() {
        val octree = OctreeImpl<Building>()

        val sphere1 = SphereBuilding(Point(10, 10, 10), 5, "Sphere 1")
        val sphere2 = SphereBuilding(Point(15, 15, 15), 5, "Sphere 2")
        octree.insert(sphere1, SphereShape as Shape<Building>)
        octree.insert(sphere2, SphereShape as Shape<Building>)

        val point1 = Point(12, 12, 12)
        val found1 = octree.find(point1)
        assertEquals(1, found1.size)
        assertEquals(sphere1, found1.first())

        val point2 = Point(15, 15, 15)
        val found2 = octree.find(point2)
        assertEquals(1, found2.size)
        assertEquals(sphere2, found2.first())

        val nearby = octree.findNearby(Point(20, 20, 20), 17.4)
        assertEquals(2, nearby.size)
        assertTrue(nearby.contains(sphere1))
        assertTrue(nearby.contains(sphere2))
    }

    @Test
    fun testIteration() {
        val octree = OctreeImpl<Building>()

        val building1 = CuboidBuilding(Point(0, 0, 0), Point(10, 10, 10), "Building 1")
        val building2 = CuboidBuilding(Point(20, 20, 20), Point(30, 30, 30), "Building 2")
        val sphere1 = SphereBuilding(Point(5, 5, 5), 5, "Sphere 1")
        val sphere2 = SphereBuilding(Point(25, 25, 25), 5, "Sphere 2")

        octree.insert(building1, CuboidShape as Shape<Building>)
        octree.insert(building2, CuboidShape as Shape<Building>)
        octree.insert(sphere1, SphereShape as Shape<Building>)
        octree.insert(sphere2, SphereShape as Shape<Building>)

        val elements = octree.toList()
        assertEquals(4, elements.size)
        assertTrue(elements.contains(building1))
        assertTrue(elements.contains(building2))
        assertTrue(elements.contains(sphere1))
        assertTrue(elements.contains(sphere2))
    }

    @Test
    fun testCuboidAndSphereMixed() {
        val octree = OctreeImpl<Building>()

        val building = CuboidBuilding(Point(0, 0, 0), Point(10, 10, 10), "Building")
        val sphere = SphereBuilding(Point(5, 5, 5), 5, "Sphere")
        octree.insert(building, CuboidShape as Shape<Building>)
        octree.insert(sphere, SphereShape as Shape<Building>)

        val point1 = Point(5, 5, 5)
        val found1 = octree.find(point1)
        assertEquals(2, found1.size)
        assertTrue(found1.contains(building))
        assertTrue(found1.contains(sphere))

        val nearby = octree.findNearby(Point(4, 4, 4), 5.0)
        assertEquals(2, nearby.size)
        assertTrue(nearby.contains(building))
        assertTrue(nearby.contains(sphere))

        val regionMin = Point(0, 0, 0)
        val regionMax = Point(5, 5, 5)
        val inRegion = octree.findInRegion(regionMin, regionMax)
        assertEquals(2, inRegion.size)
        assertTrue(inRegion.contains(building))
        assertTrue(inRegion.contains(sphere))
    }

    @Test
    fun testNodesIteration() {
        val octree = OctreeImpl<Building>()

        val building1 = CuboidBuilding(Point(0, 0, 0), Point(10, 10, 10), "Building 1")
        val building2 = CuboidBuilding(Point(20, 20, 20), Point(30, 30, 30), "Building 2")
        val sphere1 = SphereBuilding(Point(5, 5, 5), 5, "Sphere 1")
        val sphere2 = SphereBuilding(Point(25, 25, 25), 5, "Sphere 2")

        octree.insert(building1, CuboidShape as Shape<Building>)
        octree.insert(building2, CuboidShape as Shape<Building>)
        octree.insert(sphere1, SphereShape as Shape<Building>)
        octree.insert(sphere2, SphereShape as Shape<Building>)

        val nodes = octree.iteratorNode().asSequence().toList()
        assertEquals(4, nodes.size)
        assertTrue(nodes.any { it.data == building1 })
        assertTrue(nodes.any { it.data == building2 })
        assertTrue(nodes.any { it.data == sphere1 })
        assertTrue(nodes.any { it.data == sphere2 })
    }
}