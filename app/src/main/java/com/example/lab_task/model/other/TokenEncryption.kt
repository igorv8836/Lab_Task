package com.example.lab_task.model.other

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object TokenEncryption {
    @SuppressLint("GetInstance")
    fun encryptToken(token: String): String {
        val key = "f3nvdf4wnn234nfiw92qz17vk38sowi2"
        val secretKey: SecretKey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(token.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    @SuppressLint("GetInstance")
    fun decryptToken(encryptedToken: String): String {
        val key = "f3nvdf4wnn234nfiw92qz17vk38sowi2"
        val secretKey: SecretKey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.getDecoder().decode(encryptedToken)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes)
    }
}