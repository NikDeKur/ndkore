package dev.nikdekur.ndkore.memory

enum class MemoryUnit(val bytes: Long) {
    Byte(1),
    KB(1024),
    MB(1048576),
    GB(1099511627776L),
    TB(GB.bytes * 1024)
    ;

    fun from(unit: MemoryUnit, value: Long): Long {
        if (unit == this)
            return value

        val unitBytes = unit.bytes
        return if (bytes > unitBytes) {
            value / unitBytes
        } else {
            value * bytes
        }
    }
}
