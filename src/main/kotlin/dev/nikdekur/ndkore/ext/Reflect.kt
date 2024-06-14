@file:Suppress("FunctionName", "NOTHING_TO_INLINE", "kotlin:S100")

package dev.nikdekur.ndkore.ext

import dev.nikdekur.ndkore.reflect.Reflect
import dev.nikdekur.ndkore.reflect.ReflectResult
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Method

inline val ReflectResult.result: Any?
    get() {
        check(this is ReflectResult.Success) { "Result is not success" }
        return result
    }

inline fun findClass(name: String): Class<*>? {
    return try {
        Class.forName(name)
    } catch (e: ClassNotFoundException) {
        null
    }
}

inline val Any.r_ClassFields: Map<String, Any?>
    get() = Reflect.getClassFields(javaClass, this)

inline val Any.r_ClassMethods: HashMap<String, Method>
    get() = Reflect.getClassMethods(javaClass)

inline fun Any.r_GetField(name: String) = Reflect.getField(javaClass, this, name)

inline fun Any.r_SetField(name: String, value: Any?) = Reflect.setField(javaClass, this, name, value)


inline fun Class<*>.getMethodOrNull(name: String, classes: Array<out Class<*>>): Method? {
    var objClass: Class<*>? = this
    while (objClass != null) {
        val method = Reflect.getMethodOrNull(objClass, name, classes)
        if (method != null)
            return method

        objClass = objClass.superclass
    }
    return null
}

inline fun Any.r_CallMethodTyped(name: String, classes: Array<out Class<*>>, vararg args: Any?) : ReflectResult {
    return Reflect.callMethodTyped(javaClass, this, name, classes, *args)
}

inline fun Any.r_CallMethod(name: String, vararg args: Any?): ReflectResult {
    return Reflect.callMethod(javaClass, this, name, *args)
}

fun <T> Class<T>.getMethodsWithAnnotation(annotation: Class<out Annotation>): List<Method> {
    val methods = mutableListOf<Method>()
    for (method in declaredMethods) {
        if (method.isAnnotationPresent(annotation))
            methods.add(method)
    }
    return methods
}

fun <T> Class<T>.getMethodsWithAnnotations(annotations: Iterable<Class<out Annotation>>): List<Method> {
    val methods = mutableListOf<Method>()
    for (method in declaredMethods) {
        if (annotations.all { method.isAnnotationPresent(it) })
            methods.add(method)
    }
    return methods
}



fun <T : Any> Class<T>.constructTyped(types: Array<out Class<*>>, vararg args: Any): T {
    val constructor = getConstructor(*types)
    return constructor.newInstance(*args)
}

fun <T : Any> Class<T>.construct(vararg args: Any): T {
    val constructor = getConstructor(*args.map { it.javaClass }.toTArray())
    return constructor.newInstance(*args)
}



fun <T : Any> Class<T>.getAnyConstructor(classes: Iterable<Array<out Class<*>>>): Constructor<T>? {
    var constructor: Constructor<T>? = null
    for (clazz in classes) {
        try {
            constructor = getDeclaredConstructor(*clazz)
            break
        } catch (e: NoSuchMethodException) {
            continue
        }
    }
    return constructor
}


inline fun <T> AccessibleObject.withUnlock(block: () -> T): T {
    val accessible = isAccessible
    if (!accessible)
        isAccessible = true
    return block()
}


@Suppress("UNCHECKED_CAST")
inline fun <T> Class<T>.getInstanceFieldOrNull(): T? {
    return try {
        getField("INSTANCE")[null] as? T
    } catch (e: NoSuchFieldException) {
        null
    }
}

inline fun <T> Class<T>.getInstanceField(): T {
    return getInstanceFieldOrNull() ?: throw IllegalStateException("No INSTANCE field in ${this.name}. Make sure it's a kotlin 'object'.")
}



inline fun <reified T> getNestedClass(name: String): Class<*> {
    return Class.forName("${T::class.java.name}\$${name}")
}

inline fun <reified T : Enum<T>> enumValueOf(name: String, ignoreCase: Boolean = false): T {
    return if (ignoreCase) {
        enumValues<T>().find { it.name.equals(name, ignoreCase = true) }
            ?: throw IllegalArgumentException("No enum constant ${T::class.qualifiedName}.$name")
    } else {
        kotlin.enumValueOf(name)
    }
}