/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.spatial.octree

import dev.nikdekur.ndkore.spatial.Point
import dev.nikdekur.ndkore.spatial.Shape

public data class NodeData<T>(
    val data: T,
    val shape: Shape<T>,
    val min: Point = shape.getMinPoint(data),
    val max: Point = shape.getMinPoint(data),
    val center: Point = Point(
        (min.x + max.x) / 2,
        (min.y + max.y) / 2,
        (min.z + max.z) / 2
    ),
) {

    public inline fun contains(point: Point): Boolean = shape.contains(data, point)
    public inline fun distanceSquared(to: Point): Double = shape.distanceSquared(data, to)
    public inline fun intersects(min: Point, max: Point): Boolean = shape.intersects(data, min, max)

    override fun toString(): String {
        return "NodeData(data=$data, min=$min, max=$max)"
    }
}