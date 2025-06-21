/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("KotlinConstantConditions", "NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.spatial.octree

import dev.nikdekur.ndkore.spatial.Point
import dev.nikdekur.ndkore.spatial.Shape
import dev.nikdekur.ndkore.spatial.middlePoint

/**
 * # OctreeImpl Class
 *
 * This class provides a concrete implementation of the `MutableOctree` interface.
 * It represents a spatial data structure that partitions three-dimensional space
 * into smaller octant recursively, allowing for efficient spatial querying and
 * management of objects within a 3D space.
 *
 * ## Overview
 * The `OctreeImpl` class is designed to handle a dynamic number of elements and manage
 * them hierarchically.
 * It supports insertion, querying, and removal of
 * elements based on their spatial locations.
 * The class also handles splitting of
 * the octree into smaller octant's when the number of elements exceeds a defined capacity.
 *
 * ## Key Concepts
 * - **Bounds Management**: The class manages the bounds of the octree, including minimum
 *   and maximum points, and recalculates these bounds as elements are inserted.
 * - **Spatial Partitioning**: The octree partitions the 3D space into eight octanes, allowing
 *   for efficient searching and organization of spatial data.
 * - **Dynamic Splitting**: When the number of elements in a node exceeds its capacity, the
 *   node splits into eight child nodes to maintain efficiency.
 * - **Node Data Handling**: The class works with `NodeData` objects, which encapsulate the
 *   elements and their spatial shapes.

 * ## Properties
 * - `capacity`: The maximum number of elements a node can hold before it splits.
 * - `minX`, `minY`, `minZ`, `maxX`, `maxY`, `maxZ`: Coordinates defining the bounds of the octree.
 * - `centerX`, `centerY`, `centerZ`: Coordinates of the center of the octree.
 * - `data`: A list of `NodeData` objects stored in the current node.
 * - `children`: An array of child octree nodes.

 * ## Usage
 * `OctreeImpl` is instantiated with a minimum and maximum `Point` to define its initial bounds.
 * It can then be used to insert elements, query for elements within certain regions or
 * distances, and manage the octree's spatial data effectively.

 * Example instantiation and usage:
 * ```kotlin
 * val minPoint = Point(0, 0, 0)
 * val maxPoint = Point(100, 100, 100)
 * val octree = OctreeImpl<MyObject>(minPoint, maxPoint)
 *
 * val shape = Shape(myObject)
 * octree.insert(myObject, shape)
 *
 * val foundObjects = octree.find(Point(50, 50, 50))
 * ```
 *
 * ## Design Considerations
 * - **Capacity Management**: Proper capacity settings are crucial for performance.
 *   A small capacity may lead to frequent splits, while a large capacity may result
 *   in inefficient memory use.
 * - **Thread Safety**: This implementation is not thread-safe.
 * If used in a multithreaded
 *   environment, external synchronization mechanisms should be employed.
 * - **Memory Usage**: Due to its hierarchical nature, memory usage scales with the number
 *   of elements and the depth of the tree.

 * ## Advanced Features
 * - **Custom Iterators**: Provides custom iterators for traversing the octree nodes
 *   and node data objects.
 * - **Clear and Removal**: Methods are available for removing specific elements or
 *   clearing the entire octree.

 * The `OctreeImpl` class is suitable for applications requiring efficient spatial querying
 * and management, such as 3D graphics, simulations, and spatial databases.
 */

public open class OctreeImpl<T : Shape>(
    public val capacity: Int = DEFAULT_CAPACITY,
) : MutableOctree<T> {

    public var minX: Double? = null
    public var minY: Double? = null
    public var minZ: Double? = null
    public var maxX: Double? = null
    public var maxY: Double? = null
    public var maxZ: Double? = null

    public var centerX: Double? = null
    public var centerY: Double? = null
    public var centerZ: Double? = null

    override val min: Point
        get() = Point(minX!!, minY!!, minZ!!)

    override val max: Point
        get() = Point(maxX!!, maxY!!, maxZ!!)

    override val center: Point
        get() = Point(centerX!!, centerY!!, centerZ!!)

    public var size: Int = 0
        private set

    public constructor(minPoint: Point, maxPoint: Point, capacity: Int = DEFAULT_CAPACITY) : this(capacity) {
        setBounds(minPoint, maxPoint)
    }


    public fun setBounds(minPoint: Point, maxPoint: Point) {
        minX = minPoint.x
        minY = minPoint.y
        minZ = minPoint.z

        maxX = maxPoint.x
        maxY = maxPoint.y
        maxZ = maxPoint.z

        centerX = (minX!! + maxX!!) / 2
        centerY = (minY!! + maxY!!) / 2
        centerZ = (minZ!! + maxZ!!) / 2
    }

    public inline val isZoneProvided: Boolean
        get() = minX != null && minY != null && minZ != null && maxX != null && maxY != null && maxZ != null


    public var data: MutableList<T> = ArrayList(capacity)
    public var children: Array<OctreeImpl<T>?> = arrayOfNulls(8)

    public fun isChildrenEmpty(): Boolean = children.all { it == null }
    public inline fun isChildrenNotEmpty(): Boolean = !isChildrenEmpty()


    override fun insert(data: T) {
        val nodeDataMin = data.min
        val minX = minOf(minX ?: nodeDataMin.x, nodeDataMin.x)
        val minY = minOf(minY ?: nodeDataMin.y, nodeDataMin.y)
        val minZ = minOf(minZ ?: nodeDataMin.z, nodeDataMin.z)

        val nodeDataMax = data.max
        val maxX = maxOf(maxX ?: nodeDataMax.x, nodeDataMax.x)
        val maxY = maxOf(maxY ?: nodeDataMax.y, nodeDataMax.y)
        val maxZ = maxOf(maxZ ?: nodeDataMax.z, nodeDataMax.z)
        setBounds(Point(minX, minY, minZ), Point(maxX, maxY, maxZ))

        if (isChildrenNotEmpty()) {
            val index = getIndex(data)
            if (index != -1) {
                children[index]?.insert(data)
                return
            }
        }

        this.data.add(data)

        if (this.data.size > capacity && isChildrenEmpty()) {
            // When a new child (that would be created after split) size is smaller than capacity, it will throw StackOverflowError
            check(minX + capacity < maxX && minY + capacity < maxY && minZ + capacity < maxZ) {
                "Capacity is too small for this octree. Increase capacity or decrease the size of the octree. | Capacity: $capacity"
            }

            split()
            val iterator = this.data.iterator()
            while (iterator.hasNext()) {
                val currentNodeData = iterator.next()
                val index = getIndex(currentNodeData)
                if (index != -1) {
                    children[index]?.insert(currentNodeData)
                    iterator.remove()
                }
            }
        }
    }


    override fun find(point: Point): Collection<T> {
        val result = HashSet<T>()
        val stack = ArrayDeque<OctreeImpl<T>>()
        stack.add(this)

        while (stack.isNotEmpty()) {
            val current = stack.removeFirst()

            extractChildrenNodes(current, stack)

            if (current.data.isNotEmpty()) {
                for (data in current.data) {
                    if (data.contains(point))
                        result.add(data)
                }
            }
        }

        return result
    }

    override fun findNodes(point: Point): Collection<T> {
        val result = HashSet<T>()
        val stack = ArrayDeque<OctreeImpl<T>>()
        stack.add(this)

        while (stack.isNotEmpty()) {
            val currentNode = stack.removeFirst()

            if (currentNode.children.isNotEmpty()) {
                val index = currentNode.getIndex(point)
                if (index != -1) {
                    currentNode.children[index]?.let { stack.add(it) }
                } else {
                    currentNode.children.forEach { child ->
                        child?.let { stack.add(it) }
                    }
                }
            }

            if (currentNode.data.isNotEmpty())
                currentNode.data.filterTo(result) {
                    it.contains(point)
                }
        }

        return result
    }


    override fun findNearby(point: Point, radius: Double): Collection<T> {
        val stack = ArrayDeque<OctreeImpl<T>>()
        val result = HashSet<T>()
        val radiusSquared = radius * radius
        stack.add(this)

        while (stack.isNotEmpty()) {
            val current = stack.removeFirst()

            extractChildrenNodes(current, stack)

            if (current.data.isNotEmpty()) {
                for (data in current.data) {
                    if (data.distanceSquared(point) <= radiusSquared)
                        result.add(data)
                }
            }
        }
        return result
    }

    override fun findNodesNearby(point: Point, radius: Double): Collection<T> {
        val stack = ArrayDeque<OctreeImpl<T>>()
        val result = HashSet<T>()
        val radiusSquared = radius * radius
        stack.add(this)

        while (stack.isNotEmpty()) {
            val current = stack.removeFirst()

            extractChildrenNodes(current, stack)

            if (current.data.isNotEmpty()) {
                current.data.filterTo(result) {
                    it.distanceSquared(point) <= radiusSquared
                }
            }
        }
        return result
    }


    public inline fun extractChildrenNodes(current: OctreeImpl<T>, stack: MutableList<OctreeImpl<T>>) {
        if (current.children.isNotEmpty()) {
            val index = current.getIndex(min, max)
            if (index != -1) {
                val child = current.children[index]
                if (child != null) stack.add(child)
            } else {
                for (child in current.children) {
                    if (child != null) stack.add(child)
                }
            }
        }
    }


    override fun findInRegion(min: Point, max: Point): Collection<T> {
        val result = HashSet<T>()
        val stack = ArrayDeque<OctreeImpl<T>>()
        stack.add(this)

        while (stack.isNotEmpty()) {
            val current = stack.removeFirst()

            extractChildrenNodes(current, stack)

            if (current.data.isNotEmpty()) {
                for (data in current.data) {
                    if (data.intersects(min, max)) {
                        result.add(data)
                    }
                }
            }
        }

        return result
    }

    override fun findNodesInRegion(min: Point, max: Point): HashSet<T> {
        val result = HashSet<T>()
        val stack = ArrayDeque<OctreeImpl<T>>()
        stack.add(this)

        while (stack.isNotEmpty()) {
            val current = stack.removeFirst()

            extractChildrenNodes(current, stack)

            if (current.data.isEmpty()) continue

            current.data.filterTo(result) {
                it.intersects(min, max)
            }
        }

        return result
    }


    public inline fun getIndex(point: Point): Int {
        if (!isZoneProvided) return -1

        val midX = centerX!!
        val midY = centerY!!
        val midZ = centerZ!!

        val xBit = if (point.x >= midX) 0 else 1
        val yBit = if (point.y >= midY) 0 else 1
        val zBit = if (point.z >= midZ) 0 else 1

        return (yBit shl 2) or (zBit shl 1) or xBit
    }

    public inline fun getIndex(min: Point, max: Point): Int {
        val point = min.middlePoint(max)
        return getIndex(point)
    }

    public inline fun getIndex(data: T): Int {
        return getIndex(data.center)
    }

    public inline fun split() {
        val midX = centerX!!
        val midY = centerY!!
        val midZ = centerZ!!

        children[0] = newChild(Point(midX, minY!!, midZ), Point(maxX!!, midY, maxZ!!))
        children[1] = newChild(Point(minX!!, minY!!, midZ), Point(midX, midY, maxZ!!))
        children[2] = newChild(Point(midX, midY, minZ!!), Point(maxX!!, maxY!!, midZ))
        children[3] = newChild(Point(minX!!, midY, minZ!!), Point(midX, maxY!!, midZ))

        children[4] = newChild(Point(midX, minY!!, minZ!!), Point(maxX!!, midY, midZ))
        children[5] = newChild(Point(minX!!, minY!!, minZ!!), Point(midX, midY, midZ))
        children[6] = newChild(Point(midX, midY, midZ), Point(maxX!!, maxY!!, maxZ!!))
        children[7] = newChild(Point(minX!!, midY, midZ), Point(midX, maxY!!, maxZ!!))
    }

    override fun remove(data: T) {
        val value = data
        
        if (children.isNotEmpty()) {
            children.forEach { it?.remove(value) }
        }

        val iterator = this.data.iterator()
        while (iterator.hasNext()) {
            val currentData = iterator.next()
            if (currentData == value) {
                iterator.remove()
                return
            }
        }
    }

    public open fun removeIf(predicate: (T) -> Boolean) {
        if (children.isNotEmpty()) {
            children.forEach { it?.removeIf(predicate) }
        }

        val iterator = data.iterator()
        while (iterator.hasNext()) {
            val currentData = iterator.next()
            if (predicate(currentData)) {
                iterator.remove()
            }
        }
    }

    public override fun clear() {
        data.clear()
        for (index in children.indices) {
            children[index]?.clear()
            children[index] = null
        }
    }

    override fun iterator(): Iterator<T> {
        return OctreeIterator(this)
    }

    public fun iteratorNode(): Iterator<T> {
        return OctreeIteratorNode(this)
    }

    public fun forEachNode(action: (T) -> Unit) {
        return iteratorNode().forEach(action)
    }

    override fun toString(): String {
        return "OctreeImpl(min=($minX, $minY, $minZ), max=($maxX, $maxY, $maxZ), data=$data, children_amount=${children.filterNotNull().size})"
    }


    public inline fun newChild(minPoint: Point, maxPoint: Point): OctreeImpl<T> {
        return OctreeImpl<T>(capacity).apply {
            setBounds(minPoint, maxPoint)
        }
    }

    public companion object {
        public const val DEFAULT_CAPACITY: Int = 10


        public abstract class AbstractIterator<T : Shape, I>(octree: OctreeImpl<T>) : Iterator<I> {
            private val stack = ArrayDeque<OctreeImpl<T>>()
            private var dataIterator: Iterator<T>? = null
            private var current: I? = null

            init {
                stack.add(octree)
            }

            override fun hasNext(): Boolean {
                if (dataIterator?.hasNext() == true) return true
                while (stack.isNotEmpty()) {
                    val currentNode = stack.removeFirst()
                    if (currentNode.data.isNotEmpty() && dataIterator == null) {
                        dataIterator = currentNode.data.iterator()
                        if (dataIterator?.hasNext() == true) return true
                    }
                    for (child in currentNode.children) {
                        child?.let { stack.add(it) }
                    }
                }
                return false
            }

            public abstract fun result(data: T): I

            override fun next(): I {
                if (!hasNext()) throw NoSuchElementException()
                val result = dataIterator!!.next()
                current = null
                return result(result)
            }
        }

        public open class OctreeIterator<T : Shape>(octree: OctreeImpl<T>) : AbstractIterator<T, T>(octree) {
            override fun result(data: T): T {
                return data
            }
        }

        public class OctreeIteratorNode<T : Shape>(octree: OctreeImpl<T>) : AbstractIterator<T, T>(octree) {
            override fun result(data: T): T {
                return data
            }
        }
    }
}