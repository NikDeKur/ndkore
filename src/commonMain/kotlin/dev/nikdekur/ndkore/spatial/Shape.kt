/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.spatial

interface Shape<T> {

    fun getMinPoint(o: T): Point
    fun getMaxPoint(o: T): Point
    fun contains(obj: T, point: Point): Boolean
    fun distanceSquared(from: T, to: Point): Double
    fun intersects(obj: T, min: Point, max: Point): Boolean
}