/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

@file:Suppress("KotlinConstantConditions", "NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package dev.nikdekur.ndkore.spatial

import dev.nikdekur.ndkore.spatial.Point.Companion.distanceSquared
import dev.nikdekur.ndkore.spatial.Point.Companion.middlePoint
import java.util.*

data class NodeData<T>(
    val data: T,
    val minX: Int, val minY: Int, val minZ: Int,
    val maxX: Int, val maxY: Int, val maxZ: Int,
    val centerX: Int = (minX + maxX) / 2,
    val centerY: Int = (minY + maxY) / 2,
    val centerZ: Int = (minZ + maxZ) / 2
) {
    override fun toString(): String {
        return "NodeData(min=($minX, $minY, $minZ), max=($maxX, $maxY, $maxZ)"
    }
}

abstract class Octree<T>(
    val capacity: Int = DEFAULT_CAPACITY
) : Iterable<T> {

    var minX: Int? = null
    var minY: Int? = null
    var minZ: Int? = null
    var maxX: Int? = null
    var maxY: Int? = null
    var maxZ: Int? = null

    var centerX: Int? = null
    var centerY: Int? = null
    var centerZ: Int? = null

    val minPoint: Point
        get() = Point(minX!!, minY!!, minZ!!)

    val maxPoint: Point
        get() = Point(maxX!!, maxY!!, maxZ!!)

    var size: Int = 0
        private set

    constructor(minPoint: Point, maxPoint: Point, capacity: Int = DEFAULT_CAPACITY) : this(capacity) {
        setBounds(minPoint, maxPoint)
    }

    fun intersectsSphere(x: Int, y: Int, z: Int, radiusSquared: Double): Boolean {
        val distanceSquared = distanceSquared(x, y, z, centerX!!, centerY!!, centerZ!!)
        return distanceSquared <= radiusSquared
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
    var children = arrayOfNulls<Octree<T>>(8)

    fun isChildrenEmpty() = children.all { it == null }
    inline fun isChildrenNotEmpty() = !isChildrenEmpty()

    abstract fun getMinPoint(o: T): Point
    abstract fun getMaxPoint(o: T): Point
    abstract fun contains(obj: T, x: Int, y: Int, z: Int): Boolean

    open fun insert(value: T) {
        val min = getMinPoint(value)
        val max = getMaxPoint(value)
        insert(
            NodeData(
                value,
                min.x, min.y, min.z,
                max.x, max.y, max.z,
            )
        )
    }



    open fun insert(nodeData: NodeData<T>) {
        val minX = minOf(minX ?: nodeData.minX, nodeData.minX)
        val minY = minOf(minY ?: nodeData.minY, nodeData.minY)
        val minZ = minOf(minZ ?: nodeData.minZ, nodeData.minZ)

        val maxX = maxOf(maxX ?: nodeData.maxX, nodeData.maxX)
        val maxY = maxOf(maxY ?: nodeData.maxY, nodeData.maxY)
        val maxZ = maxOf(maxZ ?: nodeData.maxZ, nodeData.maxZ)
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
            if (minX + capacity >= maxX || minY + capacity >= maxY || minZ + capacity >= maxZ) {
                throw IllegalStateException("Capacity is too small for this octree. Increase capacity or decrease the size of the octree. | Capacity: $capacity")
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

    open fun findNodes(x: Int, y: Int, z: Int): HashSet<NodeData<T>> {
        val result = HashSet<NodeData<T>>()
        val stack = Stack<Octree<T>>()
        stack.push(this)

        while (stack.isNotEmpty()) {
            val currentNode = stack.pop()

            if (currentNode.children.isNotEmpty()) {
                val index = currentNode.getIndex(x, y, z)
                if (index != -1) {
                    currentNode.children[index]?.let { stack.push(it) }
                } else {
                    currentNode.children.forEach { child ->
                        child?.let { stack.push(it) }
                    }
                }
            }

            if (currentNode.data.isNotEmpty())
                currentNode.data.filterTo(result) { currentNode.contains(it.data, x, y, z) }
        }

        return result
    }

    open fun find(x: Int, y: Int, z: Int): HashSet<T> {
        val result = HashSet<T>()
        val stack = Stack<Octree<T>>()
        stack.push(this)

        while (stack.isNotEmpty()) {
            val current = stack.pop()

            if (isChildrenNotEmpty()) {
                val index = current.getIndex(x, y, z)
                if (index != -1) {
                    val child = current.children[index]
                    if (child != null) stack.push(child)
                } else {
                    for (child in current.children) {
                        if (child != null) stack.push(child)
                    }
                }
            }

            if (current.data.isNotEmpty()) {
                for (nodeData in current.data) {
                    if (current.contains(nodeData.data, x, y, z))
                        result.add(nodeData.data)
                }
            }
        }

        return result
    }

    inline fun find(point: Point): HashSet<T> = find(point.x, point.y, point.z)

    /**
     * Find all objects in the octree that are within a given radius
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param radius radius
     * @return a set of objects
     */
    open fun findNearby(x: Int, y: Int, z: Int, radius: Double): HashSet<T> {
        val stack = Stack<Octree<T>>()
        val result = HashSet<T>()
        val radiusSquared = radius * radius
        stack.push(this)

        while (stack.isNotEmpty()) {
            val current = stack.pop()
            if (isChildrenNotEmpty()) {
                val index = current.getIndex(x, y, z)
                if (index != -1) {
                    val child = current.children[index]
                    if (child != null) stack.push(child)
                } else {
                    for (child in current.children) {
                        if (child != null) stack.push(child)
                    }
                }
            }

            if (current.data.isNotEmpty()) {
                for (nodeData in current.data) {
                    if (distanceSquared(x, y, z, nodeData.centerX, nodeData.centerY, nodeData.centerZ) <= radiusSquared)
                        result.add(nodeData.data)
                }
            }
        }
        return result
    }

    /**
     * Find all objects in the octree that are intersecting with the given region
     */
    open fun findNodesInRegion(minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int): HashSet<NodeData<T>> {
        val result = HashSet<NodeData<T>>()
        val stack = Stack<Octree<T>>()
        stack.push(this)

        while (stack.isNotEmpty()) {
            val current = stack.pop()

            if (current.children.isNotEmpty()) {
                val index = current.getIndex(minX, minY, minZ, maxX, maxY, maxZ)
                if (index != -1) {
                    val child = current.children[index]
                    if (child != null) stack.push(child)
                } else {
                    for (child in current.children) {
                        if (child != null) stack.push(child)
                    }
                }
            }

            if (current.data.isNotEmpty()) {
                for (nodeData in current.data) {
                    if (nodeData.maxX >= minX && nodeData.minX <= maxX &&
                        nodeData.maxY >= minY && nodeData.minY <= maxY &&
                        nodeData.maxZ >= minZ && nodeData.minZ <= maxZ
                    ) {
                        result.add(nodeData)
                    }
                }
            }
        }

        return result
    }

    inline fun findInRegion(minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int): HashSet<T> {
        val result = HashSet<T>()
        val stack = Stack<Octree<T>>()
        stack.push(this)

        while (stack.isNotEmpty()) {
            val current = stack.pop()

            if (current.children.isNotEmpty()) {
                val index = current.getIndex(minX, minY, minZ, maxX, maxY, maxZ)
                if (index != -1) {
                    val child = current.children[index]
                    if (child != null) stack.push(child)
                } else {
                    for (child in current.children) {
                        if (child != null) stack.push(child)
                    }
                }
            }

            if (current.data.isNotEmpty()) {
                for (nodeData in current.data) {
                    if (nodeData.maxX >= minX && nodeData.minX <= maxX &&
                        nodeData.maxY >= minY && nodeData.minY <= maxY &&
                        nodeData.maxZ >= minZ && nodeData.minZ <= maxZ
                    ) {
                        result.add(nodeData.data)
                    }
                }
            }
        }

        return result
    }


//    fun findNear(x: Int, y: Int, z: Int, radius: Double): HashSet<T> {
//        val result = HashSet<T>()
//        val distanceSquared = radius * radius
//        forEachNode {
//            val distance = Point(x, y, z).distanceSquared(it.centerX, it.centerY, it.centerZ)
//            if (distance <= distanceSquared)
//                result.add(it.data)
//        }
//
//        return result
//    }




    inline fun getIndex(x: Int, y: Int, z: Int): Int {
        if (!isZoneProvided) return -1

        val midX = centerX!!
        val midY = centerY!!
        val midZ = centerZ!!

        return when {
            y >= midY -> {
                if (z >= midZ) {
                    if (x >= midX) 0 else 1
                } else {
                    if (x >= midX) 2 else 3
                }
            }
            else -> {
                if (z >= midZ) {
                    if (x >= midX) 4 else 5
                } else {
                    if (x >= midX) 6 else 7
                }
            }
        }
    }

    inline fun getIndex(minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int): Int {
        val point = middlePoint(minX, minY, minZ, maxX, maxY, maxZ)
        return getIndex(point.x, point.y, point.z)
    }
    inline fun getIndex(nodeData: NodeData<T>): Int {
        return getIndex(nodeData.centerX, nodeData.centerY, nodeData.centerZ)
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
        return "Octree(min=($minX, $minY, $minZ), max=($maxX, $maxY, $maxZ), data=$data, children_amount=${children.filterNotNull().size})"
    }


    inline fun newChild(minPoint: Point, maxPoint: Point): Octree<T> {
        return new(::getMinPoint, ::getMaxPoint, ::contains, capacity).apply { setBounds(minPoint, maxPoint) }
    }

    companion object {
        const val DEFAULT_CAPACITY = 10

        class OctreeIterator<T>(octree: Octree<T>) : Iterator<T> {
            private val stack = Stack<Octree<T>>()
            private var dataIterator: Iterator<NodeData<T>>? = null
            private var current: T? = null

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

            override fun next(): T {
                if (!hasNext()) throw NoSuchElementException()
                val result = dataIterator!!.next().data
                current = null
                return result
            }
        }

        class OctreeIteratorNode<T>(octree: Octree<T>) : Iterator<NodeData<T>> {
            private val stack = Stack<Octree<T>>()
            private var dataIterator: Iterator<NodeData<T>>? = null
            private var current: NodeData<T>? = null

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

            override fun next(): NodeData<T> {
                if (!hasNext()) throw NoSuchElementException()
                val result = dataIterator!!.next()
                current = null
                return result
            }
        }


        fun <T> new(minPoint: (T) -> Point, maxPoint: (T) -> Point, contains: (T, Int, Int, Int) -> Boolean, capacity: Int = DEFAULT_CAPACITY): Octree<T> {
            return object : Octree<T>(capacity) {
                override inline fun getMinPoint(o: T) = minPoint(o)
                override inline fun getMaxPoint(o: T) = maxPoint(o)
                override inline fun contains(obj: T, x: Int, y: Int, z: Int): Boolean = contains(obj, x, y, z)
            }
        }
    }
}