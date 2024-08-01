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
import dev.nikdekur.ndkore.spatial.Point.Companion.middlePoint
import java.util.*

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

open class OctreeImpl<T>(
    val capacity: Int = DEFAULT_CAPACITY,
) : MutableOctree<T> {

    var minX: Int? = null
    var minY: Int? = null
    var minZ: Int? = null
    var maxX: Int? = null
    var maxY: Int? = null
    var maxZ: Int? = null

    var centerX: Int? = null
    var centerY: Int? = null
    var centerZ: Int? = null

    override val min: Point
        get() = Point(minX!!, minY!!, minZ!!)

    override val max: Point
        get() = Point(maxX!!, maxY!!, maxZ!!)

    override val center: Point
        get() = Point(centerX!!, centerY!!, centerZ!!)

    var size: Int = 0
        private set

    constructor(minPoint: Point, maxPoint: Point, capacity: Int = DEFAULT_CAPACITY) : this(capacity) {
        setBounds(minPoint, maxPoint)
    }


    fun setBounds(minPoint: Point, maxPoint: Point) {
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

    val isZoneProvided
        get() = minX != null && minY != null && minZ != null && maxX != null && maxY != null && maxZ != null


    var data = ArrayList<NodeData<T>>(capacity)
    var children = arrayOfNulls<OctreeImpl<T>>(8)

    fun isChildrenEmpty() = children.all { it == null }
    inline fun isChildrenNotEmpty() = !isChildrenEmpty()


    override fun insert(nodeData: NodeData<T>) {
        val nodeDataMin = nodeData.min
        val minX = minOf(minX ?: nodeDataMin.x, nodeDataMin.x)
        val minY = minOf(minY ?: nodeDataMin.y, nodeDataMin.y)
        val minZ = minOf(minZ ?: nodeDataMin.z, nodeDataMin.z)

        val nodeDataMax = nodeData.max
        val maxX = maxOf(maxX ?: nodeDataMax.x, nodeDataMax.x)
        val maxY = maxOf(maxY ?: nodeDataMax.y, nodeDataMax.y)
        val maxZ = maxOf(maxZ ?: nodeDataMax.z, nodeDataMax.z)
        setBounds(Point(minX, minY, minZ), Point(maxX, maxY, maxZ))

        if (isChildrenNotEmpty()) {
            val index = getIndex(nodeData)
            if (index != -1) {
                children[index]?.insert(nodeData)
                return
            }
        }

        data.add(nodeData)

        if (data.size > capacity && isChildrenEmpty()) {
            // When a new child (that would be created after split) size is smaller than capacity, it will throw StackOverflowError
            check(minX + capacity < maxX && minY + capacity < maxY && minZ + capacity < maxZ) {
                "Capacity is too small for this octree. Increase capacity or decrease the size of the octree. | Capacity: $capacity"
            }

            split()
            val iterator = data.iterator()
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
        stack.push(this)

        while (stack.isNotEmpty()) {
            val current = stack.pop()

            extractChildrenNodes(current, stack)

            if (current.data.isNotEmpty()) {
                for (nodeData in current.data) {
                    if (nodeData.contains(point))
                        result.add(nodeData.data)
                }
            }
        }

        return result
    }

    override fun findNodes(point: Point): Collection<NodeData<T>> {
        val result = HashSet<NodeData<T>>()
        val stack = Stack<OctreeImpl<T>>()
        stack.push(this)

        while (stack.isNotEmpty()) {
            val currentNode = stack.pop()

            if (currentNode.children.isNotEmpty()) {
                val index = currentNode.getIndex(point)
                if (index != -1) {
                    currentNode.children[index]?.let { stack.push(it) }
                } else {
                    currentNode.children.forEach { child ->
                        child?.let { stack.push(it) }
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
        stack.push(this)

        while (stack.isNotEmpty()) {
            val current = stack.pop()

            extractChildrenNodes(current, stack)

            if (current.data.isNotEmpty()) {
                for (nodeData in current.data) {
                    if (nodeData.distanceSquared(point) <= radiusSquared)
                        result.add(nodeData.data)
                }
            }
        }
        return result
    }

    override fun findNodesNearby(point: Point, radius: Double): Collection<NodeData<T>> {
        val stack = ArrayDeque<OctreeImpl<T>>()
        val result = HashSet<NodeData<T>>()
        val radiusSquared = radius * radius
        stack.push(this)

        while (stack.isNotEmpty()) {
            val current = stack.pop()

            extractChildrenNodes(current, stack)

            if (current.data.isNotEmpty()) {
                current.data.filterTo(result) {
                    it.distanceSquared(point) <= radiusSquared
                }
            }
        }
        return result
    }


    inline fun extractChildrenNodes(current: OctreeImpl<T>, stack: Deque<OctreeImpl<T>>) {
        if (current.children.isNotEmpty()) {
            val index = current.getIndex(min, max)
            if (index != -1) {
                val child = current.children[index]
                if (child != null) stack.push(child)
            } else {
                for (child in current.children) {
                    if (child != null) stack.push(child)
                }
            }
        }
    }


    override fun findInRegion(min: Point, max: Point): Collection<T> {
        val result = HashSet<T>()
        val stack = ArrayDeque<OctreeImpl<T>>()
        stack.push(this)

        while (stack.isNotEmpty()) {
            val current = stack.pop()

            extractChildrenNodes(current, stack)

            if (current.data.isNotEmpty()) {
                for (nodeData in current.data) {
                    println(nodeData)
                    println(this.min)
                    println(this.max)
                    if (nodeData.intersects(min, max)) {
                        result.add(nodeData.data)
                    }
                }
            }
        }

        return result
    }

    override fun findNodesInRegion(min: Point, max: Point): HashSet<NodeData<T>> {
        val result = HashSet<NodeData<T>>()
        val stack = ArrayDeque<OctreeImpl<T>>()
        stack.push(this)

        while (stack.isNotEmpty()) {
            val current = stack.pop()

            extractChildrenNodes(current, stack)

            @Suppress("UsePropertyAccessSyntax")
            if (current.data.isEmpty()) continue

            current.data.filterTo(result) {
                it.intersects(min, max)
            }
        }

        return result
    }


    inline fun getIndex(point: Point): Int {
        if (!isZoneProvided) return -1

        val midX = centerX!!
        val midY = centerY!!
        val midZ = centerZ!!

        val xBit = if (point.x >= midX) 0 else 1
        val yBit = if (point.y >= midY) 0 else 1
        val zBit = if (point.z >= midZ) 0 else 1

        return (yBit shl 2) or (zBit shl 1) or xBit
    }

    inline fun getIndex(min: Point, max: Point): Int {
        val point = middlePoint(min, max)
        return getIndex(point)
    }
    inline fun getIndex(nodeData: NodeData<T>): Int {
        return getIndex(nodeData.center)
    }

    inline fun split() {
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


    open fun remove(value: T) {
        if (children.isNotEmpty()) {
            children.forEach { it?.remove(value) }
        }

        val iterator = data.iterator()
        while (iterator.hasNext()) {
            val currentNodeData = iterator.next()
            if (currentNodeData.data == value) {
                iterator.remove()
                return
            }
        }
    }

    open fun removeIf(predicate: (T) -> Boolean) {
        if (children.isNotEmpty()) {
            children.forEach { it?.removeIf(predicate) }
        }

        val iterator = data.iterator()
        while (iterator.hasNext()) {
            val currentNodeData = iterator.next()
            if (predicate(currentNodeData.data)) {
                iterator.remove()
            }
        }
    }

    open fun clear() {
        data.clear()
        for (index in children.indices) {
            children[index]?.clear()
            children[index] = null
        }
    }

    override fun iterator(): Iterator<T> {
        return OctreeIterator(this)
    }

    fun iteratorNode(): Iterator<NodeData<T>> {
        return OctreeIteratorNode(this)
    }

    fun forEachNode(action: (NodeData<T>) -> Unit) {
        return iteratorNode().forEach(action)
    }

    override fun toString(): String {
        return "OctreeImpl(min=($minX, $minY, $minZ), max=($maxX, $maxY, $maxZ), data=$data, children_amount=${children.filterNotNull().size})"
    }


    inline fun newChild(minPoint: Point, maxPoint: Point): OctreeImpl<T> {
        return OctreeImpl<T>(capacity).apply {
            setBounds(minPoint, maxPoint)
        }
    }

    companion object {
        const val DEFAULT_CAPACITY = 10


        abstract class AbstractIterator<T, I>(octree: OctreeImpl<T>) : Iterator<I> {
            private val stack = Stack<OctreeImpl<T>>()
            private var dataIterator: Iterator<NodeData<T>>? = null
            private var current: I? = null

            init {
                stack.push(octree)
            }

            override fun hasNext(): Boolean {
                if (dataIterator?.hasNext() == true) return true
                while (stack.isNotEmpty()) {
                    val currentNode = stack.pop()
                    if (currentNode.data.isNotEmpty() && dataIterator == null) {
                        dataIterator = currentNode.data.iterator()
                        if (dataIterator?.hasNext() == true) return true
                    }
                    for (child in currentNode.children) {
                        child?.let { stack.push(it) }
                    }
                }
                return false
            }

            abstract fun result(data: NodeData<T>): I

            override fun next(): I {
                if (!hasNext()) throw NoSuchElementException()
                val result = dataIterator!!.next()
                current = null
                return result(result)
            }
        }

        open class OctreeIterator<T>(octree: OctreeImpl<T>) : AbstractIterator<T, T>(octree) {
            override fun result(data: NodeData<T>): T {
                return data.data
            }
        }

        class OctreeIteratorNode<T>(octree: OctreeImpl<T>) : AbstractIterator<T, NodeData<T>>(octree) {
            override fun result(data: NodeData<T>): NodeData<T> {
                return data
            }
        }
    }
}