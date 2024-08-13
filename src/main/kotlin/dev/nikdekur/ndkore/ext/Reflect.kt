/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("FunctionName", "NOTHING_TO_INLINE", "kotlin:S100")

package dev.nikdekur.ndkore.ext

import dev.nikdekur.ndkore.reflect.Reflect
import dev.nikdekur.ndkore.reflect.ReflectResult
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method


/**
 * Attempts to find a class by its fully qualified name.
 *
 * This function tries to find and load a class with the specified name.
 * If the class cannot be found, it returns `null`.
 *
 * @param name The fully qualified name of the class to find.
 * @return The `Class` object if found, or `null` if the class does not exist.
 */
inline fun findClass(name: String): Class<*>? {
    return try {
        Class.forName(name)
    } catch (e: ClassNotFoundException) {
        null
    }
}

/**
 * Extension function to search for a field with the specified name in the class.
 *
 * @receiver The class in which to search for the field.
 * @param name The name of the field to search for.
 * @return The `Field` object representing the field if found, or `null` if the field does not exist.
 */
inline fun Class<*>.searchField(name: String): Field? {
    return Reflect.searchField(this, name)
}

/**
 * Extension function to search for a field with the specified name in the class and its superclasses.
 *
 * @receiver The class in which to start the search.
 * @param name The name of the field to search for.
 * @return The `Field` object representing the field if found, or `null` if the field does not exist.
 */
inline fun Class<*>.searchFieldRecursive(name: String): Field? {
    return Reflect.searchFieldRecursive(this, name)
}

/**
 * Extension function to search for a method with the specified name and parameter types in the class.
 *
 * @receiver The class in which to search for the method.
 * @param name The name of the method to search for.
 * @param classes The parameter types of the method.
 * @return The `Method` object representing the method if found, or `null` if the method does not exist.
 */
inline fun Class<*>.searchMethod(name: String, classes: Array<out Class<*>>): Method? {
    return Reflect.searchMethod(this, name, classes)
}

/**
 * Extension function
 * to search for a method with the specified name and parameter types in the class and its superclasses.
 *
 * @receiver The class in which to start the search.
 * @param name The name of the method to search for.
 * @param classes The parameter types of the method.
 * @return The `Method` object representing the method if found, or `null` if the method does not exist.
 */
inline fun Class<*>.searchMethodRecursive(name: String, classes: Array<out Class<*>>): Method? {
    return Reflect.searchMethodRecursive(this, name, classes)
}

/**
 * Extension property to retrieve all fields and their values from the class and its superclasses.
 *
 * @receiver The object from which to retrieve the field values.
 * @return A map where the keys are field names and the values are the corresponding field values.
 */
inline val Any.r_ClassFields: Map<String, Any?>
    get() = Reflect.getClassFields(javaClass, this)

/**
 * Extension property to retrieve all methods of the class and its superclasses.
 *
 * @receiver The object from which to retrieve the methods.
 * @return A map where the keys are method names and the values are `Method` objects representing the methods.
 */
inline val Any.r_ClassMethods: HashMap<String, Method>
    get() = Reflect.getClassMethods(javaClass)

/**
 * Extension function to get the value of a field from the object.
 *
 * @receiver The object from which to retrieve the field value.
 * @param name The name of the field whose value is to be retrieved.
 * @return A `ReflectResult` object containing the value of the field if found,
 * or `ReflectResult.Missing` if the field does not exist.
 */
inline fun Any.r_GetField(name: String) = Reflect.getFieldValue(javaClass, this, name)

/**
 * Extension function to set the value of a field in the object.
 *
 * @receiver The object in which to set the field value.
 * @param name The name of the field whose value is to be set.
 * @param value The value to be set in the field.
 */
inline fun Any.r_SetField(name: String, value: Any?) = Reflect.setFieldValue(javaClass, this, name, value)

/**
 * Extension function to invoke a method with specified arguments and return the result.
 *
 * This method searches for the method recursively starting from the object's class.
 * If the method is found,
 * it is invoked with the given arguments.
 * If the method is not found, it returns `ReflectResult.Missing`.
 *
 * @receiver The object on which to invoke the method.
 * @param name The name of the method to be invoked.
 * @param classes The parameter types of the method.
 * @param args The arguments to be passed to the method.
 * @return A `ReflectResult` object containing the result of the method invocation if the method is found,
 * or `ReflectResult.Missing` if the method does not exist.
 */
inline fun Any.r_CallMethodTyped(name: String, classes: Array<out Class<*>>, vararg args: Any?): ReflectResult {
    return Reflect.callMethodTyped(javaClass, this, name, classes, *args)
}

/**
 * Extension function to invoke a method with specified arguments and return the result.
 *
 * This method infers the parameter types from the provided arguments.
 * It then searches for the method recursively
 * starting from the object's class.
 * If the method is found, it is invoked with the given arguments.
 * If the method is not found, it returns `ReflectResult.Missing`.
 *
 * @receiver The object on which to invoke the method.
 * @param name The name of the method to be invoked.
 * @param args The arguments to be passed to the method.
 * @return A `ReflectResult` object containing the result of the method invocation if the method is found,
 * or `ReflectResult.Missing` if the method does not exist.
 */
