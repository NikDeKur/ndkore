/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.spatial.kdtree

import dev.nikdekur.ndkore.spatial.V3

/**
 * A generic interface representing a k-d tree (k-dimensional tree) data structure.
 *
 * A k-d tree is a binary tree in which every node is a k-dimensional point. This data structure is used for
 * organizing points in a k-dimensional space. It is particularly useful for applications that involve
 * multidimensional search keys, such as range searches and nearest neighbor searches. Each node in the k-d tree
 * represents a point in k-dimensional space and partitions the space into two halves, each subtree representing
 * a subset of the points that fall within that partition.
 *
 * ## Properties and Methods
 *
 * The `KDTree` interface includes methods for:
 * - Finding the nearest neighbor to a given point.
 * - Finding multiple nearest neighbors to a given point.
 * - Performing range searches to find all points within a given radius of a center point.
 * - Performing range searches to find all points within a rectangular box defined by minimum and maximum points.
 *
 * ## Type Parameters
 *
 * - `T`: The type of the elements stored in the k-d tree. This could be any type, such as a custom data class
 *   or a basic type, that is associated with the k-dimensional points in the tree.
 *
 * ## Usage
 *
 * To use a `KDTree`, you typically implement this interface in a concrete class that provides the specific
 * data storage and search algorithms. The implementation will manage the k-d tree structure and handle the
 * operations defined by the interface.
 *
 * Example:
 * ```
 * class MyKDTree<T> : KDTree<T> {
 *     // Implementation details here
 * }
 * ```
 *
 * ## Note
 *
 * The `KDTree` interface does not define how points are stored or managed. This is left to the implementing
 * class. The implementation may use various techniques to balance the tree, manage nodes, and optimize
 * search operations.
 *
 * Additionally, the k-d tree is optimized for specific types of queries. For instance, it is efficient for
 * nearest neighbor searches but may not be as efficient for certain types of range queries compared to other
 * data structures.
 */
public interface KDTree<T> {

    /**
     * The number of points stored in the k-d tree.
     */
    public val size: Int


    /**
     * Finds the nearest neighbor to the given point.
     *
     * This method searches the k-d tree to find the point that is closest to the specified [v3]. It performs
     * a search through the tree and returns the value associated with the closest point found.
     *
     * The search is performed using a traversal algorithm that minimizes the distance to the query point, efficiently
     * narrowing down the possible candidates for the nearest neighbor.
     *
     * @param v3 The point for which to find the nearest neighbor. It represents the query point in k-dimensional space.
     * @return The value associated with the nearest point, or `null` if the tree is empty or no nearest point is found.
     *
     * @note The accuracy of the result depends on the quality of the k-d tree implementation and how well the tree is balanced.
     */
    public fun nearestNeighbor(v3: V3): T?


    /**
     * Finds the `n` nearest neighbors to the given point.
     *
     * This method retrieves the `n` closest points to the specified [v3] from the k-d tree. It returns a list of
     * values associated with these nearest points. The search is performed using a priority queue or similar data structure
     * to maintain the closest neighbors found during the traversal of the k-d tree.
     *
     * The algorithm used ensures that the result list contains the `n` closest points to the query point, ordered by distance.
     *
     * @param v3 The point for which to find the nearest neighbors. It represents the query point in k-dimensional space.
     * @param n The number of nearest neighbors to retrieve. This must be a positive integer.
     * @return A list of `n` values associated with the closest points to the query point. If the tree has fewer than `n`
     *         points, the list will contain fewer elements.
     *
     * @note Performance may vary based on the number of neighbors requested and the structure of the k-d tree. Ensure
     *       the tree is well-balanced for optimal search efficiency.
     */
    public fun nearestNeighbors(v3: V3, n: Int): List<T>


    /**
     * Performs a range search to find all points within a given radius of a center point.
     *
     * This method searches the k-d tree for all points that lie within a spherical region defined by a center point and
     * a radius. It returns a list of values associated with the points that fall inside the specified region.
     *
     * The search is executed using a traversal algorithm that checks whether each point falls within the defined radius
     * from the center point. Points are included in the result list if their distance to the center is less than or equal
     * to the specified radius.
     *
     * @param center The center point of the spherical search region. It represents the center of the sphere in k-dimensional space.
     * @param radius The radius of the spherical search region. This must be a non-negative value.
     * @return A list of values associated with points that fall within the spherical region defined by the center and radius.
     *
     * @note The efficiency of the range search depends on the size of the radius and the distribution of points in the k-d tree.
     *       Consider balancing the tree and optimizing the search strategy to improve performance.
     */
    public fun rangeSearch(center: V3, radius: Double): List<T>


    /**
     * Performs a range search to find all points within a rectangular box defined by minimum and maximum points.
     *
     * This method searches the k-d tree for all points that lie within a rectangular box defined by [min] and [max] points.
     * It returns a list of values associated with the points that fall inside the specified box.
     *
     * The search is conducted by traversing the k-d tree and checking whether each point lies within the box defined by
     * the minimum and maximum bounds. Points are included in the result list if their coordinates fall within the bounds
     * specified by [min] and [max].
     *
     * @param min The minimum point of the rectangular box, representing one corner of the box in k-dimensional space.
     * @param max The maximum point of the rectangular box, representing the opposite corner of the box in k-dimensional space.
     * @return A list of values associated with points that fall within the rectangular box defined by the minimum and maximum points.
     *
     * @note The performance of the range search may be influenced by the size of the box and the distribution of points in the k-d tree.
     *       Proper tree balancing and optimization strategies can enhance search efficiency.
     */
    public fun rangeSearch(min: V3, max: V3): List<T>
}

public inline fun KDTree<*>.isEmpty(): Boolean = size == 0
public inline fun KDTree<*>.isNotEmpty(): Boolean = !isEmpty()