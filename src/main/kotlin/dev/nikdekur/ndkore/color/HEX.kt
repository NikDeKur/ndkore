package dev.nikdekur.ndkore.color


class HEX(val code: String) {
    fun toRGB(): RGB {
        val rgb = code.toInt(16)
        val r = rgb shr 16 and 0xFF
        val g = rgb shr 8 and 0xFF
        val b = rgb and 0xFF
        return RGB(r.toFloat(), g.toFloat(), b.toFloat())
    }
}
