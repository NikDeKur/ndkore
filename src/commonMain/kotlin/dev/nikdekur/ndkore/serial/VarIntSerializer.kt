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

public object VarIntSerializer : KSerializer<Int> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("VarInt", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Int) {
        if (value == 0) {
            encoder.encodeByte(0)
            return
        }
        var v = (value shl 1) xor (value shr 31) // ZigZag Encoding
        do {
            val bits = v and 0x7F
            v = v ushr 7
            encoder.encodeByte((bits or if (v != 0) 0x80 else 0).toByte())
        } while (v != 0)
    }

    override fun deserialize(decoder: Decoder): Int {
        var result = 0
        var shift = 0
        for (i in 0 until 5) { // Max 5 bytes for 32-bit integer
            val byte = decoder.decodeByte().toInt()
            result = result or ((byte and 0x7F) shl shift)
            if (byte and 0x80 == 0) {
                return (result ushr 1) xor -(result and 1) // ZigZag Decoding
            }
            shift += 7
        }

        throw IllegalArgumentException("VarInt is too big")
    }
}


public inline fun Encoder.encodeVarInt(value: Int) {
    encodeSerializableValue(VarIntSerializer, value)
}

public inline fun Decoder.decodeVarInt(): Int {
    return decodeSerializableValue(VarIntSerializer)
}

public inline fun CompositeEncoder.encodeVarInt(descriptor: SerialDescriptor, index: Int, value: Int) {
    encodeSerializableElement(descriptor, index, VarIntSerializer, value)
}

public inline fun CompositeDecoder.decodeVarInt(
    descriptor: SerialDescriptor,
    index: Int,
    previousValue: Int? = null
): Int {
    return decodeSerializableElement(descriptor, index, VarIntSerializer, previousValue)
}

