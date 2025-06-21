/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.spatial.octree

import dev.nikdekur.ndkore.spatial.Point
import dev.nikdekur.ndkore.spatial.Shape

/**
 * # Octree Interface
 *
 * This interface represents a generic Octree data structure used for spatial partitioning.
 * An Octree is a tree data structure where each internal node has exactly eight children.
 * Octrees are most often used to partition a three-dimensional space by recursively subdividing it into eight octants.
 * This interface provides methods to find and retrieve elements within the Octree.
 *
 * ## Properties
 * - `min`: The minimum point (corner) of the Octree.
 * - `max`: The maximum point (corner) of the Octree.
 * - `center`: The center point of the Octree.
 *
 * ## Usage
 * Implementing classes should provide concrete definitions for the methods and properties defined in this interface.
 * Example usage:
 *
 * ```kotlin
 * val octree: Octree<MyObject> = OctreeImpl()
 * val point = Point(1, 1, 1)
 * val foundObjects = octree.find(point)
 * ```
 */
public interface Octree<T : Shape> : Iterable<T> {

    /**
     * The minimum point (corner) of the Octree.
     */
    public val min: Point

    /**
     * The maximum point (corner) of the Octree.
     */
    public val max: Point

    /**
     * The center point of the Octree.
     */
    public val center: Point

    /**
     * Finds all elements that contain the given point.
     *
     * This method searches through the Octree to find all elements that
     * include the specified point within their bounds.
     *
     * @param point The point to search for.
     * @return A collection of elements that contain the given point.
     */
    public fun find(point: Point): Collection<T>

    /**
     * Finds all node data objects that contain the given point.
     *
     * This method searches through the Octree to find all node data objects
     * that include the specified point within their bounds.
     *
     * @param point The point to search for.
     * @return A collection of node data objects that contain the given point.
     */
    public fun findNodes(point: Point): Collection<T>

    /**
     * Finds all elements within the specified radius from the given point.
     *
     * This method searches through the Octree to find all elements that are
     * within the specified radius from the given point.
     *
     * @param point The point to search from.
     * @param radius The radius within which to search for elements.
     * @return A collection of elements within the specified radius from the given point.
     */
    public fun findNearby(point: Point, radius: Double): Collection<T>

    /**
     * Finds all node data objects within the specified radius from the given point.
     *
     * This method searches through the Octree to find all node data objects that are
     * within the specified radius from the given point.
     *
     * @param point The point to search from.
     * @param radius The radius within which to search for node data objects.
     * @return A collection of node data objects within the specified radius from the given point.
     */
    public fun findNodesNearby(point: Point, radius: Double): Collection<T>

    /**
     * Finds all elements within the specified region defined by the min and max points.
     *
     * This method searches through the Octree to find all elements that are within
     * the region defined by the min and max points.
     *
     * @param min The minimum point defining the region.
     * @param max The maximum point defining the region.
     * @return A collection of elements within the specified region.
     */
    public fun findInRegion(min: Point, max: Point): Collection<T>

    /**
     * Finds all node data objects within the specified region defined by the min and max points.
     *
     * This method searches through the Octree to find all node data objects that are within
     * the region defined by the min and max points.
     *
     * @param min The minimum point defining the region.
     * @param max The maximum point defining the region.
     * @return A collection of node data objects within the specified region.
     */
    public fun findNodesInRegion(min: Point, max: Point): Collection<T>
}