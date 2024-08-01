/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.module

class ModuleNotFoundException(moduleName: String) : RuntimeException("Module '$moduleName' not found!")
class IncorrectModuleTypeException(moduleName: String, expectedType: String, foundType: String) :
    RuntimeException("Module '$moduleName' is not instance of '$expectedType', but '$foundType'!")
class SelfDependencyException(moduleName: String) : RuntimeException("Self dependency in '$moduleName'!")
class RecursiveDependencyException(moduleName: String) : RuntimeException("Recursive module dependency in '$moduleName' with module '$moduleName'!")