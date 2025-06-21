package dev.nikdekur.ndkore.compress

import dev.nikdekur.ndkore.serial.VarIntSerializer
import dev.nikdekur.ndkore.serial.decodeVarInt
import dev.nikdekur.ndkore.serial.encodeVarInt
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.jpountz.lz4.LZ4Factory

public fun ByteArray.lz4Compress(): ByteArray {
    val compressor = LZ4Factory.fastestInstance().fastCompressor()
    val maxCompressedLength = compressor.maxCompressedLength(this.size)
    val output = ByteArray(maxCompressedLength)
    val compressedSize = compressor.compress(this, 0, this.size, output, 0, maxCompressedLength)
    return output.copyOf(compressedSize)
}

public fun ByteArray.lz4Decompress(originalSize: Int): ByteArray {
    val decompressor = LZ4Factory.fastestInstance().fastDecompressor()
    val output = ByteArray(originalSize)
    decompressor.decompress(this, 0, output, 0, originalSize)
    return output
}

public object LZ4ByteArraySerializer : KSerializer<ByteArray> {
    public val byteArraySerializer: KSerializer<ByteArray> = ByteArraySerializer()

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("LZ4ByteArray") {
        element("size", VarIntSerializer.descriptor)
        element("data", byteArraySerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: ByteArray) {
        encoder.beginStructure(descriptor).apply {
            // Original Size
            encodeVarInt(descriptor, 0, value.size)

            val compressed = value.lz4Compress()
            encodeSerializableElement(descriptor, 1, byteArraySerializer, compressed)

            endStructure(descriptor)
        }
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        val compositeDecoder = decoder.beginStructure(descriptor)
        var originalSize: Int? = null
        var compressed: ByteArray? = null

        loop@ while (true) {
            when (val index = compositeDecoder.decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> break@loop
                0 -> originalSize = compositeDecoder.decodeVarInt(descriptor, 0)
                1 -> compressed = compositeDecoder.decodeSerializableElement(descriptor, 1, byteArraySerializer)
                else -> throw SerializationException("Unexpected index: $index")
            }
        }
        compositeDecoder.endStructure(descriptor)

        if (originalSize == null || compressed == null)
            throw SerializationException("Not enough data to deserialize")
        return compressed.lz4Decompress(originalSize)
    }
}