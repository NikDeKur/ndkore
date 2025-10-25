/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:OptIn(ExperimentalSerializationApi::class, ExperimentalUuidApi::class, ExperimentalTime::class)

package dev.nikdekur.ndkore.ext

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import dev.nikdekur.ndkore.memory.MemoryAmount
import dev.nikdekur.ndkore.memory.MemoryUnit
import dev.nikdekur.ndkore.memory.toBigInteger
import dev.nikdekur.ndkore.serial.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.SerializersModule
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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


public object BigIntegerSerializer : KSerializer<BigInteger> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigInteger", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: BigInteger
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): BigInteger {
        return BigInteger.parseString(decoder.decodeString())
    }
}


public object LenientMemoryUnitSerializer : KSerializer<MemoryUnit> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MemoryUnit", PrimitiveKind.STRING)

    internal fun matchUnitToObject(unit: String): MemoryUnit {
        return when (unit.uppercase()) {
            "B", "BYTE", "BYTES" -> MemoryUnit.Byte
            "KB", "KIB", "KILOBYTE", "KIBIBYTE", "KILOBYTES", "KIBIBYTES" -> MemoryUnit.KiB
            "MB", "MIB", "MEGABYTE", "MEBIBYTE", "MEGABYTES", "MEGABYTES" -> MemoryUnit.MiB
            "GB", "GIB", "GIGABYTE", "GIBIBYTE", "GIGABYTES", "GIBIBYTES" -> MemoryUnit.GiB
            "TB", "TIB", "TERABYTE", "TEBIBYTE", "TERABYTES", "TEBIBYTES" -> MemoryUnit.TiB
            "PB", "PIB", "PETABYTE", "PEBIBYTE", "PETABYTES", "PEBIBYTES" -> MemoryUnit.PiB
            "EB", "EIB", "EXABYTE", "EXBIBYTE", "EXABYTES", "EXBIBYTES" -> MemoryUnit.EiB
            "ZB", "ZIB", "ZETTABYTE", "ZEBIBYTE", "ZETTABYTES", "ZEBIBYTES" -> MemoryUnit.ZiB
            "YB", "YIB", "YOTTABYTE", "YOBIBYTE", "YOTTABYTES", "YOBIBYTES" -> MemoryUnit.YiB
            else -> throw IllegalArgumentException("Unknown memory unit for LenientMemoryUnitSerializer: $unit")
        }
    }

    internal fun matchObjectToUnit(unit: MemoryUnit): String {
        return when (unit) {
            MemoryUnit.Byte -> "B"
            MemoryUnit.KiB -> "KiB"
            MemoryUnit.MiB -> "MiB"
            MemoryUnit.GiB -> "GiB"
            MemoryUnit.TiB -> "TiB"
            MemoryUnit.PiB -> "PiB"
            MemoryUnit.EiB -> "EiB"
            MemoryUnit.ZiB -> "ZiB"
            MemoryUnit.YiB -> "YiB"
            else -> throw IllegalArgumentException("Unknown memory unit for LenientMemoryUnitSerializer: $unit")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: MemoryUnit
    ) {
        encoder.encodeString(matchObjectToUnit(value))
    }

    override fun deserialize(decoder: Decoder): MemoryUnit {
        return matchUnitToObject(decoder.decodeString())
    }
}


public object LenientMemoryAmountSerializer : KSerializer<MemoryAmount> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MemoryAmount", PrimitiveKind.STRING)

    internal val deserializeRegex = """(\d+)\s*(\w+)""".toRegex()

    internal val units by lazy {
        listOf(
            MemoryUnit.Byte,
            MemoryUnit.KiB,
            MemoryUnit.MiB,
            MemoryUnit.GiB,
            MemoryUnit.TiB,
            MemoryUnit.PiB,
            MemoryUnit.EiB,
            MemoryUnit.ZiB,
            MemoryUnit.YiB
        )
    }

    internal fun findSuitableUnit(amount: MemoryAmount): MemoryUnit {
        return units.lastOrNull {
            amount.bytes % it.bytes == BigInteger.ZERO
        } ?: MemoryUnit.Byte
    }

    override fun serialize(
        encoder: Encoder,
        value: MemoryAmount
    ) {
        val unit = findSuitableUnit(value)
        val valueStr = value.toBigInteger(unit)
        val unitStr = LenientMemoryUnitSerializer.matchObjectToUnit(unit)
        encoder.encodeString("$valueStr$unitStr")
    }

    override fun deserialize(decoder: Decoder): MemoryAmount {
        val amount = decoder.decodeString()
        val (valueStr, unitStr) = deserializeRegex.find(amount)!!.destructured
        val value = valueStr.toBigInteger()
        val unit = LenientMemoryUnitSerializer.matchUnitToObject(unitStr)
        return MemoryAmount(value, unit)
    }
}

public object IntRangeSerializer : KSerializer<IntRange> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IntRange", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: IntRange) {
        val range = value.first..value.last
        encoder.encodeString(range.toString())
    }

    override fun deserialize(decoder: Decoder): IntRange {
        val range = decoder.decodeString()
        val (start, end) = range.split("..")
        return start.toInt()..end.toInt()
    }
}


public object StringUUIDSerializer : KSerializer<Uuid> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Uuid", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Uuid) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uuid {
        return Uuid.parse(decoder.decodeString())
    }
}


public object LongsUUIDSerializer : KSerializer<Uuid> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Uuid", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Uuid) {
        value.toLongs { most, least ->
            encoder.encodeLong(most)
            encoder.encodeLong(least)
        }
    }

    override fun deserialize(decoder: Decoder): Uuid {
        return Uuid.fromLongs(
            mostSignificantBits = decoder.decodeLong(),
            leastSignificantBits = decoder.decodeLong()
        )
    }
}


public object InstantSecAndMsSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("kotlinx.datetime.Instant") {
            element("epochSeconds", VarLongSerializer.descriptor)
            element("millisecondsOfSecond", VarIntSerializer.descriptor, isOptional = true)
        }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Instant =
        decoder.decodeStructure(descriptor) {
            var epochSeconds: Long? = null
            var millisecondsOfSecond = 0
            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> epochSeconds = decodeVarLong(descriptor, 0)
                    1 -> millisecondsOfSecond = decodeVarInt(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break@loop // https://youtrack.jetbrains.com/issue/KT-42262
                    else -> throw SerializationException("Unexpected index: $index")
                }
            }
            if (epochSeconds == null) throw MissingFieldException(
                missingField = "epochSeconds",
                serialName = descriptor.serialName
            )
            Instant.fromEpochSeconds(
                epochSeconds = epochSeconds,
                nanosecondAdjustment = millisecondsOfSecond * 1_000_000
            )
        }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeStructure(descriptor) {
            encodeVarLong(descriptor, 0, value.epochSeconds)
            if (value.nanosecondsOfSecond != 0) {
                encodeVarInt(descriptor, 1, value.nanosecondsOfSecond / 1_000_000)
            }
        }
    }
}