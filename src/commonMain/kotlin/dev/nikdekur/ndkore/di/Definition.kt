@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.di

import dev.nikdekur.ndkore.ext.toSingletonList
import kotlin.reflect.KClass

public data class Definition<S : Any>(
    val obj: S,
    val qualifier: Qualifier = Qualifier.Empty,
    val bindTo: Iterable<KClass<out S>> = emptyList()
)


public inline infix fun <S : Any> S.bind(clazz: KClass<S>): Definition<S> {
    return Definition(
        obj = this,
        bindTo = clazz.toSingletonList()
    )
}


public inline fun <S : Any> S.binds(vararg clazz: KClass<out S>): Definition<out S> {
    return Definition(obj = this, bindTo = clazz.toList())
}

public inline infix fun <S : Any> Definition<out S>.bind(clazz: KClass<out S>): Definition<out S> {
    return Definition(obj, qualifier, bindTo + clazz)
}


public inline fun <S : Any> Definition<out S>.binds(vararg clazz: KClass<out S>): Definition<out S> {
    return Definition(obj, qualifier, bindTo + clazz)
}


public inline infix fun <S : Any> Definition<out S>.qualify(qualifier: Qualifier): Definition<out S> {
    return copy(qualifier = qualifier)
}

public inline infix fun <S : Any> Definition<out S>.qualify(qualifier: String): Definition<out S> {
    return qualify(qualifier.qualifier)
}