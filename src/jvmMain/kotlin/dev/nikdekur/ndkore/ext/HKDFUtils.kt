@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import java.io.ByteArrayOutputStream
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

public object HKDFUtils {

    /**
     * Creates a new instance of HMAC-SHA256.
     *
     * This method is thread-safe and can be called multiple times.
     * It is not implemented as a field to avoid potential issues with thread safety.
     *
     * @return A new instance of HMAC-SHA256.
     */
    public inline fun newHmacSHA256(): Mac {
        return Mac.getInstance("HmacSHA256")
    }


    /**
     * Implementation of HKDF (RFC 5869) using HMAC-SHA256.
     *
     * @param ikm The input keying material
     * @param info Context-dependent information (can be empty, but recommended to be non-empty)
     * @param outputLength Desired length of the output key in bytes
     * @return The pair of derived key and salt
     */
    public fun extractAndExpand(ikm: ByteArray, info: ByteArray, salt: ByteArray, outputLength: Int): ByteArray {
        val mac = newHmacSHA256()
        mac.init(SecretKeySpec(salt, "HmacSHA256"))

        // HKDF-Extract
        val prk = mac.doFinal(ikm)

        // HKDF-Expand
        val okmStream = ByteArrayOutputStream()
        var previousBlock = ByteArray(0)
        var counter = 1.toByte()
        while (okmStream.size() < outputLength) {
            mac.init(SecretKeySpec(prk, "HmacSHA256"))
            mac.reset()
            mac.update(previousBlock)
            mac.update(info)
            mac.update(counter)
            previousBlock = mac.doFinal()
            okmStream.write(previousBlock)
            counter++
        }
        val okm = okmStream.toByteArray()
        return okm.copyOfRange(0, outputLength)
    }
}