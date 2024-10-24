@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.service.Service.State.ErrorDisabling
import dev.nikdekur.ndkore.service.Service.State.ErrorEnabling

/**
 * A service that can be registered to the [dev.nikdekur.ndkore.service.manager.ServicesManager].
 *
 * Service is a module that can be enabled and disabled.
 *
 * Service have to be able to do a reload infinitely.
 *
 * @see dev.nikdekur.ndkore.service.manager.ServicesManager
 */
public interface Service : ServicesComponent {

    /**
     * Current state of the service.
     */
    public val state: State

    /**
     * Dependencies that this service has.
     *
     * [dev.nikdekur.ndkore.service.manager.ServicesManager] will enable all dependencies in the order
     * to satisfy all dependencies of all services.
     */
    public val dependencies: Dependencies

    /**
     * Enables the service.
     *
     * Catch all exceptions and change the state.
     */
    public suspend fun enable()

    /**
     * Disables the service.
     *
     * Catch all exceptions and change the state.
     */
    public suspend fun disable()

    /**
     * Reloads the service.
     *
     * Catch all exceptions and change the state.
     *
     * By default, equivalent to calling [disable] and [enable].
     */
    public suspend fun reload()

    /**
     * # State
     *
     * State of the service.
     */
    public sealed interface State {
        /**
         * Service is enabling.
         *
         * Usually due to [enable] being called.
         */
        public data object Enabling : State

        /**
         * Service failed to enable.
         *
         * Can be due to an exception thrown in [enable].
         *
         * @param error the error that occurred
         */
        public data class ErrorEnabling(val error: Throwable) : State

        /**
         * Service is enabled.
         *
         * No errors occurred during enabling.
         */
        public data object Enabled : State


        /**
         * Service is disabling.
         *
         * Usually due to [disable] being called.
         */
        public data object Disabling : State

        /**
         * Service failed to disable.
         *
         * Can be due to an exception thrown in [disable].
         *
         * @param error the error that occurred
         */
        public data class ErrorDisabling(val error: Throwable) : State

        /**
         * Service is disabled.
         *
         * No errors occurred during disabling.
         */
        public data object Disabled : State
    }
}

/**
 * Check if the service is in an error state.
 *
 * @return true if the service is in an error state
 */
public inline fun Service.State.isErrored(): Boolean = this is ErrorEnabling || this is ErrorDisabling