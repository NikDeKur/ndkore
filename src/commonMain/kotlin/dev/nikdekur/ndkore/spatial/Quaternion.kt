package dev.nikdekur.ndkore.spatial

import kotlinx.serialization.Serializable
import kotlin.math.cos
import kotlin.math.sin

@Serializable
public data class Quaternion(
    val x: Double,
    val y: Double,
    val z: Double,
    val w: Double
) {
    public operator fun times(q: Quaternion): Quaternion = Quaternion(
        w * q.x + x * q.w + y * q.z - z * q.y,
        w * q.y - x * q.z + y * q.w + z * q.x,
        w * q.z + x * q.y - y * q.x + z * q.w,
        w * q.w - x * q.x - y * q.y - z * q.z
    )

    public operator fun times(v: V3): V3 {
        val qVec = V3(x, y, z)
        val t = qVec.cross(v) * 2.0
        return v + (t * w) + qVec.cross(t)
    }

    public fun inverted(): Quaternion = Quaternion(-x, -y, -z, w)

    public companion object {
        public fun fromAxisAngle(axis: V3, angleRad: Double): Quaternion {
            val half = angleRad / 2
            val sinHalf = sin(half)
            val n = axis.normalized()
            return Quaternion(n.x * sinHalf, n.y * sinHalf, n.z * sinHalf, cos(half))
        }
    }
}