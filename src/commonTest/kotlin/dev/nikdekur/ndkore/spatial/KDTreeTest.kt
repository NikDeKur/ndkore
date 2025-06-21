///*
// * This Source Code Form is subject to the terms of the Mozilla Public
// * License, v. 2.0. If a copy of the MPL was not distributed with this
// * file, You can obtain one at http://mozilla.org/MPL/2.0/.
// *
// * Copyright (c) 2024-present "Nik De Kur"
// */
//
//package dev.nikdekur.ndkore.spatial
//
//import dev.nikdekur.ndkore.spatial.kdtree.KDTreeImpl
//import kotlin.math.sqrt
//import kotlin.test.*
//
//class KDTreeTest {
//    private lateinit var tree: KDTreeImpl<String>
//
//    @BeforeTest
//    fun setUp() {
//        tree = KDTreeImpl()
//    }
//
//    @Test
//    fun testInsertAndSize() {
//        assertEquals(0, tree.size)
//
//        tree.insert(Point(1, 2, 3), "A")
//        assertEquals(1, tree.size)
//
//        tree.insert(Point(4, 5, 6), "B")
//        assertEquals(2, tree.size)
//    }
//
//    @Test
//    fun testInsertAndNearestNeighbor() {
//        tree.insert(Point(1, 2, 3), "A")
//        tree.insert(Point(4, 5, 6), "B")
//        tree.insert(Point(-1, -2, -3), "C")
//
//        assertEquals("A", tree.nearestNeighbor(Point(1, 2, 3)))
//        assertEquals("B", tree.nearestNeighbor(Point(4, 5, 6)))
//        assertEquals("C", tree.nearestNeighbor(Point(-1, -2, -3)))
//    }
//
//    @Test
//    fun testRemove() {
//        tree.insert(Point(1, 2, 3), "A")
//        tree.insert(Point(4, 5, 6), "B")
//        tree.insert(Point(-1, -2, -3), "C")
//
//        tree.remove(Point(1, 2, 3))
//        assertEquals(2, tree.size)
//        assertEquals("B", tree.nearestNeighbor(Point(1, 2, 3)))
//    }
//
//    @Test
//    fun testRemoveNodeWithOneChild() {
//        tree.insert(Point(1, 2, 3), "A")
//        tree.insert(Point(0, 0, 0), "B")
//        tree.insert(Point(-1, -1, -1), "C")
//
//        tree.remove(Point(0, 0, 0))
//        assertEquals(2, tree.size)
//        assertEquals("A", tree.nearestNeighbor(Point(1, 2, 3)))
//    }
//
//    @Test
//    fun testRemoveNodeWithTwoChildren() {
//        tree.insert(Point(2, 3, 4), "A")
//        tree.insert(Point(1, 2, 3), "B")
//        tree.insert(Point(3, 4, 5), "C")
//
//        tree.remove(Point(2, 3, 4))
//        assertEquals(2, tree.size)
//        assertTrue(tree.nearestNeighbor(Point(2, 3, 4)) in listOf("B", "C"))
//    }
//
//    @Test
//    fun testRangeSearch() {
//        tree.insert(Point(1, 2, 3), "A")
//        tree.insert(Point(4, 5, 6), "B")
//        tree.insert(Point(-1, -2, -3), "C")
//
//        val result = tree.rangeSearch(Point(0, 0, 0), Point(2, 3, 4))
//        assertEquals(listOf("A"), result)
//    }
//
//    @Test
//    fun testRangeSearchWithMultipleResults() {
//        tree.insert(Point(1, 2, 3), "A")
//        tree.insert(Point(2, 2, 3), "B")
//        tree.insert(Point(4, 5, 6), "C")
//
//        val result = tree.rangeSearch(Point(0, 0, 0), Point(3, 3, 3))
//        assertEquals(listOf("A", "B"), result)
//    }
//
//    @Test
//    fun testNearestNeighbors() {
//        tree.insert(Point(1, 2, 3), "A")
//        tree.insert(Point(4, 5, 6), "B")
//        tree.insert(Point(-1, -2, -3), "C")
//        tree.insert(Point(1, 3, 3), "D")
//
//        val result = tree.nearestNeighbors(Point(1, 2, 3), 2)
//        assertEquals(listOf("A", "D"), result)
//    }
//
//    @Test
//    fun testNearestNeighborsWithDuplicates() {
//        tree.insert(Point(1, 2, 3), "A")
//        tree.insert(Point(1, 2, 3), "B")
//        tree.insert(Point(4, 5, 6), "C")
//        tree.insert(Point(-1, -2, -3), "D")
//
//        val result = tree.nearestNeighbors(Point(1, 2, 3), 2)
//        assertEquals(listOf("A", "B"), result)
//    }
//
//    @Test
//    fun testClear() {
//        tree.insert(Point(1, 2, 3), "A")
//        tree.insert(Point(4, 5, 6), "B")
//
//        tree.clear()
//        assertEquals(0, tree.size)
//        assertNull(tree.nearestNeighbor(Point(1, 2, 3)))
//    }
//
//    @Test
//    fun testRangeSearchWithRadius() {
//        tree.insert(Point(1, 2, 3), "A")
//        tree.insert(Point(4, 5, 6), "B")
//        tree.insert(Point(-1, -2, -3), "C")
//
//        val result = tree.rangeSearch(Point(0, 0, 0), 4.0)
//        assertEquals(listOf("A", "C"), result)
//    }
//
//    @Test
//    fun testInsertDuplicatePoint() {
//        tree.insert(Point(1, 2, 3), "A")
//        tree.insert(Point(1, 2, 3), "B")
//
//        assertTrue(tree.nearestNeighbor(Point(1, 2, 3)) in listOf("A", "B"))
//        assertEquals(2, tree.size)
//    }
//
//    @Test
//    fun testRemoveNonExistentPoint() {
//        tree.insert(Point(1, 2, 3), "A")
//        tree.insert(Point(4, 5, 6), "B")
//
//        tree.remove(Point(0, 0, 0))
//        assertEquals(2, tree.size)
//    }
//
//    @Test
//    fun testNearestNeighborOnEmptyTree() {
//        assertNull(tree.nearestNeighbor(Point(0, 0, 0)))
//    }
//
//    @Test
//    fun testNearestNeighborsOnEmptyTree() {
//        assertTrue(tree.nearestNeighbors(Point(0, 0, 0), 1).isEmpty())
//    }
//
//    @Test
//    fun testRangeSearchOnEmptyTree() {
//        assertTrue(tree.rangeSearch(Point(0, 0, 0), sqrt(14.0)).isEmpty())
//    }
//
//    @Test
//    fun testRangeSearchWithBoxOnEmptyTree() {
//        assertTrue(tree.rangeSearch(Point(0, 0, 0), Point(1, 1, 1)).isEmpty())
//    }
//
//    @Test
//    fun testRemoveAllPoints() {
//        tree.insert(Point(1, 2, 3), "A")
//        tree.insert(Point(4, 5, 6), "B")
//        tree.insert(Point(-1, -2, -3), "C")
//
//        tree.remove(Point(1, 2, 3))
//        tree.remove(Point(4, 5, 6))
//        tree.remove(Point(-1, -2, -3))
//
//        assertEquals(0, tree.size)
//        assertNull(tree.nearestNeighbor(Point(1, 2, 3)))
//    }
//
//
//    data class CastlesChest(val point: Point, val name: String)
//
//
//    @Test
//    fun castlesMapTest() {
//        val chestsList = mutableListOf<CastlesChest>()
//        repeat(10) { x ->
//            repeat(10) { y ->
//                repeat(10) { z ->
//                    chestsList.add(CastlesChest(Point(x, y, z), "Chest $x $y $z"))
//                }
//            }
//        }
//
//        val tree = KDTreeImpl<CastlesChest>()
//        chestsList.forEach { tree.insert(it.point, it) }
//
//        val point = Point(654, 176, 935)
//        val nearestByTree = tree.nearestNeighbor(point)
//        val nearestByBruteForce = chestsList.minByOrNull { it.point.distanceSquared(point) }
//        assertEquals(nearestByBruteForce, nearestByTree)
//    }
//}
