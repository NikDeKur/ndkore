@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

/**
 * Reads a byte from the specified index in the byte array.
 *
 * Retrieves the byte value at the provided index from this byte array.
 *
 * @param index The index from which to read the byte.
 * @return The byte value at the specified index.
 */
public inline fun ByteArray.readByte(index: Int): Byte {
    return this[index]
}


/**
 * Reads an unsigned byte from the specified index in the byte array.
 *
 * Retrieves the byte at the given index and converts it to an unsigned byte.
 *
 * @param index The index from which to read the unsigned byte.
 * @return The unsigned byte value at the specified index.
 */
public inline fun ByteArray.readUByte(index: Int): UByte {
    return readByte(index).toUByte()
}


/**
 * Reads a short from the specified index in the byte array.
 *
 * Retrieves two bytes starting from the given index and combines them into a short value.
 *
 * @param index The starting index from which to read the short.
 * @return The short value constructed from two bytes.
 */
public inline fun ByteArray.readShort(index: Int): Short {
    return ((this[index].toInt() and 0xFF shl 8) or (this[index + 1].toInt() and 0xFF)).toShort()
}


/**
 * Reads an unsigned short from the specified index in the byte array.
 *
 * Retrieves two bytes starting from the given index, converts them into a short, and then converts it to an unsigned short.
 *
 * @param index The starting index from which to read the unsigned short.
 * @return The unsigned short value.
 */
public inline fun ByteArray.readUShort(index: Int): UShort {
    return readShort(index).toUShort()
}


/**
 * Reads an integer from the specified index in the byte array.
 *
 * Combines four consecutive bytes starting from the given index into an integer value.
 *
 * @param index The starting index from which to read the integer.
 * @return The integer value constructed from four bytes.
 */
public inline fun ByteArray.readInt(index: Int): Int {
    return ((this[index].toInt() and 0xFF shl 24) or
            (this[index + 1].toInt() and 0xFF shl 16) or
            (this[index + 2].toInt() and 0xFF shl 8) or
            (this[index + 3].toInt() and 0xFF))
}


/**
 * Reads an unsigned integer from the specified index in the byte array.
 *
 * Combines four bytes starting from the given index into an integer and then converts it to an unsigned integer.
 *
 * @param index The starting index from which to read the unsigned integer.
 * @return The unsigned integer value.
 */
public inline fun ByteArray.readUInt(index: Int): UInt {
    return readInt(index).toUInt()
}


/**
 * Reads a long from the specified index in the byte array.
 *
 * Combines eight consecutive bytes starting from the given index into a long value.
 *
 * @param index The starting index from which to read the long.
 * @return The long value constructed from eight bytes.
 */
public inline fun ByteArray.readLong(index: Int): Long {
    return ((this[index].toLong() and 0xFF shl 56) or
            (this[index + 1].toLong() and 0xFF shl 48) or
            (this[index + 2].toLong() and 0xFF shl 40) or
            (this[index + 3].toLong() and 0xFF shl 32) or
            (this[index + 4].toLong() and 0xFF shl 24) or
            (this[index + 5].toLong() and 0xFF shl 16) or
            (this[index + 6].toLong() and 0xFF shl 8) or
            (this[index + 7].toLong() and 0xFF))
}


/**
 * Reads an unsigned long from the specified index in the byte array.
 *
 * Combines eight bytes starting from the given index into a long and converts it to an unsigned long.
 *
 * @param index The starting index from which to read the unsigned long.
 * @return The unsigned long value.
 */
public inline fun ByteArray.readULong(index: Int): ULong {
    return readLong(index).toULong()
}


/**
 * Reads a float from the specified index in the byte array.
 *
 * Combines four bytes starting from the given index into an integer and converts it to a float.
 *
 * @param index The starting index from which to read the float.
 * @return The float value.
 */
public inline fun ByteArray.readFloat(index: Int): Float {
    return Float.fromBits(readInt(index))
}


/**
 * Reads a double from the specified index in the byte array.
 *
 * Combines eight bytes starting from the given index into a long and converts it to a double.
 *
 * @param index The starting index from which to read the double.
 * @return The double value.
 */
public inline fun ByteArray.readDouble(index: Int): Double {
    return Double.fromBits(readLong(index))
}


/**
 * Writes a byte to the specified index in the byte array.
 *
 * Sets the byte value at the provided index in this byte array.
 *
 * @param index The index at which to write the byte.
 * @param value The byte value to write.
 */
