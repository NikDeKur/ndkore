/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

class ClassIsNotServiceException(clazz: Class<*>) :
    RuntimeException("Class '${clazz.name}' is not a service!")

/**
 * Indicates service not found.
 */
class ServiceNotFoundException(serviceClass: Class<*>) :
    RuntimeException("Service for '${serviceClass.name}' not found!")


/**
 * Indicates service circular dependency or self-dependency.
 */
class CircularDependencyException(service: Service) : RuntimeException("Circular dependency in '$service'!")