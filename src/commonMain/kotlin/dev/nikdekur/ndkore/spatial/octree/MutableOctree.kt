/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.spatial.octree

import dev.nikdekur.ndkore.spatial.Shape


/**
 * # MutableOctree Interface
 *
 * This interface extends the Octree interface by adding mutability.
 * It provides methods to insert new data into the Octree.
 *
 * ## Usage
 * Implementing classes should provide concrete definitions for the methods defined in this interface.
 * Example usage:
 *
 * ```kotlin
 * val mutableOctree: MutableOctree<MyObject> = OctreeImpl()
 * val data = NodeData(myObject, myShape)
 * mutableOctree.insert(data)
 * ```
 */
public interface MutableOctree<T : Shape> : Octree<T> {

    /**
     * Inserts a new node data object into the Octree.
     *
     * This method adds the specified node data object into the Octree.
     *
     * @param data The node data object to insert.
     */
    public fun insert(data: T)


    /**
     * Removes a node data object from the Octree.
     *
     * This method removes the specified node data object from the Octree.
     *
     * @param data The node data object to remove.
     */
    public fun remove(data: T)


    /**
     * Clears the Octree.
     *
     * This method removes all data from the Octree.
     */
    public fun clear()
}
