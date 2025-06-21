@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.ext.toSingletonList
import dev.nikdekur.ndkore.service.manager.ServicesManager
import kotlin.reflect.KClass

public interface Definition<S : Any> {
    public val service: S
    public val qualifier: Qualifier
    public val bindTo: Iterable<KClass<*>>
}

public data class SimpleDefinition<S : Any>(
    override val service: S,
    override val qualifier: Qualifier = Qualifier.Empty,
    override val bindTo: Iterable<KClass<*>> = emptyList()
) : Definition<S>

public suspend inline fun <S : Any> ServicesManager.registerService(
    definition: Definition<S>,
    qualifier: Qualifier = Qualifier.Empty
) {
    val definition = SimpleDefinition(definition.service, qualifier, definition.bindTo)
    registerService(definition)
}

public suspend inline fun ServicesManager.registerService(
    service: Service,
    qualifier: Qualifier = Qualifier.Empty
) {
    val definition = SimpleDefinition(service, qualifier)
    registerService(definition)
}


public inline infix fun <S : Any> S.bind(clazz: KClass<S>): Definition<S> {
    return SimpleDefinition(
        service = this,
        bindTo = clazz.toSingletonList()
    )
}


public inline fun <S : Any> S.binds(vararg clazz: KClass<out S>): Definition<out S> {
    return SimpleDefinition(service = this, bindTo = clazz.toList())
}


public inline infix fun <S : Any> Definition<out S>.bind(clazz: KClass<out S>): Definition<S> {
    return SimpleDefinition(service, qualifier, bindTo + clazz)
}


public inline fun <S : Any> Definition<out S>.binds(vararg clazz: KClass<out S>): Definition<out S> {
    return SimpleDefinition(service, qualifier, bindTo + clazz)
}


public inline infix fun <S : Any> Definition<out S>.qualify(qualifier: Qualifier): Definition<S> {
    return SimpleDefinition(service, qualifier, bindTo)
}

public inline infix fun <S : Any> Definition<out S>.qualify(qualifier: String): Definition<S> {
    return qualify(qualifier.qualifier)
}