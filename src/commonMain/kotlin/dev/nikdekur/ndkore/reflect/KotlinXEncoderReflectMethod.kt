package dev.nikdekur.ndkore.reflect

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

public class KotlinXEncoderReflectMethod(
    public val serializersModule: SerializersModule = SerializersModule { }
) : ReflectMethod {

    @OptIn(ExperimentalSerializationApi::class)
    override fun findValue(obj: Any, name: String): Any? {
        val encoder = KotlinXValueFinder(name)
        val serializer = serializersModule.serializer(obj::class, emptyList(), false)
        serializer.serialize(encoder, obj)
        return encoder.result
    }

    @OptIn(ExperimentalSerializationApi::class)
    public class KotlinXValueFinder(
        public val field: String,
        override val serializersModule: SerializersModule = SerializersModule { }
    ) : AbstractEncoder() {

        public var result: Any? = null

        override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
            return descriptor.getElementName(index) == field
        }

        override fun encodeValue(value: Any) {
            result = value
        }
    }
}

