/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.spatial.kdtree

import dev.nikdekur.ndkore.spatial.Point
import java.util.LinkedList
import kotlin.math.pow

/**
 * A concrete implementation of the `MutableKDTree` interface using a k-d tree data structure.
 *
 * This class provides a specific implementation of a mutable k-d tree. It supports operations for
 * inserting, removing, and querying points in a k-dimensional space. The implementation is optimized
 * for common operations such as nearest neighbor searches, nearest neighbors queries, and range searches.
 *
 * ## Structure
 *
 * The k-d tree is represented using a binary tree where each node contains a point in 3-dimensional space
 * (x, y, z) and associated value. The tree is partitioned based on the dimensions of the points to efficiently
 * support range queries and nearest neighbor searches.
 *
 * - **Nodes**: Each node in the tree holds a point and its associated value. Nodes are organized to partition
 *   the space into regions that allow for efficient querying.
 * - **Root**: The top node of the tree from which all operations originate.
 * - **Depth**: The depth of a node in the tree determines the dimension by which to partition the space.
 *
 * ## Methods
 *
 * The class provides methods for:
 * - **Insertion**: Adding new points to the tree and updating the tree structure as necessary.
 * - **Removal**: Removing points from the tree while maintaining the k-d tree properties.
 * - **Nearest Neighbor Search**: Finding the closest point to a given query point.
 * - **Nearest Neighbors Search**: Finding multiple nearest neighbors to a given query point.
 * - **Range Search**: Finding all points within a specified radius from a center point or within a rectangular box.
 * - **Clearing**: Removing all points from the tree.
 *
 * ## Type Parameters
 *
 * - `T`: The type of the elements stored in the k-d tree. This type parameter allows the tree to store any
 *   type of value associated with each point.
 *
 * ## Usage
 *
 * To use the `KDTreeImpl`, you create an instance of the class and interact with it using the provided methods.
 * You can insert points, remove points, and perform various queries as needed.
 *
 * Example:
 * ```
 * val tree = KDTreeImpl<MyDataClass>()
 * tree.insert(Point(1.0, 2.0, 3.0), myData)
 * val nearest = tree.nearestNeighbor(Point(1.0, 2.0, 3.0))
 * ```
 *
 * ## Note
 *
 * This implementation is designed to handle 3-dimensional points. For other dimensions, modifications to
 * the implementation would be necessary. The implementation focuses on maintaining the k-d tree properties
 * while providing efficient search operations. Performance considerations include balancing the tree and optimizing
 * search algorithms to handle various types of queries efficiently.
 *
 * When using this implementation, consider the performance implications of frequent insertions and deletions
 * as they may affect the efficiency of subsequent queries. Additionally, ensure that the point and value types
 * used with the tree are compatible with the operations defined.
 */
open class KDTreeImpl<T> : MutableKDTree<T> {
    private var root: Node<T>? = null

    override var size: Int = 0
        protected set

    override fun insert(point: Point, value: T) {
        var node = root
        var depth = 0
        while (node != null) {
            val cd = depth % 3
            if (comparePoint(point, node.point, cd) < 0) {
                if (node.left == null) {
                    node.left = Node(point, value)
                    size++
                    return
                }
                node = node.left
            } else {
                if (node.right == null) {
                    node.right = Node(point, value)
                    size++
                    return
                }
                node = node.right
            }
            depth++
        }
        root = Node(point, value)
        size++
    }


