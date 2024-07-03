/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.map

import dev.nikdekur.ndkore.spatial.Point
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

abstract class SpatialHashGrid<T>(val cellSize: Int) {

    val grid = ConcurrentHashMap<Point, MutableSet<T>>()
    private val newSetGen = { _: Point -> Collections.newSetFromMap(ConcurrentHashMap<T, Boolean>()) }
    private val reusablePoint = Point(0, 0, 0)


    abstract fun contains(obj: T, x: Int, y: Int, z: Int): Boolean
    abstract fun getMinPoint(o: T): Point
    abstract fun getMaxPoint(o: T): Point


    fun insert(value: T, minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int) {
        for (x in minX..maxX) {
            reusablePoint.x = x.div(cellSize)
            for (y in minY..maxY ) {
                reusablePoint.y = y.div(cellSize)
                for (z in minZ..maxZ) {
                    reusablePoint.z = z.div(cellSize)
                    grid.computeIfAbsent(reusablePoint, newSetGen)
                        .add(value)
                }
            }
        }
    }

    inline fun insert(value: T, min: Point, max: Point) = insert(value, min.x, min.y, min.z, max.x, max.y, max.z)
    inline fun insert(value: T) = insert(value, getMinPoint(value), getMaxPoint(value))

    inline fun find(x: Int, y: Int, z: Int): List<T> {
        val cellKey = calcCellKey(x, y, z)
        return grid[cellKey]?.filter { contains(it, x, y, z) } ?: emptyList()
    }
    inline fun find(location: Point): List<T> = find(location.x, location.y, location.z)

    fun findNear(x: Int, y: Int, z: Int, radius: Int): Set<T> {
        val result = HashSet<T>()
        val radiusSquared = radius * radius
        val minX = x - radius
        val minY = y - radius
        val minZ = z - radius
        val maxX = x + radius
        val maxY = y + radius
        val maxZ = z + radius

        for (iX in minX..maxX) {
            val squaredX = (iX - x) * (iX - x)
            for (iY in minY..maxY) {
                val squaredY = (iY - y) * (iY - y)
                for (iZ in minZ..maxZ) {
                    val squaredZ = (iZ - z) * (iZ - z)
                    if (squaredX + squaredY + squaredZ <= radiusSquared) {
                        find(iX, iY, iZ).let {
                            result.addAll(it)
                        }
                    }
                }
            }
        }

        return result
    }

    inline fun findNear(location: Point, radius: Int): Set<T> = findNear(location.x, location.y, location.z, radius)

    inline fun clear() {
        grid.clear()
    }

    inline fun remove(value: T) {
        removeAt(getMinPoint(value), value)
    }

    inline fun removeAt(point: Point, value: T? = null) = removeAt(point.x, point.y, point.z, value)

    fun removeAt(x: Int, y: Int, z: Int, value: T? = null) {
        val cellKey = calcCellKey(x, y, z)
        val cell = grid[cellKey] ?: return
        if (value == null) {
            cell.clear()
        } else {
            cell.remove(value)
        }
    }

    inline fun calcCellKey(x: Int, y: Int, z: Int) = Point(x.div(cellSize), y.div(cellSize), z.div(cellSize))
    inline fun calcCellKey(point: Point): Point = calcCellKey(point.x, point.y, point.z)
}