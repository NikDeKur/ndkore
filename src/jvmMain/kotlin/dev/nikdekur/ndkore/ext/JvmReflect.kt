@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import dev.nikdekur.ndkore.reflect.Reflect
import dev.nikdekur.ndkore.reflect.ReflectResult
import dev.nikdekur.ndkore.reflect.UnsafeReflectAPI
import java.io.File
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.security.ProtectionDomain
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection


/**
 * Attempts to find a class by its fully qualified name.
 *
 * This function tries to find and load a class with the specified name.
 * If the class cannot be found, it returns `null`.
 *
 * @param name The fully qualified name of the class to find.
 * @return The `Class` object if found, or `null` if the class does not exist.
 */
public inline fun findClass(name: String): Class<*>? {
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
public inline fun Class<*>.searchField(name: String): Field? {
    return Reflect.searchField(this, name)
}

/**
 * Extension function to search for a field with the specified name in the class and its superclasses.
 *
 * @receiver The class in which to start the search.
 * @param name The name of the field to search for.
 * @return The `Field` object representing the field if found, or `null` if the field does not exist.
 */
public inline fun Class<*>.searchFieldRecursive(name: String): Field? {
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
public inline fun Class<*>.searchMethod(name: String, classes: Array<out Class<*>>): Method? {
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
public inline fun Class<*>.searchMethodRecursive(name: String, classes: Array<out Class<*>>): Method? {
    return Reflect.searchMethodRecursive(this, name, classes)
}

/**
 * Extension property to retrieve all fields and their values from the class and its superclasses.
 *
 * @receiver The object from which to retrieve the field values.
 * @return A map where the keys are field names and the values are the corresponding field values.
 */
public inline val Any.r_ClassFields: Map<String, Any?>
    get() = Reflect.getClassFields(javaClass, this)

/**
 * Extension property to retrieve all methods of the class and its superclasses.
 *
 * @receiver The object from which to retrieve the methods.
 * @return A map where the keys are method names and the values are `Method` objects representing the methods.
 */
public inline val Any.r_ClassMethods: HashMap<String, Method>
    get() = Reflect.getClassMethods(javaClass)

/**
 * Extension function to get the value of a field from the object.
 *
 * @receiver The object from which to retrieve the field value.
 * @param name The name of the field whose value is to be retrieved.
 * @return A `ReflectResult` object containing the value of the field if found,
 * or `ReflectResult.Missing` if the field does not exist.
 */
@Suppress("FunctionName", "kotlin:S100")
public inline fun Any.r_GetField(name: String): ReflectResult = Reflect.getFieldValue(javaClass, this, name)

/**
 * Extension function to set the value of a field in the object.
 *
 * @receiver The object in which to set the field value.
 * @param name The name of the field whose value is to be set.
 * @param value The value to be set in the field.
 */
@Suppress("FunctionName", "kotlin:S100")
public inline fun Any.r_SetField(name: String, value: Any?): Unit = Reflect.setFieldValue(javaClass, this, name, value)

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
@Suppress("FunctionName", "kotlin:S100")
public inline fun Any.r_CallMethodTyped(name: String, classes: Array<out Class<*>>, vararg args: Any?): ReflectResult {
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
@Suppress("FunctionName", "kotlin:S100")
public inline fun Any.r_CallMethod(name: String, vararg args: Any?): ReflectResult {
    val classes = args.mapNotNull { it?.javaClass }.toTypedArray()
    return Reflect.callMethodTyped(javaClass, this, name, classes, *args)
}

/**
 * Sets the value of a field in the given object using the `Unsafe` instance.
 *
 * This method uses the `Unsafe` instance to set the value of a field directly in the object,
 * without facing any access restrictions.
 * It allows setting the value of a field that is even declared as `final`.
 *
 * @param name The name of the field whose value is to be set.
 * @param value The value to be set in the field.
 * @throws IllegalAccessException If the `Unsafe` instance could not be accessed.
 * @throws NoSuchFieldException If the field does not exist in the object.
 */
@UnsafeReflectAPI
@Suppress("FunctionName", "kotlin:S100")
public inline fun Any.r_SetFieldUnsafe(name: String, value: Any?) {
    Reflect.setFieldValueUnsafe(javaClass, this, name, value)
}

/**
 * Retrieves methods in the class that are annotated with a specific annotation.
 *
 * @param annotation The class of the annotation to search for.
 * @return A list of `Method` objects representing the methods that have the specified annotation.
 */
public fun <T> Class<T>.getMethodsWithAnnotation(annotation: Class<out Annotation>): List<Method> {
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
public fun <T> Class<T>.getMethodsWithAnnotations(annotations: Iterable<Class<out Annotation>>): List<Method> {
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
public fun <T : Any> Class<T>.constructTyped(types: Array<out Class<*>>, vararg args: Any): T {
    val constructor = getConstructor(*types)
    return constructor.newInstance(*args)
}

/**
 * Constructs an instance of the class using a constructor inferred from argument types.
 *
 * @param args The arguments to pass to the constructor.
 * @return A new instance of the class.
 */
public fun <T : Any> Class<T>.construct(vararg args: Any): T {
    val constructor = getConstructor(*args.map { it.javaClass }.toTypedArray())
    return constructor.newInstance(*args)
}

/**
 * Finds a constructor in the class that matches any of the provided parameter type arrays.
 *
 * @param classes An iterable of parameter type arrays to search for.
 * @return The `Constructor` object if found, or `null` if no matching constructor exists.
 */
public fun <T : Any> Class<T>.getAnyConstructor(classes: Iterable<Array<out Class<*>>>): Constructor<T>? {
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
public inline fun <T> AccessibleObject.withUnlock(block: () -> T): T {
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
public inline fun <T> Class<T>.getInstanceFieldOrNull(): T? {
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
public inline fun <T> Class<T>.getInstanceField(): T {
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
public inline fun <reified T> getNestedClass(name: String): Class<*> {
    return Class.forName("${T::class.java.name}$${name}")
}


/**
 * Retrieves the JAR file
 *
 * @param protectionDomain The `ProtectionDomain` of the class to resolve the JAR file.
 * Could be obtained by [Class.getProtectionDomain]
 * @return The `File` object representing the JAR file.
 */
public inline fun resolveJar(protectionDomain: ProtectionDomain): File {
    return File(protectionDomain.codeSource.location.toURI())
}

/**
 * Searches for entries in the JAR file that match the specified directory.
 *
 * @param directory The directory to search for in the JAR file.
 * @return The `File` object representing the JAR file.
 */
public inline fun JarFile.getEntries(directory: String): List<JarEntry> {
    val path = "$directory/"
    return entries()
        .asSequence()
        .filter { it.name.startsWith(path) && it.name != path }
        .toList()
}


/**
 * Creates a new `KType` object with the specified parameters.
 *
 * JVM only function because KType fields depend on a platform.
 *
 * @param arguments The type arguments of the type (generics).
 * @param classifier The classifier of the type (usually KClass).
 * @param isMarkedNullable Whether the type is nullable or not.
 * @param annotations The annotations of the type (field annotations).
 * @return A new `KType` object.
 */
public inline fun KType(
    classifier: KClassifier,
    isMarkedNullable: Boolean = false,
    arguments: List<KTypeProjection> = emptyList(),
    annotations: List<Annotation> = emptyList()
): KType = object : KType {
    override val annotations: List<Annotation> = annotations
    override val isMarkedNullable: Boolean = isMarkedNullable
    override val classifier: KClassifier = classifier
    override val arguments: List<KTypeProjection> = arguments
}

