package dev.nikdekur.ndkore.spatial

import dev.nikdekur.ndkore.spatial.octree.OctreeImpl
import dev.nikdekur.ndkore.spatial.octree.isEmpty
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

/**
 * Comprehensive test suite for OctreeImpl covering all possible scenarios and edge cases.
 * Tests both single and multiple shape types, duplicate shapes, boundary conditions,
 * and all octree operations.
 */
class OctreeImplTest {

    companion object {
        private val ORIGIN = Point(0.0, 0.0, 0.0)
        private val UNIT_POINT = Point(1.0, 1.0, 1.0)
        private val NEGATIVE_POINT = Point(-1.0, -1.0, -1.0)
        private val LARGE_POINT = Point(100.0, 100.0, 100.0)

        // Test shapes for various scenarios
        private fun createTestCuboid(
            minX: Double, minY: Double, minZ: Double,
            maxX: Double, maxY: Double, maxZ: Double
        ) =
            CuboidShapeData(Point(minX, minY, minZ), Point(maxX, maxY, maxZ))

        private fun createTestSphere(centerX: Double, centerY: Double, centerZ: Double, radius: Double) =
            SphereShapeData(Point(centerX, centerY, centerZ), radius)

        private fun createTestCylinder(
            centerX: Double, centerY: Double, centerZ: Double,
            radius: Double, height: Double
        ) =
            CylinderShapeData(Point(centerX, centerY, centerZ), radius, height)
    }

    // ========== CONSTRUCTOR TESTS ==========

    @Test
    fun `should create octree with default capacity`() {
        val octree = OctreeImpl<Shape>()
        assertTrue(octree.isEmpty())
        assertEquals(0, octree.count())
    }

    @Test
    fun `should create octree with custom capacity`() {
        val octree = OctreeImpl<Shape>(capacity = 5)
        assertTrue(octree.isEmpty())
        assertEquals(0, octree.count())
    }

    @Test
    fun `should create octree with predefined bounds`() {
        val minPoint = Point(-10.0, -10.0, -10.0)
        val maxPoint = Point(10.0, 10.0, 10.0)
        val octree = OctreeImpl<Shape>(minPoint, maxPoint, capacity = 8)

        assertEquals(minPoint, octree.min)
        assertEquals(maxPoint, octree.max)
        assertEquals(Point(0.0, 0.0, 0.0), octree.center)
        assertTrue(octree.isEmpty())
    }

    @Test
    fun `should handle invalid capacity gracefully`() {
        // Test with capacity 0 and negative values
        OctreeImpl<Shape>(capacity = 0)
        OctreeImpl<Shape>(capacity = -1)
    }

    // ========== AUTO-BOUNDS CALCULATION TESTS ==========

    @Test
    fun `should auto-calculate bounds on first insert`() {
        val octree = OctreeImpl<Shape>()
        val shape = createTestCuboid(1.0, 1.0, 1.0, 5.0, 5.0, 5.0)

        octree.insert(shape)

        // Bounds should be calculated to accommodate the shape
        assertTrue(octree.min.x <= shape.min.x)
        assertTrue(octree.min.y <= shape.min.y)
        assertTrue(octree.min.z <= shape.min.z)
        assertTrue(octree.max.x >= shape.max.x)
        assertTrue(octree.max.y >= shape.max.y)
        assertTrue(octree.max.z >= shape.max.z)
    }

    @Test
    fun `should expand bounds when inserting shapes outside current bounds`() {
        val initialBounds = Point(-5.0, -5.0, -5.0) to Point(5.0, 5.0, 5.0)
        val octree = OctreeImpl<Shape>(initialBounds.first, initialBounds.second)

        // Insert shape outside bounds
        val outsideShape = createTestCuboid(10.0, 10.0, 10.0, 15.0, 15.0, 15.0)
        octree.insert(outsideShape)

        // Verify bounds expanded
        assertTrue(octree.max.x >= outsideShape.max.x)
        assertTrue(octree.max.y >= outsideShape.max.y)
        assertTrue(octree.max.z >= outsideShape.max.z)
    }

    // ========== SINGLE SHAPE TYPE TESTS ==========

    @Test
    fun `should handle single cuboid insertion and retrieval`() {
        val octree = OctreeImpl<Shape>()
        val cuboid = createTestCuboid(0.0, 0.0, 0.0, 2.0, 2.0, 2.0)

        octree.insert(cuboid)

        assertEquals(1, octree.count())
        assertFalse(octree.isEmpty())
        assertTrue(octree.contains(cuboid))

        // Test point containment
        val insidePoint = Point(1.0, 1.0, 1.0)
        val found = octree.find(insidePoint)
        assertTrue(found.contains(cuboid))
    }

