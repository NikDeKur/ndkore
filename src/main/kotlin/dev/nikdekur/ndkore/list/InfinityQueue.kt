package dev.nikdekur.ndkore.list

class InfinityQueue<T> : ArrayList<T>, MutableList<T> {
    var index = 0
        private set

    constructor() : super()
    constructor(list: List<T>) : super(list)

    fun get(): T? {
        if (isEmpty()) {
            return null
        }
        val element = get(index)
        index = (index + 1) % size
        return element
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val queue = InfinityQueue<Int>()
            for (i in 0..9) {
                println(queue.get())
            }
        }
    }
}
