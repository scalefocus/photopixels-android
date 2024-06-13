package io.photopixels.data.storage.datastore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Cipher Util class used to encrypt/decrypt data by managing the secret key in Android Keystore
 */
class CipherUtil {

    companion object {
        private val KEY_STORE = KeyStore.getInstance("AndroidKeyStore")
        private const val KEY_ALIAS = "auth_key"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val AES_MODE = "AES/GCM/NoPadding"
        private const val GCM_SIZE = 128
    }

    init {
        KEY_STORE.load(null)
        createKeyIfNotExists()
    }

    private val cipher by lazy { Cipher.getInstance(AES_MODE) }

    fun encrypt(data: String): String {
        val secretKey: SecretKey = getSecretKey()

        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(data.toByteArray())

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT) + ":" +
            Base64.encodeToString(iv, Base64.DEFAULT)
    }

    fun decrypt(encryptedText: String): String {
        val parts = encryptedText.split(":")
        val encryptedData = Base64.decode(parts[0], Base64.DEFAULT)
        val iv = Base64.decode(parts[1], Base64.DEFAULT)

        val secretKey: SecretKey = getSecretKey()
        val gcmParameterSpec = GCMParameterSpec(GCM_SIZE, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec)

        val decryptedBytes = cipher.doFinal(encryptedData)

        return String(decryptedBytes)
    }

    private fun getSecretKey(): SecretKey {
        val keyEntry = KEY_STORE.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return keyEntry.secretKey
    }

    private fun createKeyIfNotExists() {
        if (!KEY_STORE.containsAlias(KEY_ALIAS)) {
            KeyGenerator.getInstance(ALGORITHM).apply {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(PADDING)
                        .setUserAuthenticationRequired(false)
                        .setRandomizedEncryptionRequired(true)
                        .build()
                )
            }.generateKey()
        }
    }
}
