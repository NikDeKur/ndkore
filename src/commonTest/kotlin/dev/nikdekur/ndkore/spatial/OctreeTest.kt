/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.spatial

import dev.nikdekur.ndkore.`interface`.Unique
import dev.nikdekur.ndkore.spatial.octree.OctreeImpl
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


interface Building : Shape, Unique<String>

data class CuboidBuilding(
    override val min: Point,
    override val max: Point,
    override val id: String,
) : Building, CuboidShape

data class SphereBuilding(
    override val center: Point,
    override val radius: Double,
    override val id: String,
) : Building, SphereShape {
    val radiusSquared = radius * radius
}


@Suppress("UNCHECKED_CAST")
class OctreeTest {

    fun calculateFinalDamage(initialDamage: Int, defensePoints: Int, toughness: Int): Double {
        return initialDamage * (1 - (min(
            20.0,
            max((defensePoints / 5.0), defensePoints - (initialDamage / (2.0 + (toughness / 4.0))))
        ) / 25.0))
    }


    enum class ArmorType {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }

    @Test
    fun testDamage() {
        val initialDamage = 7
        val defensePoints = 9
        val toughness = 4
        val finalDamage = calculateFinalDamage(initialDamage, defensePoints, toughness)
        assertEquals(5.13, finalDamage, 0.01)
    }



    @Test
    fun testCube() {
        val octree = OctreeImpl<Building>()

        val building = CuboidBuilding(Point.ZERO, Point(10.0, 10.0, 10.0), "10x10x10 Cube")
        octree.insert(building)

        val point = Point(5.0, 5.0, 5.0)
        val found = octree.find(point)
        assertEquals(1, found.size)
        assertEquals(building, found.first())

        val point2 = Point(10.0, 10.0, 10.0)
        val found2 = octree.find(point2)
        assertEquals(1, found2.size)
        assertEquals(building, found2.first())

        val point3 = Point(15.0, 15.0, 15.0)
        val found3 = octree.find(point3)
        assertEquals(0, found3.size)

        val nearby1 = octree.findNearby(point3, 8.67)
        assertEquals(1, nearby1.size)
        assertEquals(building, nearby1.first())

        val region1Min = Point.ZERO
        val region1Max = Point(1.0, 1.0, .01)
        val inRegion1 = octree.findInRegion(region1Min, region1Max)
        assertEquals(1, inRegion1.size)
        assertEquals(building, inRegion1.first())

        val region2Min = Point(10.0, 10.0, 10.0)
        val region2Max = Point(11.0, 11.0, 11.0)
        val inRegion2 = octree.findInRegion(region2Min, region2Max)
        assertEquals(1, inRegion2.size)
        assertEquals(building, inRegion2.first())

        val region3Min = Point(11.0, 11.0, 11.0)
        val region3Max = Point(12.0, 12.0, 12.0)
        val inRegion3 = octree.findInRegion(region3Min, region3Max)
        assertEquals(0, inRegion3.size)
    }

    @Test
    fun testSphere() {
        val octree = OctreeImpl<Building>()

        val building = SphereBuilding(Point(10.0, 10.0, 10.0), 5.0, "Sphere")
        octree.insert(building)

        val point = Point(10.0, 10.0, 10.0)
        val found = octree.find(point)
        assertEquals(1, found.size)
        assertEquals(building, found.first())

        val point2 = Point(12.0, 12.0, 12.0)
        val found2 = octree.find(point2)
        assertEquals(1, found2.size)
        assertEquals(building, found2.first())

        val point3 = Point(15.0, 15.0, 15.0)
        val found3 = octree.find(point3)
        assertEquals(0, found3.size)

        val nearby1 = octree.findNearby(point3, 5.0)
        assertEquals(1, nearby1.size)
        assertEquals(building, nearby1.first())

        val region1Min = Point(10.0, 10.0, 10.0)
        val region1Max = Point(11.0, 11.0, 11.0)
        val inRegion1 = octree.findInRegion(region1Min, region1Max)
        assertEquals(1, inRegion1.size)
        assertEquals(building, inRegion1.first())

        val region2Min = Point(10.0, 10.0, 10.0)
        val region2Max = Point(11.0, 11.0, 11.0)
        val inRegion2 = octree.findInRegion(region2Min, region2Max)
        assertEquals(1, inRegion2.size)
        assertEquals(building, inRegion2.first())

        val region3Min = Point(13.0, 13.0, 13.0)
        val region3Max = Point(14.0, 14.0, 14.0)
        val inRegion3 = octree.findInRegion(region3Min, region3Max)
        assertEquals(0, inRegion3.size)
    }