    override fun remove(point: Point) {
        val stack = ArrayDeque<Pair<Node<T>?, Int>>()
        var node = root
        var parent: Node<T>? = null
        var direction: Boolean? = null // true for the left, false for the right
        var depth = 0

        // Find the node to remove
        while (node != null) {
            val cd = depth % 3
            if (node.point == point) {
                break
            }

            stack.addLast(node to depth)
            parent = node
            direction = comparePoint(point, node.point, cd) < 0
            node = if (direction == true) node.left else node.right
            depth++
        }

        if (node == null) {
            // Node not found
            return
        }

        // Handle the case where the node to remove is a leaf node
        fun removeLeaf() {
            if (parent != null) {
                if (direction == true) parent.left = null
                else parent.right = null
            } else {
                root = null
            }
        }

        // Remove the node
        fun removeNodeWithSingleChild() {
            val child = if (node.left != null) node.left else node.right
            if (parent != null) {
                if (direction == true) parent.left = child
                else parent.right = child
            } else {
                root = child
            }
        }

        // Replace the node with the minimum node in the subtree
        fun replaceWithMinNode() {
            val min = findMin(node.right!!, (depth + 1) % 3, depth + 1)
            node.point = min!!.point
            node.value = min.value
            node.right = remove(node.right, min.point, depth + 1).first
        }

        // Determine which removal case applies
        when {
            node.left == null && node.right == null -> removeLeaf()
            node.left == null || node.right == null -> removeNodeWithSingleChild()
            else -> replaceWithMinNode()
        }

        size--
    }


    private fun remove(node: Node<T>?, point: Point, depth: Int): Pair<Node<T>?, Boolean> {
        if (node == null) return null to false

        val cd = depth % 3
        return when {
            node.point == point -> removeNode(node, depth)
            comparePoint(point, node.point, cd) < 0 -> {
                val (newLeft, removed) = remove(node.left, point, depth + 1)
                node.left = newLeft
                node to removed
            }

            else -> {
                val (newRight, removed) = remove(node.right, point, depth + 1)
                node.right = newRight
                node to removed
            }
        }
    }

    private fun removeNode(node: Node<T>, depth: Int): Pair<Node<T>?, Boolean> {
        return when {
            node.right != null -> {
                val min = findMin(node.right, depth % 3, depth + 1)
                node.point = min!!.point
                node.value = min.value
                node.right = remove(node.right, min.point, depth + 1).first
                node to true
            }

            node.left != null -> {
                val min = findMin(node.left, depth % 3, depth + 1)
                node.point = min!!.point
                node.value = min.value
                node.right = remove(node.left, min.point, depth + 1).first
                node.left = null
                node to true
            }

            else -> null to true
        }
    }

    private fun findMin(node: Node<T>?, dim: Int, depth: Int): Node<T>? {
        if (node == null) return null

        val cd = depth % 3
        return if (cd == dim) {
            node.left?.let { findMin(it, dim, depth + 1) } ?: node
        } else {
            minNode(node, findMin(node.left, dim, depth + 1), findMin(node.right, dim, depth + 1), dim)
        }
    }

    private inline fun minNode(x: Node<T>?, y: Node<T>?, z: Node<T>?, dim: Int): Node<T>? {
        return listOfNotNull(x, y, z).minByOrNull { compare(it.point, Point(0, 0, 0), dim) }
    }

    private inline fun compare(a: Point, b: Point, dim: Int): Int {
        return when (dim) {
            0 -> a.x.compareTo(b.x)
            1 -> a.y.compareTo(b.y)
            else -> a.z.compareTo(b.z)
        }
    }

    private inline fun comparePoint(a: Point, b: Point, cd: Int): Int {
        return when (cd) {
            0 -> a.x.compareTo(b.x)
            1 -> a.y.compareTo(b.y)
            else -> a.z.compareTo(b.z)
        }
    }

    override fun nearestNeighbor(point: Point): T? {
        var best: Node<T>? = null
        var node = root
        var depth = 0
        val stack = ArrayDeque<Triple<Node<T>?, Int, Int>>()
        while (node != null || stack.isNotEmpty()) {
            if (node != null) {
                val cd = depth % 3
                val nextBranch = if (comparePoint(point, node.point, cd) < 0) node.left else node.right
                val otherBranch = if (nextBranch == node.left) node.right else node.left

                best = closerDistance(point, best, node)
                if (nextBranch != null) {
                    stack.add(Triple(otherBranch, depth + 1, 0))
                    node = nextBranch
                    depth++
                } else {
                    node = null
                }
            } else {
                val (nextNode, nextDepth, _) = stack.removeLast()
                node = nextNode
                depth = nextDepth
            }
        }

        return best?.value
    }