public inline fun ByteArray.writeByte(index: Int, value: Byte) {
    this[index] = value
}


/**
 * Writes an unsigned byte to the specified index in the byte array.
 *
 * Converts the unsigned byte to a regular byte and writes it at the given index.
 *
 * @param index The index at which to write the unsigned byte.
 * @param value The unsigned byte value to write.
 */
public inline fun ByteArray.writeUByte(index: Int, value: UByte) {
    writeByte(index, value.toByte())
}


/**
 * Writes a short to the specified index in the byte array.
 *
 * Sets two consecutive bytes starting from the given index to represent the short value.
 *
 * @param index The starting index at which to write the short.
 * @param value The short value to write.
 */
public inline fun ByteArray.writeShort(index: Int, value: Short) {
    this[index] = (value.toInt() shr 8).toByte()
    this[index + 1] = value.toByte()
}


/**
 * Writes an unsigned short to the specified index in the byte array.
 *
 * Converts the unsigned short to a regular short and writes it at the given index.
 *
 * @param index The starting index at which to write the unsigned short.
 * @param value The unsigned short value to write.
 */
public inline fun ByteArray.writeUShort(index: Int, value: UShort) {
    writeShort(index, value.toShort())
}


/**
 * Writes an integer to the specified index in the byte array.
 *
 * Sets four consecutive bytes starting from the given index to represent the integer value.
 *
 * @param index The starting index at which to write the integer.
 * @param value The integer value to write.
 */
public inline fun ByteArray.writeInt(index: Int, value: Int) {
    this[index] = (value shr 24).toByte()
    this[index + 1] = (value shr 16).toByte()
    this[index + 2] = (value shr 8).toByte()
    this[index + 3] = value.toByte()
}


/**
 * Writes an unsigned integer to the specified index in the byte array.
 *
 * Converts the unsigned integer to a regular integer and writes it at the given index.
 *
 * @param index The starting index at which to write the unsigned integer.
 * @param value The unsigned integer value to write.
 */
public inline fun ByteArray.writeUInt(index: Int, value: UInt) {
    writeInt(index, value.toInt())
}


/**
 * Writes a long to the specified index in the byte array.
 *
 * Sets eight consecutive bytes starting from the given index to represent the long value.
 *
 * @param index The starting index at which to write the long.
 * @param value The long value to write.
 */
public inline fun ByteArray.writeLong(index: Int, value: Long) {
    this[index] = (value shr 56).toByte()
    this[index + 1] = (value shr 48).toByte()
    this[index + 2] = (value shr 40).toByte()
    this[index + 3] = (value shr 32).toByte()
    this[index + 4] = (value shr 24).toByte()
    this[index + 5] = (value shr 16).toByte()
    this[index + 6] = (value shr 8).toByte()
    this[index + 7] = value.toByte()
}


/**
 * Writes an unsigned long to the specified index in the byte array.
 *
 * Converts the unsigned long to a regular long and writes it at the given index.
 *
 * @param index The starting index at which to write the unsigned long.
 * @param value The unsigned long value to write.
 */
public inline fun ByteArray.writeULong(index: Int, value: ULong) {
    writeLong(index, value.toLong())
}


/**
 * Writes a float to the specified index in the byte array.
 *
 * Converts the float to its bit representation and writes it at the given index.
 *
 * @param index The starting index at which to write the float.
 * @param value The float value to write.
 */
public inline fun ByteArray.writeFloat(index: Int, value: Float) {
    writeInt(index, value.toBits())
}


/**
 * Writes a double to the specified index in the byte array.
 *
 * Converts the double to its bit representation and writes it at the given index.
 *
 * @param index The starting index at which to write the double.
 * @param value The double value to write.
 */
public inline fun ByteArray.writeDouble(index: Int, value: Double) {
    writeLong(index, value.toBits())
}


/**
 * Compares two byte arrays in constant time.
 *
 * This function checks if two byte arrays are equal in a way that is resistant to timing attacks.
 *
 * @param other The byte array to compare with.
 * @return `true` if the byte arrays are equal, `false` otherwise.
 */
public fun ByteArray.constantTimeEquals(other: ByteArray): Boolean {
    if (this.size != other.size) return false
    var result = 0
    for (i in indices) {
        result = result or (this[i].toInt() xor other[i].toInt())
    }
    return result == 0
}
