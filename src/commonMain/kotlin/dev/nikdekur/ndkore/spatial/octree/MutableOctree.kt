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
interface MutableOctree<T> : Octree<T> {

    /**
     * Inserts a new node data object into the Octree.
     *
     * This method adds the specified node data object into the Octree.
     *
     * @param data The node data object to insert.
     */
    fun insert(data: NodeData<T>)

    /**
     * Inserts a new value with its corresponding shape into the Octree.
     *
     * This method creates a new node data object with the specified value and shape,
     * and then inserts it into the Octree.
     *
     * @param value The value to insert.
     * @param shape The shape corresponding to the value.
     */
    fun insert(value: T, shape: Shape<T>) {
        val nodeData = NodeData(value, shape)
        insert(nodeData)
    }
}