    private inline fun distanceToSquared(p: Point, n: Point, cd: Int): Double {
        return when (cd) {
            0 -> (p.x - n.x).toDouble().pow(2)
            1 -> (p.y - n.y).toDouble().pow(2)
            else -> (p.z - n.z).toDouble().pow(2)
        }
    }

    private inline fun closerDistance(p: Point, n1: Node<T>?, n2: Node<T>?): Node<T>? {
        if (n1 == null) return n2
        if (n2 == null) return n1

        val d1 = p.distanceSquared(n1.point)
        val d2 = p.distanceSquared(n2.point)

        return if (d1 < d2) n1 else n2
    }

    override fun nearestNeighbors(point: Point, n: Int): List<T> {
        val neighbors = LinkedList<Node<T>>()
        val stack = ArrayDeque<Triple<Node<T>?, Int, Int>>()
        stack.add(Triple(root, 0, 0))
        while (stack.isNotEmpty()) {
            val (node, depth, _) = stack.removeLast()
            if (node == null) continue

            val cd = depth % 3
            val nextBranch = if (comparePoint(point, node.point, cd) < 0) node.left else node.right
            val otherBranch = if (nextBranch == node.left) node.right else node.left

            if (neighbors.size < n) {
                neighbors.add(node)
                neighbors.sortBy { it.point.distanceSquared(point) }
            } else if (node.point.distanceSquared(point) < neighbors.last().point.distanceSquared(point)) {
                neighbors[neighbors.size - 1] = node
                neighbors.sortBy { it.point.distanceSquared(point) }
            }

            stack.add(Triple(nextBranch, depth + 1, 0))
            if (neighbors.size < n || distanceToSquared(point, node.point, cd) < neighbors.last().point.distanceSquared(
                    point
                )
            ) {
                stack.add(Triple(otherBranch, depth + 1, 0))
            }
        }

        return neighbors.map { it.value }
    }


    override fun rangeSearch(center: Point, radius: Double): List<T> {
        val result = LinkedList<T>()
        val stack = ArrayDeque<Pair<Node<T>?, Int>>()
        stack.add(root to 0)
        while (stack.isNotEmpty()) {
            val (node, depth) = stack.removeLast()
            if (node == null) continue

            if (node.point.distanceSquared(center) <= radius.pow(2)) {
                result.add(node.value)
            }

            val cd = depth % 3
            if (distanceToSquared(center, node.point, cd) <= radius.pow(2)) {
                stack.add(node.left to depth + 1)
                stack.add(node.right to depth + 1)
            } else {
                val nextBranch = if (comparePoint(center, node.point, cd) < 0) node.left else node.right
                stack.add(nextBranch to depth + 1)
            }
        }

        return result
    }

    override fun rangeSearch(min: Point, max: Point): List<T> {
        val result = LinkedList<T>()
        val stack = ArrayDeque<Pair<Node<T>?, Int>>()

        stack.add(root to 0)
        while (stack.isNotEmpty()) {
            val (node, depth) = stack.removeLast()
            if (node == null) continue

            if (isInBox(node.point, min, max)) {
                result.add(node.value)
            }

            val cd = depth % 3
            if (comparePoint(min, node.point, cd) <= 0) {
                stack.add(node.left to depth + 1)
            }
            if (comparePoint(max, node.point, cd) >= 0) {
                stack.add(node.right to depth + 1)
            }
        }

        return result
    }


    private inline fun isInBox(p: Point, min: Point, max: Point): Boolean {
        return p >= min && p <= max
    }

    override fun clear() {
        root = null
        size = 0
    }
}
