@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.spatial

data class Point(var x: Int, var y: Int, var z: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Point) return false
        return x == other.x && y == other.y && z == other.z
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }

    inline fun clone() = Point(x, y, z)

    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Point): Point = Point(x - other.x, y - other.y, z - other.z)
    operator fun times(other: Int): Point = Point(x * other, y * other, z * other)
    operator fun div(other: Int): Point = Point(x / other, y / other, z / other)

    fun lengthSquared(): Int {
        return (x * x + y * y + z * z)
    }
    fun lengthSquared2D(): Int {
        return (x * x + z * z)
    }

    fun distanceSquared(x: Int, y: Int, z: Int): Double {
        val a = this.x - x
        val b = this.y - y
        val c = this.z - z
        return (a * a + b * b + c * c).toDouble()
    }

    companion object {
        inline fun middlePoint(point1: Point, point2: Point): Point {
            return Point((point1.x + point2.x) / 2, (point1.y + point2.y) / 2, (point1.z + point2.z) / 2)
        }

        inline fun middlePoint(minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int): Point {
            return Point((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2)
        }

        inline fun distanceSquared(
            x1: Int, y1: Int, z1: Int,
            x2: Int, y2: Int, z2: Int
        ): Double {
            val a = x1 - x2
            val b = y1 - y2
            val c = z1 - z2
            return (a * a + b * b + c * c).toDouble()
        }
    }
}