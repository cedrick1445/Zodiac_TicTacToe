package dev.cdrck.mdgtictoe.util

import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptUtil {
    private const val METHOD = "AES/CBC/PKCS5Padding"
    private const val IV = "fedcba9876543210"

    @Throws(Exception::class)
    fun encrypt(message: String, key: String): String {
        val secretKeySpec = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance(METHOD)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, IvParameterSpec(IV.toByteArray()))
        val encryptedBytes = cipher.doFinal(message.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    @Throws(Exception::class)
    fun decrypt(message: String?, key: String): String {
        message ?: throw IllegalArgumentException("Message cannot be null")

        val secretKeySpec = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance(METHOD)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(IV.toByteArray()))
        val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(message))
        val trimmedBytes = decryptedBytes.copyOfRange(16, decryptedBytes.size)
        return String(trimmedBytes, StandardCharsets.UTF_8)
    }
}
