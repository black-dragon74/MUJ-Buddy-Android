package com.black_dragon74.mujbuddy.utils

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

// Don't change it. It will break the whole functionality of the app
const val CRYPTO_METHOD = "RSA"
const val CRYPTO_TRANSFORM = "RSA/None/PKCS1Padding"

class Encryptor {

    private val pKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzkJpTLAu8EDpp82Wu9u8Fi9mF6noCyzB2Xqms90xpEvlbW4B4hwtalkorzXAFo49zGezjiTGte5qXna//kNTBxLtIozkyVT1iO/mIamiCirgqMiLVQOfgE1zRde7/eBij8GQ//ZUmKfNVrutn3cidthOERrxLbUC/d1endJ6GSgW/k4cZy4ZKMSy0sJH9T2niS0R4bauEp41R3pp/2beCLKn+JZKn2pDpMdoiWLzRbgMIwSlJuuMNB1qaymtz8m/4LpUWdbWyyxxf8H3XXYxe+uVVtKAHQeqOEnFWUsWmnG/ILljSJTq27dCHKREoYzfG4zR9wEXty+L29MC+jcKHwIDAQAB"

    // This extension on string converts the string to a Public key instance object
    private fun String.toPublicKey(): PublicKey {
        val keyBytes: ByteArray = Base64.decode(this, Base64.DEFAULT)
        val spec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(CRYPTO_METHOD)

        return keyFactory.generatePublic(spec)
    }

    // Encrypts a string with PKCS1 padding
    fun encrypt(message: String, key: String = pKey): String {
        val encryptedBytes: ByteArray
        val pubKey: PublicKey? = key.toPublicKey()
        val cipher: Cipher = Cipher.getInstance(CRYPTO_TRANSFORM)

        cipher.init(Cipher.ENCRYPT_MODE, pubKey)
        encryptedBytes = cipher.doFinal(message.toByteArray(Charsets.UTF_8))

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    // Decrypt a message
    fun decrypt(message: String, key: String = pKey): String {
        val decryptedBytes: ByteArray
        val pubKey: PublicKey? = key.toPublicKey()
        val cipher = Cipher.getInstance(CRYPTO_TRANSFORM)

        cipher.init(Cipher.DECRYPT_MODE, pubKey)
        decryptedBytes = cipher.doFinal(message.toByteArray(Charsets.UTF_8))

        return String(decryptedBytes)
    }
}