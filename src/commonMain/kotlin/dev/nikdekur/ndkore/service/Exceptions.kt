/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import kotlin.reflect.KClass

public class ClassIsNotServiceException(
    public val clazz: KClass<*>
) : RuntimeException("Class '${clazz}' is not a service!")

/**
 * Indicates service not found in the services manager.
 *
 * Can be thrown when a service dependency is not found.
 */
public class DependentServiceNotFoundException(
    public val source: Service,
    public val serviceClass: KClass<*>,
    public val qualifier: Qualifier
) : RuntimeException("$source need service '${serviceClass}' with qualifier '$qualifier', which is not found!")


public class ServiceNotFoundException(
    public val serviceClass: KClass<*>,
    public val qualifier: Qualifier
) : RuntimeException("Service '${serviceClass}' with qualifier '$qualifier' not found!")



/**
 * Indicates service circular dependency or self-dependency.
 */
public class CircularDependencyException(
    public val service: Service
) : RuntimeException("Recursive dependency found on processing `$service`")