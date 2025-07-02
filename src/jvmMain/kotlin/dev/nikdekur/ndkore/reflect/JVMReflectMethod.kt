package dev.nikdekur.ndkore.reflect

import dev.nikdekur.ndkore.ext.asCamelCaseGetter
import dev.nikdekur.ndkore.ext.r_CallMethod
import dev.nikdekur.ndkore.ext.r_GetField
import dev.nikdekur.ndkore.reflect.ReflectMethod.NotFound

public open class JVMReflectMethod : AbstractReflectMethod() {

    override val supportMethodCalling: Boolean
        get() = true

    override fun findValue(obj: Any, name: String): Any? {
        val sup = super.findValue(obj, name)
        if (sup != NotFound) return sup

        val asField = findAsField(obj, name)
        if (asField != NotFound) return asField

        val asMethod = findAsMethod(obj, name)
        if (asMethod != NotFound) return asMethod

        return NotFound
    }

    public open fun findAsField(obj: Any, name: String): Any? {
        val result = obj.r_GetField(name)
        if (result != ReflectResult.Missing) return result.value

        return NotFound
    }

    public open fun findAsMethod(obj: Any, name: String): Any? {
        val result1 = obj.r_CallMethod(name)
        if (result1 != ReflectResult.Missing) return result1.value

        val result2 = obj.r_CallMethod(name.asCamelCaseGetter())
        if (result2 != ReflectResult.Missing) return result2.value

        return NotFound
    }

    public companion object Default : JVMReflectMethod()
}