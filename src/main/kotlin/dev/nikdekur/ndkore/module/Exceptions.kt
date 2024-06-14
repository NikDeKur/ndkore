package dev.nikdekur.ndkore.module

class ModuleNotFoundException(moduleName: String) : RuntimeException("Module '$moduleName' not found!")
class IncorrectModuleTypeException(moduleName: String, expectedType: String, foundType: String) :
    RuntimeException("Module '$moduleName' is not instance of '$expectedType', but '$foundType'!")
class SelfDependencyException(moduleName: String) : RuntimeException("Self dependency in '$moduleName'!")
class RecursiveDependencyException(moduleName: String) : RuntimeException("Recursive module dependency in '$moduleName' with module '$moduleName'!")