inline fun Any.r_CallMethod(name: String, vararg args: Any?): ReflectResult {
    val classes = args.mapNotNull { it?.javaClass }.toTypedArray()
    return Reflect.callMethodTyped(javaClass, this, name, classes, *args)
}


/**
 * Retrieves methods in the class that are annotated with a specific annotation.
 *
 * @param annotation The class of the annotation to search for.
 * @return A list of `Method` objects representing the methods that have the specified annotation.
 */
fun <T> Class<T>.getMethodsWithAnnotation(annotation: Class<out Annotation>): List<Method> {
    val methods = mutableListOf<Method>()
    for (method in declaredMethods) {
        if (method.isAnnotationPresent(annotation))
            methods.add(method)
    }
    return methods
}

/**
 * Retrieves methods in the class that are annotated with all specified annotations.
 *
 * @param annotations An iterable of annotation classes to search for.
 * @return A list of `Method` objects representing the methods that have all the specified annotations.
 */
fun <T> Class<T>.getMethodsWithAnnotations(annotations: Iterable<Class<out Annotation>>): List<Method> {
    val methods = mutableListOf<Method>()
    for (method in declaredMethods) {
        if (annotations.all { method.isAnnotationPresent(it) })
            methods.add(method)
    }
    return methods
}

/**
 * Constructs an instance of the class using a specific constructor with parameter types.
 *
 * @param types The parameter types of the constructor.
 * @param args The arguments to pass to the constructor.
 * @return A new instance of the class.
 */
fun <T : Any> Class<T>.constructTyped(types: Array<out Class<*>>, vararg args: Any): T {
    val constructor = getConstructor(*types)
    return constructor.newInstance(*args)
}

/**
 * Constructs an instance of the class using a constructor inferred from argument types.
 *
 * @param args The arguments to pass to the constructor.
 * @return A new instance of the class.
 */
fun <T : Any> Class<T>.construct(vararg args: Any): T {
    val constructor = getConstructor(*args.map { it.javaClass }.toTypedArray())
    return constructor.newInstance(*args)
}

/**
 * Finds a constructor in the class that matches any of the provided parameter type arrays.
 *
 * @param classes An iterable of parameter type arrays to search for.
 * @return The `Constructor` object if found, or `null` if no matching constructor exists.
 */
fun <T : Any> Class<T>.getAnyConstructor(classes: Iterable<Array<out Class<*>>>): Constructor<T>? {
    for (clazz in classes) {
        try {
            return getDeclaredConstructor(*clazz)
        } catch (e: NoSuchMethodException) {
            continue
        }
    }
    return null
}

/**
 * Executes a block of code with the `AccessibleObject` temporarily set to accessible.
 *
 * @receiver The `AccessibleObject` to temporarily unlock.
 * @param block The block of code to execute.
 * @return The result of the block of code.
 */
inline fun <T> AccessibleObject.withUnlock(block: () -> T): T {
    val accessible = isAccessible
    if (!accessible) isAccessible = true
    return try {
        block()
    } finally {
        isAccessible = accessible
    }
}

/**
 * Attempts to retrieve the singleton instance field from a Kotlin object class.
 *
 * @return The singleton instance if present, or `null` if the field does not exist or is not accessible.
 */
@Suppress("UNCHECKED_CAST")
inline fun <T> Class<T>.getInstanceFieldOrNull(): T? {
    return try {
        getField("INSTANCE")[null] as? T
    } catch (e: NoSuchFieldException) {
        null
    }
}

/**
 * Retrieves the singleton instance field from a Kotlin object class.
 *
 * @return The singleton instance.
 * @throws IllegalStateException If the instance field does not exist or is not accessible.
 */
inline fun <T> Class<T>.getInstanceField(): T {
    return getInstanceFieldOrNull()
        ?: throw IllegalStateException("No INSTANCE field in ${this.name}. Make sure it's a Kotlin 'object'.")
}

/**
 * Retrieves a nested class by its name within a containing class.
 *
 * @param name The name of the nested class.
 * @return The `Class` object representing the nested class.
 * @throws ClassNotFoundException If the nested class does not exist.
 */
inline fun <reified T> getNestedClass(name: String): Class<*> {
    return Class.forName("${T::class.java.name}\$${name}")
}

/**
 * Retrieves an enum constant by its name, optionally ignoring case.
 *
 * @param name The name of the enum constant.
 * @param ignoreCase Whether to ignore a case when matching the name.
 * @return The enum constant of type `T`.
 * @throws IllegalArgumentException If no matching enum constant is found.
 */
inline fun <reified T : Enum<T>> enumValueOf(name: String, ignoreCase: Boolean = false): T {
    return if (ignoreCase) {
        enumValues<T>().find { it.name.equals(name, ignoreCase = true) }
            ?: throw IllegalArgumentException("No enum constant ${T::class.qualifiedName}.$name")
    } else {
        kotlin.enumValueOf(name)
    }
}
