package dev.nikdekur.ndkore.ext

import dev.nikdekur.ndkore.service.ServiceNotFoundException
import dev.nikdekur.ndkore.service.ServicesComponent
import dev.nikdekur.ndkore.service.ServicesManager

/**
 * # Java Services
 *
 * Provides utility functions for working with services from Java.
 */
public object JavaServices {

    /**
     * Registers a service with the manager.
     *
     * This method registers a service and binds it to one or more classes, allowing for dependency injection.
     * Injecting a service will always return the same instance.
     *
     * If manager is enabled, the service will be enabled immediately.
     * If some service is already registered with the same class, it will be replaced
     * and the old service will be disabled (if manager is enabled).
     *
     * @param service The service to be registered.
     * @param bindTo One or more classes to which the service should be bound.
     */
    public fun <C : Any, S> registerService(
        manager: ServicesManager,
        service: S,
        vararg bindTo: Class<out C>
    ) where S : C {
        manager.registerService(service, *bindTo.map { it.kotlin }.toTArray())
    }

    /**
     * Retrieves a service by its class, or returns null if it is not found.
     *
     * This method attempts to locate a service by its class
     * or class it is binded to.
     * It will return the service instance if found,
     * or null if no service is found.
     *
     * @param C The type of the service to retrieve.
     * @param serviceClass The Class of the service to retrieve.
     * @return The service instance, or null if not found.
     */
    public fun <C : Any> getServiceOrNull(manager: ServicesManager, serviceClass: Class<out C>): C? {
        return manager.getServiceOrNull(serviceClass.kotlin)
    }

    /**
     * Retrieves a service by its class.
     *
     * This method attempts to locate a service by its class
     * or class it is binded to.
     *
     * @param C The type of the service to retrieve.
     * @param serviceClass The Class of the service to retrieve.
     * @return The service instance.
     * @throws ServiceNotFoundException If the service is not found.
     */
    public fun <C : Any> getService(manager: ServicesManager, serviceClass: Class<out C>): C {
        return manager.getService(serviceClass.kotlin)
    }


    /**
     * Retrieves a service by its class, or returns null if it is not found.
     *
     * This extension function simplifies the retrieval of services by using reified generics to infer the service class.
     *
     * @param C The type of the service to retrieve.
     * @return The service instance, or null if not found.
     */
    public fun <C : Any> getOrNull(component: ServicesComponent, clazz: Class<out C>): C? =
        getServiceOrNull(component.manager, clazz)


    /**
     * Retrieves a service by its class.
     *
     * This extension function simplifies the retrieval of services by using reified generics to infer the service class.
     * It will return the service instance if found, or throw a [ServiceNotFoundException] if not found.
     *
     * @param C The type of the service to retrieve.
     * @return The service instance.
     * @throws ServiceNotFoundException If the service is not found.
     */
    public fun <C : Any> get(component: ServicesComponent, clazz: Class<out C>): C =
        getService(component.manager, clazz)
}