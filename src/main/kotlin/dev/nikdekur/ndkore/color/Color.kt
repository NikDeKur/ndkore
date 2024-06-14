package dev.nikdekur.ndkore.color

import dev.nikdekur.ndkore.ext.Patterns

class Color {
    private var _hex: HEX? = null
    val hex: HEX
        get() {
            if (_hex == null) {
                _hex = _rgb!!.toHEX()
            }
            return _hex!!
        }

    private var _rgb: RGB? = null
    val rgb: RGB
        get() {
            if (_rgb == null) {
                _rgb = _hex!!.toRGB()
            }
            return _rgb!!
        }





    constructor(hex: HEX) {
        this._hex = hex
        _rgb = null
    }

    constructor(rgb: RGB) {
        _hex = null
        this._rgb = rgb
    }






    fun toAWTColor(): java.awt.Color {
        return rgb.toAWTColor()
    }







    companion object {
        @JvmStatic
        fun fromHEX(code: String): Color {
            val hex = HEX(code)
            return Color(hex)
        }

        @JvmStatic
        fun fromRGB(r: Int, g: Int, b: Int): Color {
            val rgb = RGB(r.toFloat(), g.toFloat(), b.toFloat())
            return Color(rgb)
        }



        @JvmStatic
        fun recognizeColor(text: String): Color {
            for (rpgPattern in Patterns.ALL_RGB) {
                val matcher = rpgPattern.matcher(text)
                if (matcher.find()) {
                    val r = matcher.group(1).toInt()
                    val g = matcher.group(2).toInt()
                    val b = matcher.group(3).toInt()
                    return fromRGB(r, g, b)
                }
            }
            for (hexPattern in Patterns.ALL_HEX) {
                val matcher = hexPattern.matcher(text)
                if (matcher.find()) {
                    val value = matcher.group(1)
                    return fromHEX(value)
                }
            }
            throw IllegalArgumentException(text)
        }
    }
}