    @Test
    fun testMultipleCuboidElements() {
        val octree = OctreeImpl<Building>()

        val building1 = CuboidBuilding(Point.ZERO, Point(10.0, 10.0, 10.0), "Building 1")
        val building2 = CuboidBuilding(Point(5.0, 5.0, 5.0), Point(15.0, 15.0, 15.0), "Building 2")
        octree.insert(building1)
        octree.insert(building2)

        val point1 = Point(5.0, 5.0, 5.0)
        val found1 = octree.find(point1)
        assertEquals(2, found1.size)
        assertTrue(found1.contains(building1))
        assertTrue(found1.contains(building2))

        val regionMin = Point.ZERO
        val regionMax = Point(20.0, 20.0, 20.0)
        val inRegion = octree.findInRegion(regionMin, regionMax)
        assertEquals(2, inRegion.size)
        assertTrue(inRegion.contains(building1))
        assertTrue(inRegion.contains(building2))
    }

    @Test
    fun testMultipleSphereElements() {
        val octree = OctreeImpl<Building>()

        val sphere1 = SphereBuilding(Point(10.0, 10.0, 10.0), 5.0, "Sphere 1")
        val sphere2 = SphereBuilding(Point(15.0, 15.0, 15.0), 5.0, "Sphere 2")
        octree.insert(sphere1)
        octree.insert(sphere2)

        val point1 = Point(12.0, 12.0, 12.0)
        val found1 = octree.find(point1)
        assertEquals(1, found1.size)
        assertEquals(sphere1, found1.first())

        val point2 = Point(15.0, 15.0, 15.0)
        val found2 = octree.find(point2)
        assertEquals(1, found2.size)
        assertEquals(sphere2, found2.first())

        val nearby = octree.findNearby(Point(20.0, 20.0, 20.0), 17.4)
        assertEquals(2, nearby.size)
        assertTrue(nearby.contains(sphere1))
        assertTrue(nearby.contains(sphere2))
    }

    @Test
    fun testIteration() {
        val octree = OctreeImpl<Building>()

        val building1 = CuboidBuilding(Point.ZERO, Point(10.0, 10.0, 10.0), "Building 1")
        val building2 = CuboidBuilding(Point(20.0, 20.0, 20.0), Point(30.0, 30.0, 30.0), "Building 2")
        val sphere1 = SphereBuilding(Point(5.0, 5.0, 5.0), 5.0, "Sphere 1")
        val sphere2 = SphereBuilding(Point(25.0, 25.0, 25.0), 5.0, "Sphere 2")

        octree.insert(building1)
        octree.insert(building2)
        octree.insert(sphere1)
        octree.insert(sphere2)

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

        val building = CuboidBuilding(Point.ZERO, Point(10.0, 10.0, 10.0), "Building")
        val sphere = SphereBuilding(Point(5.0, 5.0, 5.0), 5.0, "Sphere")
        octree.insert(building)
        octree.insert(sphere)

        val point1 = Point(5.0, 5.0, 5.0)
        val found1 = octree.find(point1)
        assertEquals(2, found1.size)
        assertTrue(found1.contains(building))
        assertTrue(found1.contains(sphere))

        val nearby = octree.findNearby(Point(4.0, 4.0, 4.0), 5.0)
        assertEquals(2, nearby.size)
        assertTrue(nearby.contains(building))
        assertTrue(nearby.contains(sphere))

        val regionMin = Point.ZERO
        val regionMax = Point(5.0, 5.0, 5.0)
        val inRegion = octree.findInRegion(regionMin, regionMax)
        assertEquals(2, inRegion.size)
        assertTrue(inRegion.contains(building))
        assertTrue(inRegion.contains(sphere))
    }

    @Test
    fun testNodesIteration() {
        val octree = OctreeImpl<Building>()

        val building1 = CuboidBuilding(Point.ZERO, Point(10.0, 10.0, 10.0), "Building 1")
        val building2 = CuboidBuilding(Point(20.0, 20.0, 20.0), Point(30.0, 30.0, 30.0), "Building 2")
        val sphere1 = SphereBuilding(Point(5.0, 5.0, 5.0), 5.0, "Sphere 1")
        val sphere2 = SphereBuilding(Point(25.0, 25.0, 25.0), 5.0, "Sphere 2")

        octree.insert(building1)
        octree.insert(building2)
        octree.insert(sphere1)
        octree.insert(sphere2)

        val nodes = octree.iteratorNode().asSequence().toList()
        assertEquals(4, nodes.size)
        assertTrue(nodes.any { it == building1 })
        assertTrue(nodes.any { it == building2 })
        assertTrue(nodes.any { it == sphere1 })
        assertTrue(nodes.any { it == sphere2 })
    }
}