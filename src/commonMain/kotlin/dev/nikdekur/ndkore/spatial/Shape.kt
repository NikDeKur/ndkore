/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.spatial

public interface Shape<T> {

    public fun getMinPoint(o: T): Point
    public fun getMaxPoint(o: T): Point
    public fun contains(obj: T, point: Point): Boolean
    public fun distanceSquared(from: T, to: Point): Double
    public fun intersects(obj: T, min: Point, max: Point): Boolean
}