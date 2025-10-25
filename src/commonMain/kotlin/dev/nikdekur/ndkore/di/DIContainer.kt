package dev.nikdekur.ndkore.di

import kotlin.reflect.KClass

/**
 * Interface for a Dependency Injection (DI) Container.
 *
 * This interface defines the basic operations for a DI container, allowing
 * for the addition and retrieval of dependencies.
 *
 * Basic implementations: [RuntimeDIContainer], [KoinDIContainer].
 *
 * @see Definition
 * @see Qualifier
 */
public interface DIContainer {

    /**
     * Adds a dependency definition to the container.
     *
     * This method registers a new dependency definition in the container.
     * It can be used to add services, factories, or other definitions.
     *
     * @param definition The definition of the dependency to add.
     */
    public fun add(definition: Definition<*>)

    /**
     * Retrieves a dependency by its class, or returns null if it is not found.
     *
     * This method attempts to locate a dependency by its class or class it is binded to.
     * It will return the service instance if found, or null if no service is found.
     *
     * @param C The type of the service to retrieve.
     * @param serviceClass The KClass of the service to retrieve.
     * @return The service instance, or null if not found.
     */
    public fun <C : Any> getOrNull(
        serviceClass: KClass<out C>,
        qualifier: Qualifier = Qualifier.Empty
    ): C?

    /**
     * Retrieves a dependency by its class.
     *
     * This method attempts to locate a service by its class or class it is binded to.
     *
     * @param C The type of the dependency to retrieve.
     * @param serviceClass The KClass of the dependency to retrieve.
     * @return The dependency instance.
     * @throws DependencyNotFoundException If the dependency is not found.
     */
    public fun <C : Any> get(
        serviceClass: KClass<out C>,
        qualifier: Qualifier = Qualifier.Empty
    ): C
}