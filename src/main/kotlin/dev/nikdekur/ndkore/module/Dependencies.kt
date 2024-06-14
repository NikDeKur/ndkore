package dev.nikdekur.ndkore.module

/**
 * Represents dependencies of a plugin module.
 *
 * @param before List of modules that should be loaded before this module
 * @param after List of modules that should be loaded after this module
 * @param first True if this module should be loaded first
 * @param last True if this module should be loaded last
 * @see Module
 */
data class Dependencies(
    val before: List<Class<out Module<*>>> = emptyList(),
    val after: List<Class<out Module<*>>> = emptyList(),
    val first: Boolean = false,
    val last: Boolean = false,
) {

    companion object {
        @JvmStatic
        fun after(vararg modules: Class<out Module<*>>) = Dependencies(emptyList(), modules.toList(), first = false, last = false)
        @JvmStatic
        fun before(vararg modules: Class<out Module<*>>) = Dependencies(modules.toList(), emptyList(), first = false, last = false)

        private val EMPTY = Dependencies(emptyList(), emptyList(), first = false, last = false)
        private val FIRST = Dependencies(emptyList(), emptyList(), first = true, last = false)
        private val LAST = Dependencies(emptyList(), emptyList(), first = false, last = true)
        @JvmStatic
        fun none() = EMPTY
        @JvmStatic
        fun first() = FIRST
        @JvmStatic
        fun last() = LAST
    }
}


class DependenciesBuilder {
    private val before = mutableListOf<Class<out Module<*>>>()
    private val after = mutableListOf<Class<out Module<*>>>()
    var first = false
    var last = false

    fun before(vararg modules: Class<out Module<*>>) {
        before.addAll(modules)
    }

    fun after(vararg modules: Class<out Module<*>>) {
        after.addAll(modules)
    }


    fun build() = Dependencies(before, after, first, last)
}

inline fun dependencies(block: DependenciesBuilder.() -> Unit): Dependencies {
    val builder = DependenciesBuilder()
    builder.block()
    return builder.build()
}