@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.module

import dev.nikdekur.ndkore.interfaces.Snowflake

interface Module<A> : Snowflake<String> {

    val app: A

    override val id: String
        get() = idOf(javaClass)

    fun onLoad() {
        // Plugin Module does not require to implement onLoad
    }
    fun onUnload() {
        // Plugin Module does not require to implement onUnload
    }

    val dependencies: Dependencies
        get() = Dependencies.none()

    companion object {
        @JvmStatic
        inline fun idOf(clazz: Class<out Module<*>>): String {
            return clazz.simpleName
        }

        @JvmStatic
        inline val Class<out Module<*>>.id
            get() = idOf(this)
    }
}

