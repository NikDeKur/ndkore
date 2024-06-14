package dev.nikdekur.ndkore.events

import dev.nikdekur.ndkore.map.set.SetsHashMap
import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

@Suppress("unused")
class EventsController<EventClass : Any> {
    val registeredListeners = CopyOnWriteArrayList<InstanceMethodsBound>()

    fun invoke(event: EventClass) {
        for (bound in registeredListeners) {
            bound.invoke(event)
        }
    }

    fun registerListener(listener: EventListener) {
        val bound = InstanceMethodsBound(listener)
        registeredListeners.add(bound)
    }

    inner class InstanceMethodsBound(val instance: Any) {
        val map = SetsHashMap<Class<out EventClass>, Method>()

        init {
            for (method in instance.javaClass.methods) {
                checkAndAddMethod(method)
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun checkAndAddMethod(method: Method) {
            val annotation: Annotation? = method.getAnnotation(EventHandler::class.java)
            if (annotation == null || method.parameterCount != 1) return

            val argType = method.parameterTypes[0] as? Class<out EventClass> ?: return
            map.add(argType, method)
        }

        fun invoke(event: EventClass) {
            val clazz = event::class.java
            val methods: Collection<Method> = map[clazz]
            for (method in methods) {
                try {
                    method.invoke(instance, event)
                } catch (e: Exception) {
                    System.err.println("An internal error occurred while trying to invoke EventListener: $clazz$${method.name} with event: $event")
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        val DEFAULT: EventsController<Event> = EventsController()
    }
}