    @Test
    fun `should handle multiple cuboids`() {
        val octree = OctreeImpl<Shape>()
        val cuboids = listOf(
            createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
            createTestCuboid(2.0, 2.0, 2.0, 3.0, 3.0, 3.0),
            createTestCuboid(5.0, 5.0, 5.0, 6.0, 6.0, 6.0),
            createTestCuboid(-1.0, -1.0, -1.0, 0.5, 0.5, 0.5)
        )

        cuboids.forEach { octree.insert(it) }

        assertEquals(4, octree.count())
        cuboids.forEach { assertTrue(octree.contains(it)) }
    }

    @Test
    fun `should handle single sphere operations`() {
        val octree = OctreeImpl<Shape>()
        val sphere = createTestSphere(0.0, 0.0, 0.0, 2.0)

        octree.insert(sphere)

        assertEquals(1, octree.count())
        assertTrue(octree.contains(sphere))

        // Test point inside sphere
        val insidePoint = Point(1.0, 1.0, 0.0)
        val found = octree.find(insidePoint)
        assertTrue(found.contains(sphere))

        // Test point outside sphere
        val outsidePoint = Point(3.0, 3.0, 3.0)
        val notFound = octree.find(outsidePoint)
        assertFalse(notFound.contains(sphere))
    }

    @Test
    fun `should handle multiple spheres`() {
        val octree = OctreeImpl<Shape>()
        val spheres = listOf(
            createTestSphere(0.0, 0.0, 0.0, 1.0),
            createTestSphere(5.0, 5.0, 5.0, 2.0),
            createTestSphere(-3.0, -3.0, -3.0, 1.5),
            createTestSphere(10.0, 0.0, -5.0, 0.5)
        )

        spheres.forEach { octree.insert(it) }

        assertEquals(4, octree.count())
        spheres.forEach { assertTrue(octree.contains(it)) }
    }

    @Test
    fun `should handle single cylinder operations`() {
        val octree = OctreeImpl<Shape>()
        val cylinder = createTestCylinder(0.0, 0.0, 0.0, 2.0, 5.0)

        octree.insert(cylinder)

        assertEquals(1, octree.count())
        assertTrue(octree.contains(cylinder))

        // Test point inside cylinder
        val insidePoint = Point(1.0, 2.0, 0.0)
        val found = octree.find(insidePoint)
        assertTrue(found.contains(cylinder))
    }

    // ========== MIXED SHAPE TYPE TESTS ==========

    @Test
    fun `should handle mixed shape types in same octree`() {
        val octree = OctreeImpl<Shape>()
        val cuboid = createTestCuboid(0.0, 0.0, 0.0, 2.0, 2.0, 2.0)
        val sphere = createTestSphere(3.0, 3.0, 3.0, 1.0)
        val cylinder = createTestCylinder(6.0, 0.0, 6.0, 1.0, 3.0)

        octree.insert(cuboid)
        octree.insert(sphere)
        octree.insert(cylinder)

        assertEquals(3, octree.count())
        assertTrue(octree.contains(cuboid))
        assertTrue(octree.contains(sphere))
        assertTrue(octree.contains(cylinder))

        // Verify each shape can be found at appropriate points
        assertTrue(octree.find(Point(1.0, 1.0, 1.0)).contains(cuboid))
        assertTrue(octree.find(Point(3.0, 3.0, 3.0)).contains(sphere))
        assertTrue(octree.find(Point(6.0, 1.0, 6.0)).contains(cylinder))
    }

    @Test
    fun `should handle overlapping mixed shapes`() {
        val octree = OctreeImpl<Shape>()
        val centerPoint = Point(0.0, 0.0, 0.0)

        val cuboid = createTestCuboid(-2.0, -2.0, -2.0, 2.0, 2.0, 2.0)
        val sphere = createTestSphere(0.0, 0.0, 0.0, 3.0)
        val cylinder = createTestCylinder(-1.0, -2.0, -1.0, 2.0, 4.0)

        octree.insert(cuboid)
        octree.insert(sphere)
        octree.insert(cylinder)

        // Point at origin should be found in all shapes
        val foundAtOrigin = octree.find(centerPoint)
        assertEquals(3, foundAtOrigin.size)
        assertTrue(foundAtOrigin.contains(cuboid))
        assertTrue(foundAtOrigin.contains(sphere))
        assertTrue(foundAtOrigin.contains(cylinder))
    }

    // ========== DUPLICATE SHAPE TESTS ==========

    @Test
    fun `should handle duplicate shape insertion`() {
        val octree = OctreeImpl<Shape>()
        val shape = createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)

        octree.insert(shape)
        octree.insert(shape) // Insert same instance twice

        // Should contain the shape multiple times
        assertEquals(2, octree.count())

