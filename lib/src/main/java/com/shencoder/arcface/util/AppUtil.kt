package com.shencoder.arcface.util

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * @author ShenBen
 * @date 2019/12/4 17:21
 * @email 714081644@qq.com
 */
internal object AppUtil {
    /**
     * Used to build output as Hex
     */
    private val DIGITS_LOWER = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
        'e', 'f'
    )

    /**
     * Used to build output as Hex
     */
    private val DIGITS_UPPER = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
        'E', 'F'
    )

    fun md5EncryptAndBase64(str: String): String {
        return encodeBase64(md5Encrypt(str))
    }

    /**
     * md5加密
     */
    private fun md5Encrypt(encryptStr: String): ByteArray {
        return try {
            val md5 = MessageDigest.getInstance("MD5")
            md5.update(encryptStr.toByteArray(StandardCharsets.UTF_8))
            md5.digest()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * base64加密
     */
    private fun encodeBase64(b: ByteArray): String {
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    @Throws(Exception::class)
    fun decodeHex(data: String): ByteArray {
        return decodeHex(data.toCharArray())
    }

    @Throws(Exception::class)
    fun decodeHex(data: CharArray): ByteArray {
        val len = data.size
        if (len and 0x01 != 0) {
            throw Exception("Odd number of characters.")
        }
        val out = ByteArray(len shr 1)
        // two characters form the hex value.
        var i = 0
        var j = 0
        while (j < len) {
            var f = toDigit(data[j], j) shl 4
            j++
            f = f or toDigit(data[j], j)
            j++
            out[i] = (f and 0xFF).toByte()
            i++
        }
        return out
    }

    @Throws(Exception::class)
    private fun toDigit(ch: Char, index: Int): Int {
        val digit = Character.digit(ch, 16)
        if (digit == -1) {
            throw Exception("Illegal hexadecimal character $ch at index $index")
        }
        return digit
    }

    fun encodeHexString(data: ByteArray, toLowerCase: Boolean): String {
        return String(encodeHex(data, toLowerCase))
    }

    @JvmOverloads
    fun encodeHex(data: ByteArray, toLowerCase: Boolean = true): CharArray {
        return encodeHex(data, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
    }

    private fun encodeHex(data: ByteArray, toDigits: CharArray): CharArray {
        val l = data.size
        val out = CharArray(l shl 1)
        // two characters form the hex value.
        var i = 0
        var j = 0
        while (i < l) {
            out[j++] = toDigits[0xF0 and data[i].toInt() ushr 4]
            out[j++] = toDigits[0x0F and data[i].toInt()]
            i++
        }
        return out
    }
}