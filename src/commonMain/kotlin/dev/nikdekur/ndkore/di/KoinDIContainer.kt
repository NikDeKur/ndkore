@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.di

import dev.nikdekur.ndkore.ext.loadModule
import dev.nikdekur.ndkore.ext.single
import dev.nikdekur.ndkore.service.manager.DIContainerBuilder
import org.koin.core.context.KoinContext
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.module.Module
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.qualifier
import org.koin.dsl.binds
import kotlin.reflect.KClass

public class KoinDIContainer(
    public val context: KoinContext
) : DIContainer {

    override fun add(definition: Definition<*>) {
        context.loadModule {
            reg(definition.obj, definition.qualifier, definition.bindTo)
        }
    }

    @Suppress("UNCHECKED_CAST")
    public inline fun <I : Any> Module.reg(service: I, qualifier: Qualifier, bindTo: Iterable<KClass<out I>>) {
        val moduleClass = service::class as KClass<I>
        val service = service

        val qualifier = qualifier.toKoinQualifier()
        val definition = single(clazz = moduleClass, qualifier = qualifier) { service }
        definition binds bindTo.toList().toTypedArray()
    }

    override fun <C : Any> getOrNull(
        serviceClass: KClass<out C>,
        qualifier: Qualifier
    ): C? {
        return context.get().getOrNull(serviceClass, qualifier = qualifier.toKoinQualifier())
    }

    override fun <C : Any> get(
        serviceClass: KClass<out C>,
        qualifier: Qualifier
    ): C {
        return try {
            context.get().get(serviceClass, qualifier = qualifier.toKoinQualifier())
        } catch (e: NoDefinitionFoundException) {
            throw DependencyNotFoundException(serviceClass, qualifier)
        }
    }
}


public inline fun Qualifier.toKoinQualifier(): StringQualifier? = if (value.isEmpty()) null else qualifier(value)

public open class KoinDIContainerBuilder : DIContainerBuilder<KoinDIContainer>() {

    public var actualContext: KoinContext? = null

    public fun context(context: KoinContext) {
        this.actualContext = context
    }

    override fun build(): KoinDIContainer {
        val context = actualContext
        requireNotNull(context) { "Koin context must be set!" }

        return KoinDIContainer(context)
    }
}


public inline fun KoinDIContainer(builder: KoinDIContainerBuilder.() -> Unit): KoinDIContainer {
    val b = KoinDIContainerBuilder().apply(builder)
    return b.build()
}