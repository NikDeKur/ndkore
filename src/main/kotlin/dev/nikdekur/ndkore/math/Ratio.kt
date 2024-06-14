package dev.nikdekur.ndkore.math

class Ratio {
    var ratio: List<Int>

    constructor(vararg ratio: Int) {
        this.ratio = ratio.toList()
    }

    constructor(ratio: List<Int>) {
        this.ratio = ratio
    }

    fun split(number: Int): List<Int> {
        val oneEl: Int = number / ratio.sum()
        return ratio.map { it * oneEl }
    }
}
