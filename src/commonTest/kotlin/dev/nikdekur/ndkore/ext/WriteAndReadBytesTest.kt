package dev.nikdekur.ndkore.ext

import kotlin.test.Test
import kotlin.test.assertEquals

class WriteAndReadBytesTest {

    @Test
    fun testReadByte() {
        val bytes = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        val byte = bytes.readByte(0)
        assertEquals(0x01.toByte(), byte)
    }

    @Test
    fun testReadByte2() {
        val bytes = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        val byte = bytes.readByte(2)
        assertEquals(0x03.toByte(), byte)
    }

    @Test
    fun testReadUnsignedByte() {
        val bytes = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        val byte = bytes.readUByte(0)
        assertEquals(0x01.toUByte(), byte)
    }

    @Test
    fun testReadUnsignedByte2() {
        val bytes = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        val byte = bytes.readUByte(2)
        assertEquals(0x03.toUByte(), byte)
    }


    @Test
    fun testReadShort() {
        val byteArray = ByteArray(4)
        byteArray.writeShort(0, 25.toShort())
        byteArray.writeShort(2, 50.toShort())

        val short1 = byteArray.readShort(0)
        val short2 = byteArray.readShort(2)

        assertEquals(25.toShort(), short1)
        assertEquals(50.toShort(), short2)
    }

    @Test
    fun testReadShort2() {
        val byteArray = ByteArray(4)
        byteArray.writeShort(0, 25.toShort())
        byteArray.writeShort(2, 50.toShort())

        val short1 = byteArray.readShort(2)
        val short2 = byteArray.readShort(0)

        assertEquals(50.toShort(), short1)
        assertEquals(25.toShort(), short2)
    }


    @Test
    fun testReadUnsignedShort() {
        val byteArray = ByteArray(4)
        byteArray.writeShort(0, 25.toShort())
        byteArray.writeShort(2, 50.toShort())

        val short1 = byteArray.readUShort(0)
        val short2 = byteArray.readUShort(2)

        assertEquals(25.toUShort(), short1)
        assertEquals(50.toUShort(), short2)
    }


    @Test
    fun testReadUnsignedShort2() {
        val byteArray = ByteArray(4)
        byteArray.writeShort(0, 25.toShort())
        byteArray.writeShort(2, 50.toShort())

        val short1 = byteArray.readUShort(2)
        val short2 = byteArray.readUShort(0)

        assertEquals(50.toUShort(), short1)
        assertEquals(25.toUShort(), short2)
    }


    @Test
    fun testReadInt() {
        val byteArray = ByteArray(8)
        byteArray.writeInt(0, 25)
        byteArray.writeInt(4, 50)

        val int1 = byteArray.readInt(0)
        val int2 = byteArray.readInt(4)

        assertEquals(25, int1)
        assertEquals(50, int2)
    }


    @Test
    fun testReadInt2() {
        val byteArray = ByteArray(8)
        byteArray.writeInt(0, 25)
        byteArray.writeInt(4, 50)

        val int1 = byteArray.readInt(4)
        val int2 = byteArray.readInt(0)

        assertEquals(50, int1)
        assertEquals(25, int2)
    }


    @Test
    fun testReadUnsignedInt() {
        val byteArray = ByteArray(8)
        byteArray.writeUInt(0, 25u)
        byteArray.writeUInt(4, 50u)

        val int1 = byteArray.readUInt(0)
        val int2 = byteArray.readUInt(4)

        assertEquals(25u, int1)
        assertEquals(50u, int2)
    }


    @Test
    fun testReadUnsignedInt2() {
        val byteArray = ByteArray(8)
        byteArray.writeUInt(0, 25u)
        byteArray.writeUInt(4, 50u)

        val int1 = byteArray.readUInt(4)
        val int2 = byteArray.readUInt(0)

        assertEquals(50u, int1)
        assertEquals(25u, int2)
    }


    @Test
    fun testReadLong() {
        val byteArray = ByteArray(16)
        byteArray.writeLong(0, 25L)
        byteArray.writeLong(8, 50L)

        val long1 = byteArray.readLong(0)
        val long2 = byteArray.readLong(8)

        assertEquals(25L, long1)
        assertEquals(50L, long2)
    }

    @Test
    fun testReadLong2() {
        val byteArray = ByteArray(16)
        byteArray.writeLong(0, 25L)
        byteArray.writeLong(8, 50L)

        val long1 = byteArray.readLong(8)
        val long2 = byteArray.readLong(0)

        assertEquals(50L, long1)
        assertEquals(25L, long2)
    }


    @Test
    fun testReadUnsignedLong() {
        val byteArray = ByteArray(16)
        byteArray.writeULong(0, 25u)
        byteArray.writeULong(8, 50u)

        val long1 = byteArray.readULong(0)
        val long2 = byteArray.readULong(8)

        assertEquals(25u, long1)
        assertEquals(50u, long2)
    }


    @Test
    fun testReadUnsignedLong2() {
        val byteArray = ByteArray(16)
        byteArray.writeLong(0, 25L)
        byteArray.writeLong(8, 50L)

        val long1 = byteArray.readULong(8)
        val long2 = byteArray.readULong(0)

        assertEquals(50.toULong(), long1)
        assertEquals(25.toULong(), long2)
    }


    @Test
    fun testReadFloat() {
        val byteArray = ByteArray(8)
        byteArray.writeFloat(0, 25.50f)
        byteArray.writeFloat(4, 50.25f)

        val float1 = byteArray.readFloat(0)
        val float2 = byteArray.readFloat(4)

        assertEquals(25.50f, float1.round(2))
        assertEquals(50.25f, float2.round(2))
    }


    @Test
    fun testReadFloat2() {
        val byteArray = ByteArray(8)
        byteArray.writeFloat(0, 75.50f)
        byteArray.writeFloat(4, 100.69f)

        val float1 = byteArray.readFloat(4)
        val float2 = byteArray.readFloat(0)

        assertEquals(100.69f, float1.round(2))
        assertEquals(75.50f, float2.round(2))
    }


    @Test
    fun testReadDouble() {
        val byteArray = ByteArray(16)
        byteArray.writeDouble(0, 25.50)
        byteArray.writeDouble(8, 50.25)

        val double1 = byteArray.readDouble(0)
        val double2 = byteArray.readDouble(8)

        assertEquals(25.50, double1.round(2))
        assertEquals(50.25, double2.round(2))
    }


    @Test
    fun testReadDouble2() {
        val byteArray = ByteArray(16)
        byteArray.writeDouble(0, 75.50)
        byteArray.writeDouble(8, 100.69)

        val double1 = byteArray.readDouble(8)
        val double2 = byteArray.readDouble(0)

        assertEquals(100.69, double1.round(2))
        assertEquals(75.50, double2.round(2))
    }


    @Test
    fun testComplex() {
        val byteArray = ByteArray(8)

        byteArray.writeInt(0, 25)
        byteArray.writeInt(4, 50)

        // Read all the bytes
        val long = byteArray.readLong(0)
        assertEquals(107374182450, long)
    }
}