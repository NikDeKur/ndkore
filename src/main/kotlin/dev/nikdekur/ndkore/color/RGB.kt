package dev.nikdekur.ndkore.color

import java.awt.Color

class RGB(
    val red: Float = 0f,
    val green: Float = 0f,
    val blue: Float = 0f,
    val alpha: Float = 255f
) {

    fun toInt(): Int {
        return (red.toInt() shl 16) or (green.toInt() shl 8) or blue.toInt()
    }

    fun toAWTColor(): Color {
        return Color(red / 255, green / 255, blue / 255, alpha / 255)
    }

    fun toHEX(): HEX {
        return HEX(String.format("#%02X%02X%02X", red, green, blue))
    }
}
