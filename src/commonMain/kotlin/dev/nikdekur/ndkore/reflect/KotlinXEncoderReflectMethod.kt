package dev.nikdekur.ndkore.reflect

import dev.nikdekur.ndkore.reflect.ReflectMethod.NotFound
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

public class KotlinXEncoderReflectMethod(
    public val serializersModule: SerializersModule = SerializersModule { }
) : AbstractReflectMethod() {

    override val supportMethodCalling: Boolean
        get() = false

    @OptIn(ExperimentalSerializationApi::class)
    override fun findValue(obj: Any, name: String): Any? {
        val sup = super.findValue(obj, name)
        if (sup != NotFound) return sup

        val serializer = try {
            serializersModule.serializer(obj::class, emptyList(), false)
        } catch (e: SerializationException) {
            val isNotFound = e.message?.contains("not found") ?: false
            if (isNotFound) {
                // If serializer not found, we return NotFound
                return NotFound
            } else {
                // If it's another error, we rethrow it
                throw e
            }
        }

        val encoder = KotlinXValueFinder(name)
        serializer.serialize(encoder, obj)
        val result = encoder.result
        if (result === obj) {
            // If the result is the same as the object, we return NotFound
            return NotFound
        }

        return result
    }

    @OptIn(ExperimentalSerializationApi::class)
    public class KotlinXValueFinder(
        public val field: String,
        override val serializersModule: SerializersModule = SerializersModule { }
    ) : AbstractEncoder() {
        public var result: Any? = NotFound

        override fun encodeNull() {
            result = null
        }

        override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
            println("encodeElement: $field -> ${descriptor.getElementName(index)}")
            return descriptor.getElementName(index) == field
        }


        override fun encodeValue(value: Any) {
            println("encodeValue: $field -> $value")
            result = value
        }


        override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
            println("encodeSerializableValue: $field -> $value")
            result = value
        }

        override fun <T : Any> encodeNullableSerializableValue(serializer: SerializationStrategy<T>, value: T?) {
            println("encodeNullableSerializableValue: $field -> $value")
            result = value
        }
    }
}

