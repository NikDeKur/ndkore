/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.spatial.kdtree

import dev.nikdekur.ndkore.spatial.Point

/**
 * A mutable k-d tree interface that extends the basic `KDTree` interface to include modification operations.
 *
 * This interface provides additional methods for modifying the k-d tree. In addition to querying operations,
 * it allows for inserting, removing, and clearing elements within the k-d tree. This is useful for scenarios
 * where the set of points in the tree is dynamic and needs to be updated frequently.
 *
 * ## Properties and Methods
 *
 * The `MutableKDTree` interface includes methods for:
 * - Inserting a new point with an associated value into the k-d tree.
 * - Removing a point and its associated value from the k-d tree.
 * - Clearing all elements from the k-d tree.
 *
 * ## Type Parameters
 *
 * - `T`: The type of the elements stored in the k-d tree. This could be any type, such as a custom data class
 *   or a basic type, that is associated with the k-dimensional points in the tree.
 *
 * ## Usage
 *
 * To use a `MutableKDTree`, you need to implement this interface and provide concrete methods for managing
 * the tree structure. This typically involves implementing the `insert`, `remove`, and `clear` methods
 * to handle changes to the tree.
 *
 * Example:
 * ```
 * class MyMutableKDTree<T> : MutableKDTree<T> {
 *     // Implementation details here
 * }
 * ```
 *
 * ## Note
 *
 * Implementing a mutable k-d tree requires careful management of the tree structure to ensure that
 * operations such as insertion and deletion do not violate the k-d tree properties. The implementation must
 * handle rebalancing or restructuring of the tree as necessary.
 *
 * Additionally, modifications to the k-d tree should be performed with consideration for the impact on query
 * performance. Frequent updates can affect the efficiency of nearest neighbor searches and range queries.
 */
public interface MutableKDTree<T> : KDTree<T> {

    /**
     * Inserts a new point with an associated value into the k-d tree.
     *
     * This method adds a new point to the k-d tree and associates it with the provided value. The tree structure
     * is updated to include the new point while maintaining the k-d tree properties. If a point with the same coordinates
     * already exists in the tree, its value may be updated depending on the implementation specifics.
     *
     * The insertion operation involves traversing the tree to find the appropriate position for the new point, which is
     * then added to the tree while preserving the partitioning scheme of the k-d tree.
     *
     * @param point The point to be inserted into the k-d tree. It represents a location in k-dimensional space.
     * @param value The value to be associated with the point. It can be any type defined by the type parameter `T`.
     *
     * @note Ensure that the tree is properly balanced after insertion to maintain efficient query performance.
     */
    public fun insert(point: Point, value: T)

    /**
     * Removes a point and its associated value from the k-d tree.
     *
     * This method deletes a point from the k-d tree along with its associated value. The tree structure is updated to
     * ensure that the k-d tree properties are preserved after the removal operation. If the point to be removed is not
     * present in the tree, no changes are made.
     *
     * The removal operation involves locating the node containing the point, handling various cases (e.g., leaf node,
     * node with one child, or node with two children), and updating the tree structure accordingly.
     *
     * @param point The point to be removed from the k-d tree. It represents a location in k-dimensional space.
     *
     * @note The removal process may require rebalancing the tree or restructuring it to maintain search efficiency.
     */
    public fun remove(point: Point)

    /**
     * Clears all elements from the k-d tree.
     *
     * This method removes all points and their associated values from the k-d tree, effectively resetting it to an
     * empty state. The tree structure is completely cleared, and the size of the tree is set to zero.
     *
     * The `clear` operation is useful when you need to remove all elements from the tree and start fresh, or when
     * performing operations that require an empty tree.
     *
     * @note After clearing the tree, all search operations will return empty results until new elements are inserted.
     */
    public fun clear()

}