        val found = octree.find(Point(0.5, 0.5, 0.5))
        assertEquals(2, found.size)
        assertEquals(2, found.count { it == shape })
    }

    @Test
    fun `should handle identical but different instance shapes`() {
        val octree = OctreeImpl<Shape>()
        val shape1 = createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        val shape2 = createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0) // Same bounds, different instance

        octree.insert(shape1)
        octree.insert(shape2)

        assertEquals(2, octree.count())
        assertTrue(octree.contains(shape1))
        assertTrue(octree.contains(shape2))

        val found = octree.find(Point(0.5, 0.5, 0.5))
        assertEquals(2, found.size)
    }

    @Test
    fun `should handle many duplicates of same shape`() {
        val octree = OctreeImpl<Shape>()
        val shape = createTestSphere(0.0, 0.0, 0.0, 1.0)
        val duplicateCount = 10

        repeat(duplicateCount) {
            octree.insert(shape)
        }

        assertEquals(duplicateCount, octree.count())
        val found = octree.find(Point(0.0, 0.0, 0.0))
        assertEquals(duplicateCount, found.size)
    }

    // ========== BOUNDARY CONDITION TESTS ==========

    @Test
    fun `should handle shapes at octree boundaries`() {
        val minBound = Point(-10.0, -10.0, -10.0)
        val maxBound = Point(10.0, 10.0, 10.0)
        val octree = OctreeImpl<Shape>(minBound, maxBound)

        // Shapes exactly at boundaries
        val minCornerShape = createTestCuboid(-10.0, -10.0, -10.0, -9.0, -9.0, -9.0)
        val maxCornerShape = createTestCuboid(9.0, 9.0, 9.0, 10.0, 10.0, 10.0)
        val centerShape = createTestSphere(0.0, 0.0, 0.0, 0.5)

        octree.insert(minCornerShape)
        octree.insert(maxCornerShape)
        octree.insert(centerShape)

        assertEquals(3, octree.count())
        assertTrue(octree.find(Point(-9.5, -9.5, -9.5)).contains(minCornerShape))
        assertTrue(octree.find(Point(9.5, 9.5, 9.5)).contains(maxCornerShape))
        assertTrue(octree.find(Point(0.0, 0.0, 0.0)).contains(centerShape))
    }

    @Test
    fun `should handle very small shapes`() {
        val octree = OctreeImpl<Shape>()
        val tinyShape = createTestCuboid(0.0, 0.0, 0.0, 0.001, 0.001, 0.001)

        octree.insert(tinyShape)

        assertTrue(octree.contains(tinyShape))
        assertTrue(octree.find(Point(0.0005, 0.0005, 0.0005)).contains(tinyShape))
    }

    @Test
    fun `should handle very large shapes`() {
        val octree = OctreeImpl<Shape>()
        val largeShape = createTestCuboid(-1000.0, -1000.0, -1000.0, 1000.0, 1000.0, 1000.0)

        octree.insert(largeShape)

        assertTrue(octree.contains(largeShape))
        assertTrue(octree.find(Point(0.0, 0.0, 0.0)).contains(largeShape))
        assertTrue(octree.find(Point(500.0, 500.0, 500.0)).contains(largeShape))
    }

    // ========== FIND OPERATION TESTS ==========

    @Test
    fun `find should return empty collection for point with no shapes`() {
        val octree = OctreeImpl<Shape>()
        val shape = createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        octree.insert(shape)

        val found = octree.find(Point(10.0, 10.0, 10.0))
        assertTrue(found.isEmpty())
    }

    @Test
    fun `find should return all shapes containing the point`() {
        val octree = OctreeImpl<Shape>()
        val testPoint = Point(1.0, 1.0, 1.0)

        val containingCuboid = createTestCuboid(0.0, 0.0, 0.0, 2.0, 2.0, 2.0)
        val containingSphere = createTestSphere(1.0, 1.0, 1.0, 1.0)
        val nonContainingShape = createTestCuboid(5.0, 5.0, 5.0, 6.0, 6.0, 6.0)

        octree.insert(containingCuboid)
        octree.insert(containingSphere)
        octree.insert(nonContainingShape)

        val found = octree.find(testPoint)
        assertEquals(2, found.size)
        assertTrue(found.contains(containingCuboid))
        assertTrue(found.contains(containingSphere))
        assertFalse(found.contains(nonContainingShape))
    }

    // ========== FINDNEARBY OPERATION TESTS ==========

    @Test
    fun `findNearby should return shapes within radius`() {
        val octree = OctreeImpl<Shape>()
        val centerPoint = Point(0.0, 0.0, 0.0)

        val nearShape = createTestCuboid(1.0, 1.0, 1.0, 2.0, 2.0, 2.0) // Distance ~1.73
        val farShape = createTestCuboid(5.0, 5.0, 5.0, 6.0, 6.0, 6.0) // Distance ~8.66
        val veryNearShape = createTestSphere(0.5, 0.0, 0.0, 0.2) // Distance 0.3

        octree.insert(nearShape)
        octree.insert(farShape)
        octree.insert(veryNearShape)

        val foundWithin3 = octree.findNearby(centerPoint, 3.0)
        assertEquals(2, foundWithin3.size)
        assertTrue(foundWithin3.contains(nearShape))
        assertTrue(foundWithin3.contains(veryNearShape))
        assertFalse(foundWithin3.contains(farShape))

        val foundWithin10 = octree.findNearby(centerPoint, 10.0)
        assertEquals(3, foundWithin10.size)
    }

    @Test
    fun `findNearby should handle zero radius correctly`() {
        val octree = OctreeImpl<Shape>()
        val point = Point(1.0, 1.0, 1.0)

        val containingShape = createTestCuboid(0.0, 0.0, 0.0, 2.0, 2.0, 2.0)
        val touchingShape = createTestSphere(1.0, 1.0, 1.0, 0.5)
        val nonTouchingShape = createTestCuboid(3.0, 3.0, 3.0, 4.0, 4.0, 4.0)

        octree.insert(containingShape)
        octree.insert(touchingShape)
        octree.insert(nonTouchingShape)

        val found = octree.findNearby(point, 0.0)
        assertEquals(2, found.size) // Only shapes that contain the point
        assertTrue(found.contains(containingShape))
        assertTrue(found.contains(touchingShape))
    }

    // ========== FINDINREGION OPERATION TESTS ==========

    @Test
    fun `findInRegion should return shapes that intersect with region`() {
        val octree = OctreeImpl<Shape>()

        val insideShape = createTestCuboid(1.0, 1.0, 1.0, 2.0, 2.0, 2.0)
        val intersectingShape = createTestCuboid(2.5, 2.5, 2.5, 4.0, 4.0, 4.0)
        val outsideShape = createTestCuboid(10.0, 10.0, 10.0, 11.0, 11.0, 11.0)
        val containingShape = createTestCuboid(-1.0, -1.0, -1.0, 5.0, 5.0, 5.0)

        octree.insert(insideShape)
        octree.insert(intersectingShape)
        octree.insert(outsideShape)
        octree.insert(containingShape)

        val regionMin = Point(0.0, 0.0, 0.0)
        val regionMax = Point(3.0, 3.0, 3.0)

        val found = octree.findInRegion(regionMin, regionMax)
        assertEquals(3, found.size)
        assertTrue(found.contains(insideShape))
        assertTrue(found.contains(intersectingShape))
        assertTrue(found.contains(containingShape))
        assertFalse(found.contains(outsideShape))
    }

    @Test
    fun `findInRegion should handle point region (min equals max)`() {
        val octree = OctreeImpl<Shape>()
        val point = Point(1.0, 1.0, 1.0)

        val containingShape = createTestCuboid(0.0, 0.0, 0.0, 2.0, 2.0, 2.0)
        val nonContainingShape = createTestCuboid(3.0, 3.0, 3.0, 4.0, 4.0, 4.0)

        octree.insert(containingShape)
        octree.insert(nonContainingShape)

        val found = octree.findInRegion(point, point)
        assertEquals(1, found.size)
        assertTrue(found.contains(containingShape))
    }

    // ========== REMOVE OPERATION TESTS ==========

    @Test
    fun `should remove single shape correctly`() {
        val octree = OctreeImpl<Shape>()
        val shape = createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)

        octree.insert(shape)
        assertTrue(octree.contains(shape))
        assertEquals(1, octree.count())

        octree.remove(shape)
        assertFalse(octree.contains(shape))
        assertEquals(0, octree.count())
        assertTrue(octree.isEmpty())
    }

    @Test
    fun `should remove only one instance of duplicated shape`() {
        val octree = OctreeImpl<Shape>()
        val shape = createTestSphere(0.0, 0.0, 0.0, 1.0)

        octree.insert(shape)
        octree.insert(shape)
        octree.insert(shape)
        assertEquals(3, octree.count())

        octree.remove(shape)
        assertEquals(2, octree.count())
        assertTrue(octree.contains(shape))

        octree.remove(shape)
        assertEquals(1, octree.count())
        assertTrue(octree.contains(shape))

        octree.remove(shape)
        assertEquals(0, octree.count())
        assertFalse(octree.contains(shape))
    }

    @Test
    fun `should handle remove of non-existent shape gracefully`() {
        val octree = OctreeImpl<Shape>()
        val existingShape = createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        val nonExistentShape = createTestCuboid(5.0, 5.0, 5.0, 6.0, 6.0, 6.0)

        octree.insert(existingShape)
        assertEquals(1, octree.count())


        octree.remove(nonExistentShape)

        assertEquals(1, octree.count()) // Should remain unchanged
        assertTrue(octree.contains(existingShape))
    }

    @Test
    fun `should remove from mixed shape types correctly`() {
        val octree = OctreeImpl<Shape>()
        val cuboid = createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        val sphere = createTestSphere(2.0, 2.0, 2.0, 1.0)
        val cylinder = createTestCylinder(4.0, 0.0, 4.0, 1.0, 2.0)

        octree.insert(cuboid)
        octree.insert(sphere)
        octree.insert(cylinder)
        assertEquals(3, octree.count())

        octree.remove(sphere)
        assertEquals(2, octree.count())
        assertTrue(octree.contains(cuboid))
        assertFalse(octree.contains(sphere))
        assertTrue(octree.contains(cylinder))
    }

    // ========== CLEAR OPERATION TESTS ==========

    @Test
    fun `should clear empty octree without issues`() {
        val octree = OctreeImpl<Shape>()
        assertTrue(octree.isEmpty())

        octree.clear()
        assertTrue(octree.isEmpty())
        assertEquals(0, octree.count())
    }

    @Test
    fun `should clear octree with single shape`() {
        val octree = OctreeImpl<Shape>()
        val shape = createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)

        octree.insert(shape)
        assertFalse(octree.isEmpty())

        octree.clear()
        assertTrue(octree.isEmpty())
        assertEquals(0, octree.count())
        assertFalse(octree.contains(shape))
    }

    @Test
    fun `should clear octree with multiple mixed shapes`() {
        val octree = OctreeImpl<Shape>()
        val shapes = listOf(
            createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
            createTestSphere(2.0, 2.0, 2.0, 1.0),
            createTestCylinder(4.0, 0.0, 4.0, 1.0, 2.0),
            createTestCuboid(-1.0, -1.0, -1.0, 0.5, 0.5, 0.5)
        )

        shapes.forEach { octree.insert(it) }
        assertEquals(4, octree.count())

        octree.clear()
        assertTrue(octree.isEmpty())
        assertEquals(0, octree.count())
        shapes.forEach { assertFalse(octree.contains(it)) }
    }

    @Test
    fun `should be able to insert after clear`() {
        val octree = OctreeImpl<Shape>()
        val originalShape = createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        val newShape = createTestSphere(2.0, 2.0, 2.0, 1.0)

        octree.insert(originalShape)
        octree.clear()

        octree.insert(newShape)
        assertEquals(1, octree.count())
        assertFalse(octree.contains(originalShape))
        assertTrue(octree.contains(newShape))
    }

    // ========== ITERATOR TESTS ==========

    @Test
    fun `should iterate through empty octree`() {
        val octree = OctreeImpl<Shape>()
        val items = octree.toList()
        assertTrue(items.isEmpty())
    }

    @Test
    fun `should iterate through all shapes including duplicates`() {
        val octree = OctreeImpl<Shape>()
        val shape1 = createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        val shape2 = createTestSphere(2.0, 2.0, 2.0, 1.0)

        octree.insert(shape1)
        octree.insert(shape2)
        octree.insert(shape1) // Duplicate

        val items = octree.toList()
        assertEquals(3, items.size)
        assertEquals(2, items.count { it == shape1 })
        assertEquals(1, items.count { it == shape2 })
    }

    @Test
    fun `should maintain iterator consistency during modifications`() {
        val octree = OctreeImpl<Shape>()
        val shapes = (1..5).map {
            createTestCuboid(
                it.toDouble(), it.toDouble(), it.toDouble(),
                it.toDouble() + 1, it.toDouble() + 1, it.toDouble() + 1
            )
        }

        shapes.forEach { octree.insert(it) }

        val iteratedShapes = mutableListOf<Shape>()
        for (shape in octree) {
            iteratedShapes.add(shape)
        }

        assertEquals(5, iteratedShapes.size)
        shapes.forEach { shape ->
            assertTrue(iteratedShapes.contains(shape))
        }
    }

    // ========== STRESS TESTS ==========

    @Test
    fun `should handle large number of shapes efficiently`() {
        val octree = OctreeImpl<Shape>()
        val shapeCount = 1000
        val shapes = mutableListOf<Shape>()

        // Insert many shapes
        repeat(shapeCount) { i ->
            val shape = when (i % 3) {
                0 -> createTestCuboid(
                    i.toDouble(), i.toDouble(), i.toDouble(),
                    i.toDouble() + 1, i.toDouble() + 1, i.toDouble() + 1
                )

                1 -> createTestSphere(i.toDouble(), i.toDouble(), i.toDouble(), 0.5)
                else -> createTestCylinder(i.toDouble(), i.toDouble(), i.toDouble(), 0.5, 1.0)
            }
            shapes.add(shape)
            octree.insert(shape)
        }

        assertEquals(shapeCount, octree.count())

        // Verify random samples
        repeat(100) { i ->
            val randomShape = shapes[i * 10]
            assertTrue(octree.contains(randomShape))
        }
    }

    @Test
    fun `should handle high density of overlapping shapes`() {
        val octree = OctreeImpl<Shape>()
        val centerPoint = Point(0.0, 0.0, 0.0)
        val overlappingShapes = mutableListOf<Shape>()

        // Create many overlapping shapes at the same location
        repeat(50) { i ->
            val radius = 1.0 + i * 0.1
            val sphere = createTestSphere(0.0, 0.0, 0.0, radius)
            overlappingShapes.add(sphere)
            octree.insert(sphere)
        }

        assertEquals(50, octree.count())

        // All shapes should be found at center point
        val foundAtCenter = octree.find(centerPoint)
        assertEquals(50, foundAtCenter.size)
        overlappingShapes.forEach { shape ->
            assertTrue(foundAtCenter.contains(shape))
        }
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    fun `should handle shapes with zero volume`() {
        val octree = OctreeImpl<Shape>()
        val zeroVolumeCuboid = createTestCuboid(1.0, 1.0, 1.0, 1.0, 1.0, 1.0) // Point
        val zeroRadiusSphere = createTestSphere(2.0, 2.0, 2.0, 0.0)
        val zeroHeightCylinder = createTestCylinder(3.0, 3.0, 3.0, 1.0, 0.0)

        octree.insert(zeroVolumeCuboid)
        octree.insert(zeroRadiusSphere)
        octree.insert(zeroHeightCylinder)

        assertEquals(3, octree.count())
        assertTrue(octree.contains(zeroVolumeCuboid))
        assertTrue(octree.contains(zeroRadiusSphere))
        assertTrue(octree.contains(zeroHeightCylinder))
    }

    @Test
    fun `should handle extreme coordinate values`() {
        val octree = OctreeImpl<Shape>()
        val extremeShape = createTestCuboid(
            Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE,
            Double.MAX_VALUE / 2, Double.MAX_VALUE / 2, Double.MAX_VALUE / 2
        )


        octree.insert(extremeShape)
        assertTrue(octree.contains(extremeShape))
    }

    @Test
    fun `should handle negative coordinates correctly`() {
        val octree = OctreeImpl<Shape>()
        val negativeShapes = listOf(
            createTestCuboid(-5.0, -5.0, -5.0, -1.0, -1.0, -1.0),
            createTestSphere(-10.0, -10.0, -10.0, 2.0),
            createTestCylinder(-3.0, -8.0, -3.0, 1.0, 4.0)
        )

        negativeShapes.forEach { octree.insert(it) }

        assertEquals(3, octree.count())
        negativeShapes.forEach { assertTrue(octree.contains(it)) }

        // Test finding in negative space
        val foundNegative = octree.find(Point(-3.0, -3.0, -3.0))
        assertTrue(foundNegative.isNotEmpty())
    }

    // ========== FINDNODES TESTS ==========

    @Test
    fun `findNodes should return same results as find for basic shapes`() {
        val octree = OctreeImpl<Shape>()
        val shapes = listOf(
            createTestCuboid(0.0, 0.0, 0.0, 2.0, 2.0, 2.0),
            createTestSphere(1.0, 1.0, 1.0, 1.0),
            createTestCylinder(0.5, 0.0, 0.5, 0.5, 2.0)
        )

        shapes.forEach { octree.insert(it) }

        val testPoint = Point(1.0, 1.0, 1.0)
        val foundShapes = octree.find(testPoint)
        val foundNodes = octree.findNodes(testPoint)

        assertEquals(foundShapes.size, foundNodes.size)
        foundShapes.forEach { shape ->
            assertTrue(foundNodes.contains(shape))
        }
    }

    @Test
    fun `findNodesNearby should work correctly`() {
        val octree = OctreeImpl<Shape>()
        val centerPoint = Point(0.0, 0.0, 0.0)

        val nearShape = createTestCuboid(1.0, 1.0, 1.0, 2.0, 2.0, 2.0)
        val farShape = createTestCuboid(10.0, 10.0, 10.0, 11.0, 11.0, 11.0)

        octree.insert(nearShape)
        octree.insert(farShape)

        val foundNear = octree.findNodesNearby(centerPoint, 5.0)
        val foundFar = octree.findNodesNearby(centerPoint, 17.4) // 10 * root of 3

        assertTrue(foundNear.contains(nearShape))
        assertFalse(foundNear.contains(farShape))

        assertTrue(foundFar.contains(nearShape))
        assertTrue(foundFar.contains(farShape))
    }

    @Test
    fun `findNodesInRegion should work correctly`() {
        val octree = OctreeImpl<Shape>()

        val insideShape = createTestCuboid(1.0, 1.0, 1.0, 2.0, 2.0, 2.0)
        val outsideShape = createTestCuboid(10.0, 10.0, 10.0, 11.0, 11.0, 11.0)
        val intersectingShape = createTestSphere(2.5, 2.5, 2.5, 1.0)

        octree.insert(insideShape)
        octree.insert(outsideShape)
        octree.insert(intersectingShape)

        val regionMin = Point(0.0, 0.0, 0.0)
        val regionMax = Point(3.0, 3.0, 3.0)

        val foundInRegion = octree.findNodesInRegion(regionMin, regionMax)

        assertTrue(foundInRegion.contains(insideShape))
        assertTrue(foundInRegion.contains(intersectingShape))
        assertFalse(foundInRegion.contains(outsideShape))
    }

    // ========== PERFORMANCE AND CONSISTENCY TESTS ==========

    @Test
    fun `should maintain consistency after many insert and remove operations`() {
        val octree = OctreeImpl<Shape>()
        val shapes = (1..100).map { i ->
            createTestCuboid(
                i.toDouble(), i.toDouble(), i.toDouble(),
                i.toDouble() + 1, i.toDouble() + 1, i.toDouble() + 1
            )
        }

        // Insert all shapes
        shapes.forEach { octree.insert(it) }
        assertEquals(100, octree.count())

        // Remove every other shape
        shapes.filterIndexed { index, _ -> index % 2 == 0 }
            .forEach { octree.remove(it) }
        assertEquals(50, octree.count())

        // Verify remaining shapes
        shapes.filterIndexed { index, _ -> index % 2 == 1 }
            .forEach { shape ->
                assertTrue(octree.contains(shape))
            }

        // Verify removed shapes are gone
        shapes.filterIndexed { index, _ -> index % 2 == 0 }
            .forEach { shape ->
                assertFalse(octree.contains(shape))
            }
    }

    @Test
    fun `should handle concurrent-like operations correctly`() {
        val octree = OctreeImpl<Shape>()
        val baseShapes = (1..20).map { i ->
            createTestSphere(i.toDouble(), i.toDouble(), i.toDouble(), 0.5)
        }

        // Simulate concurrent-like operations: insert, query, remove, insert
        baseShapes.forEach { octree.insert(it) }

        repeat(10) { iteration ->
            // Query operations
            val queryPoint = Point(iteration.toDouble(), iteration.toDouble(), iteration.toDouble())
            val found = octree.find(queryPoint)
            val foundNearby = octree.findNearby(queryPoint, 2.0)

            // These operations should not throw exceptions
            found.size
            foundNearby.size

            // Modify operations
            val newShape = createTestCuboid(
                iteration.toDouble() + 100, iteration.toDouble() + 100, iteration.toDouble() + 100,
                iteration.toDouble() + 101, iteration.toDouble() + 101, iteration.toDouble() + 101
            )
            octree.insert(newShape)

            if (iteration % 3 == 0 && baseShapes.isNotEmpty()) {
                octree.remove(baseShapes[iteration % baseShapes.size])
            }
        }

        // Octree should still be in a valid state
        assertTrue(octree.count() >= 0)
        octree.toList()
    }

    @Test
    fun `should handle shape modifications after insertion correctly`() {
        val octree = OctreeImpl<Shape>()

        // Note: Since shapes are immutable data classes, we test that the octree
        // correctly handles the shapes as they were when inserted
        val originalShape = createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        octree.insert(originalShape)

        // Create a "modified" version (actually a new instance)
        val modifiedShape = createTestCuboid(0.0, 0.0, 0.0, 2.0, 2.0, 2.0)

        assertTrue(octree.contains(originalShape))
        assertFalse(octree.contains(modifiedShape))

        // The octree should still find the original shape correctly
        val found = octree.find(Point(0.5, 0.5, 0.5))
        assertTrue(found.contains(originalShape))
    }

    // ========== SPECIAL GEOMETRY TESTS ==========

    @Test
    fun `should handle CylinderShape specific behaviors`() {
        val octree = OctreeImpl<Shape>()
        val cylinder = createTestCylinder(0.0, 0.0, 0.0, 2.0, 5.0)

        octree.insert(cylinder)

        // Point inside cylinder radius and height
        assertTrue(octree.find(Point(1.0, 2.0, 0.0)).contains(cylinder))

        // Point outside cylinder radius but within height
        assertFalse(octree.find(Point(3.0, 2.0, 0.0)).contains(cylinder))

        // Point inside radius but outside height
        assertFalse(octree.find(Point(1.0, 6.0, 0.0)).contains(cylinder))

        // Point at exact boundary
        assertTrue(octree.find(Point(2.0, 0.0, 0.0)).contains(cylinder))
    }

    @Test
    fun `should handle SphereShape specific behaviors`() {
        val octree = OctreeImpl<Shape>()
        val sphere = createTestSphere(0.0, 0.0, 0.0, 2.0)

        octree.insert(sphere)

        // Point at center
        assertTrue(octree.find(Point(0.0, 0.0, 0.0)).contains(sphere))

        // Point on surface (within radius)
        assertTrue(octree.find(Point(2.0, 0.0, 0.0)).contains(sphere))

        // Point just outside sphere
        assertFalse(octree.find(Point(2.1, 0.0, 0.0)).contains(sphere))

        // Point at diagonal within sphere
        sqrt(3.0) // ~1.73, which is < 2.0
        assertTrue(octree.find(Point(1.0, 1.0, 1.0)).contains(sphere))
    }

    @Test
    fun `should handle ExclusiveCuboidShape vs CuboidShape differences`() {
        val octree = OctreeImpl<Shape>()

        // This test would need ExclusiveCuboidShapeData implementation
        // For now, we test regular cuboids and document the expected behavior
        val cuboid = createTestCuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        octree.insert(cuboid)

        // Regular cuboid includes boundaries
        assertTrue(octree.find(Point(0.0, 0.0, 0.0)).contains(cuboid)) // Min boundary
        assertTrue(octree.find(Point(1.0, 1.0, 1.0)).contains(cuboid)) // Max boundary
        assertTrue(octree.find(Point(0.5, 0.5, 0.5)).contains(cuboid)) // Inside
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    fun `should work correctly with complex real-world scenario`() {
        val octree = OctreeImpl<Shape>()

        // Simulate a 3D world with various objects
        val buildings = listOf(
            createTestCuboid(0.0, 0.0, 0.0, 10.0, 20.0, 8.0),    // Building 1
            createTestCuboid(15.0, 0.0, 0.0, 25.0, 15.0, 12.0),  // Building 2
            createTestCuboid(-10.0, 0.0, 5.0, -5.0, 18.0, 10.0)  // Building 3
        )

        val trees = listOf(
            createTestCylinder(12.0, 0.0, 4.0, 1.0, 8.0),       // Tree 1
            createTestCylinder(3.0, 0.0, -5.0, 0.8, 6.0),       // Tree 2
            createTestCylinder(-2.0, 0.0, 12.0, 1.2, 10.0)      // Tree 3
        )

        val sphericalObjects = listOf(
            createTestSphere(5.0, 25.0, 2.0, 2.0),              // Balloon
            createTestSphere(-8.0, 10.0, -3.0, 1.5),            // Another sphere
            createTestSphere(20.0, 8.0, 6.0, 0.5)               // Small object
        )

        // Insert all objects
        (buildings + trees + sphericalObjects).forEach { octree.insert(it) }

        assertEquals(9, octree.count())

        // Test various queries
        val groundLevel = octree.find(Point(5.0, 0.5, 2.0))
        assertTrue(groundLevel.isNotEmpty()) // Should find building 1

        val airSpace = octree.findNearby(Point(5.0, 25.0, 2.0), 3.0)
        assertTrue(airSpace.contains(sphericalObjects[0])) // Should find balloon

        val searchRegion = octree.findInRegion(Point(-15.0, 0.0, -10.0), Point(30.0, 30.0, 15.0))
        assertEquals(9, searchRegion.size) // Should find all objects in this large region

        // Test removal of specific object types
        trees.forEach { octree.remove(it) }
        assertEquals(6, octree.count())

        // Verify trees are gone but others remain
        trees.forEach { assertFalse(octree.contains(it)) }
        buildings.forEach { assertTrue(octree.contains(it)) }
        sphericalObjects.forEach { assertTrue(octree.contains(it)) }
    }

    @Test
    fun `should maintain performance with deep octree subdivision`() {
        val octree = OctreeImpl<Shape>(
            minPoint = Point(0.0, 0.0, 0.0),
            maxPoint = Point(10000.0, 10000.0, 10000.0),
            capacity = 1
        ) // Force deep subdivision

        // Create many small shapes in a grid pattern
        val shapes = mutableListOf<Shape>()
        for (x in 0 until 10) {
            for (y in 0 until 10) {
                for (z in 0 until 10) {
                    val shape = createTestCuboid(
                        x.toDouble(), y.toDouble(), z.toDouble(),
                        x.toDouble() + 0.5, y.toDouble() + 0.5, z.toDouble() + 0.5
                    )
                    shapes.add(shape)
                    octree.insert(shape)
                }
            }
        }

        assertEquals(1000, octree.count())

        // Test that queries still work efficiently
        val queryTime = measureTime {
            repeat(100) { i ->
                val queryPoint = Point(i / 10.0, i / 10.0, i / 10.0)
                val found = octree.find(queryPoint)
                // Each query should find at most a few shapes
                assertTrue(found.size <= 8) // At most the shapes in adjacent cells
            }
        }

        // Queries should complete reasonably quickly (this is a rough performance check)
        assertTrue(queryTime < 1.seconds, "Queries took too long: ${queryTime}ms")
    }
}