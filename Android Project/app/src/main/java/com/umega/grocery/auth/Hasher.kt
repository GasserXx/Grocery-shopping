package com.umega.grocery.auth

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Hasher {
    fun hashPassword(password: String): String? {
        try {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val passwordBytes = password.toByteArray(StandardCharsets.UTF_8)
            val hashedBytes = messageDigest.digest(passwordBytes)

            // Convert hashed bytes to hexadecimal format
            val stringBuilder = StringBuilder()
            for (hashedByte in hashedBytes) {
                stringBuilder.append(String.format("%02x", hashedByte))
            }

            return stringBuilder.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null // Handle the exception according to your needs
        }
    }
}
