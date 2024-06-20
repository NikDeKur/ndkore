package dev.nikdekur.ndkore.memory


open class MemoryAmount(val unit: MemoryUnit, val amount: Long) {
    class AmountBytes(amount: Long) : MemoryAmount(MemoryUnit.Byte, amount)
    class AmountKB(amount: Long) : MemoryAmount(MemoryUnit.KB, amount)
    class AmountMB(amount: Long) : MemoryAmount(MemoryUnit.MB, amount)
    class AmountGB(amount: Long) : MemoryAmount(MemoryUnit.GB, amount)
    class AmountTB(amount: Long) : MemoryAmount(MemoryUnit.TB, amount)

    fun add(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(unit, amount + memory.amount)
    }

    fun subtract(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(unit, amount - memory.amount)
    }

    operator fun times(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(unit, amount * memory.amount)
    }

    fun divide(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(unit, amount / memory.amount)
    }

    fun convertTo(unit: MemoryUnit): MemoryAmount {
        return MemoryAmount(unit, unit.from(this.unit, amount))
    }

    companion object {
        fun of(input: MemoryUnit, output: MemoryUnit, value: Long): MemoryAmount {
            val memoryAmount = MemoryAmount(input, value)
            return if (input == output) {
                memoryAmount
            } else {
                memoryAmount.convertTo(output)
            }
        }

        fun of(input: MemoryUnit, value: Long): MemoryAmount {
            return of(input, input, value)
        }
    }
}
