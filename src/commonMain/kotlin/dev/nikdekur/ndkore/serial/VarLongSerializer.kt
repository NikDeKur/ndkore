@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.serial

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public object VarLongSerializer : KSerializer<Long> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("VarLong", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Long) {
        var v = (value shl 1) xor (value shr 31) // ZigZag Encoding
        while (true) {
            val bits = v and 0x7F
            v = v ushr 7
            encoder.encodeByte((bits or if (v != 0L) 0x80 else 0).toByte())
            if (v == 0L) break
        }
    }

    override fun deserialize(decoder: Decoder): Long {
        var result = 0L
        var shift = 0
        for (i in 0 until 9) { // Max 9 bytes for 32-bit Longe
            val byte = decoder.decodeByte().toLong()
            result = result or ((byte and 0x7F) shl shift)
            if (byte and 0x80 == 0L) {
                return (result ushr 1) xor -(result and 1) // ZigZag Decoding
            }
            shift += 7
        }

        throw IllegalArgumentException("VarLong is too big")
    }
}


public inline fun Encoder.encodeVarLong(value: Long) {
    encodeSerializableValue(VarLongSerializer, value)
}

public inline fun Decoder.decodeVarLong(): Long {
    return decodeSerializableValue(VarLongSerializer)
}

public inline fun CompositeEncoder.encodeVarLong(descriptor: SerialDescriptor, index: Int, value: Long) {
    encodeSerializableElement(descriptor, index, VarLongSerializer, value)
}

public inline fun CompositeDecoder.decodeVarLong(
    descriptor: SerialDescriptor,
    index: Int,
    previousValue: Long? = null
): Long {
    return decodeSerializableElement(descriptor, index, VarLongSerializer, previousValue)
}
