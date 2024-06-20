@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.reflect

import dev.nikdekur.ndkore.ext.*
import sun.misc.Unsafe
import java.lang.reflect.Method

object Reflect {

    fun getClassFields(clazz: Class<*>, obj: Any?): Map<String, Any?> {
        val fieldsMap: HashMap<String, Any?> = HashMap()
        try {
            var objClass: Class<*>? = clazz
            while (objClass != null) {
                val fields = objClass.declaredFields
                for (f in fields) {
                    f.withUnlock {
                        fieldsMap[f.name] = f[obj]
                    }
                }
                objClass = objClass.superclass
            }
        } catch (e: Exception) {
            return emptyMap()
        }

        return fieldsMap
    }



    fun getClassMethods(clazz: Class<*>): HashMap<String, Method> {
        var objClass: Class<*>? = clazz
        val methodsMap: HashMap<String, Method> = HashMap()

        while (objClass != null) {
            val methods = objClass.declaredMethods
            for (m in methods) {
                methodsMap[m.name] = m
            }
            objClass = objClass.superclass
        }

        return methodsMap
    }


    fun getField(clazz: Class<*>, obj: Any?, name: String): ReflectResult {
        try {
            var objClass: Class<*>? = clazz
            while (objClass != null) {
                val field = try {
                    objClass.getDeclaredField(name)
                } catch (_: NoSuchFieldException) {
                    null
                }

                if (field != null) {
                    return ReflectResult.Success(field.withUnlock {
                        field[obj]
                    })
                }
                objClass = objClass.superclass
            }
        } catch (e: Exception) {
            return ReflectResult.ExceptionFail(e)
        }
        return ReflectResult.FAIL
    }

    fun setField(clazz: Class<*>, obj: Any?, name: String, value: Any?): ReflectResult {
        try {
            var objClass: Class<*>? = clazz
            while (objClass != null) {
                val field = try {
                    objClass.getDeclaredField(name)
                } catch (_: NoSuchFieldException) {
                    null
                }


                if (field != null) {
                    field.withUnlock {
                        field[obj] = value
                    }
                    return ReflectResult.Success(value)
                }
                objClass = objClass.superclass
            }
        } catch (e: Exception) {
            return ReflectResult.ExceptionFail(e)
        }
        return ReflectResult.FAIL
    }


    inline fun callMethodTyped(clazz: Class<*>, obj: Any?, name: String, classes: Array<out Class<*>>, vararg args: Any?): ReflectResult {
        val method = clazz.getMethodOrNull(name, classes) ?: return ReflectResult.FAIL
        return try {
            ReflectResult.Success(method.withUnlock {
                method.invoke(obj, *args)
            })
        } catch (e: Exception) {
            ReflectResult.ExceptionFail(e)
        }
    }

    inline fun callMethod(clazz: Class<*>, obj: Any?, name: String, vararg args: Any?): ReflectResult {
        val classes = args.mapNotNull { it?.javaClass }.toTypedArray()
        return callMethodTyped(clazz, obj, name, classes, *args)
    }

    inline fun getMethodOrNull(clazz: Class<*>, name: String, classes: Array<out Class<*>>): Method? {
        return try {
            clazz.getMethod(name, *classes)
        } catch (_: NoSuchMethodException) {
            null
        }
    }

    fun getUnsafe(): Unsafe {
        val result = getField(Unsafe::class.java, null, "theUnsafe") as ReflectResult.Success
        return result.result as Unsafe
    }
}
