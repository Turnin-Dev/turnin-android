package com.peekr.data.common.util.crypto

import java.io.InputStream
import java.io.OutputStream
import java.security.Key
import java.security.KeyStore
import java.security.KeyStoreSpi
import java.security.Provider
import java.security.SecureRandom
import java.security.Security
import java.security.cert.Certificate
import java.security.spec.AlgorithmParameterSpec
import java.util.Date
import java.util.Enumeration
import javax.crypto.KeyGenerator
import javax.crypto.KeyGeneratorSpi
import javax.crypto.SecretKey

object FakeAndroidKeyStore {
    // 전역 키 캐시
    private val keyCache = mutableMapOf<String, SecretKey>()

    val setup by lazy {
        Security.addProvider(
            object : Provider("AndroidKeyStore", 1.0, "") {
                init {
                    put("KeyStore.AndroidKeyStore", FakeKeyStore::class.java.name)
                    put("KeyGenerator.AES", FakeAesKeyGenerator::class.java.name)
                }
            },
        )
    }

    internal fun storeKey(alias: String, key: SecretKey) {
        keyCache[alias] = key
    }

    internal fun getKey(alias: String): SecretKey? = keyCache[alias]

    @Suppress("unused")
    class FakeKeyStore : KeyStoreSpi() {
        private val wrapped = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null)
        }

        override fun engineIsKeyEntry(alias: String?): Boolean =
            getKey(alias ?: "") != null || wrapped.isKeyEntry(alias)

        override fun engineIsCertificateEntry(alias: String?): Boolean =
            wrapped.isCertificateEntry(alias)

        override fun engineGetCertificate(alias: String?): Certificate =
            wrapped.getCertificate(alias)

        override fun engineGetCreationDate(alias: String?): Date = wrapped.getCreationDate(alias)

        override fun engineDeleteEntry(alias: String?) {
            wrapped.deleteEntry(alias)
        }

        override fun engineSetKeyEntry(alias: String?, key: Key?, password: CharArray?, chain: Array<out Certificate>?) {
            if (key is SecretKey && alias != null) {
                storeKey(alias, key)
            }
            wrapped.setKeyEntry(alias, key, password, chain)
        }

        override fun engineSetKeyEntry(alias: String?, key: ByteArray?, chain: Array<out Certificate>?) =
            wrapped.setKeyEntry(alias, key, chain)

        override fun engineStore(stream: OutputStream?, password: CharArray?) =
            wrapped.store(stream, password)

        override fun engineSize(): Int = wrapped.size()

        override fun engineAliases(): Enumeration<String> = wrapped.aliases()

        override fun engineContainsAlias(alias: String?): Boolean =
            getKey(alias ?: "") != null || wrapped.containsAlias(alias)

        override fun engineLoad(stream: InputStream?, password: CharArray?) =
            wrapped.load(stream, password)

        override fun engineGetCertificateChain(alias: String?): Array<Certificate> =
            wrapped.getCertificateChain(alias)

        override fun engineSetCertificateEntry(alias: String?, cert: Certificate?) =
            wrapped.setCertificateEntry(alias, cert)

        override fun engineGetCertificateAlias(cert: Certificate?): String =
            wrapped.getCertificateAlias(cert)

        override fun engineGetKey(alias: String?, password: CharArray?): Key? =
            getKey(alias ?: "") ?: wrapped.getKey(alias, password)

        override fun engineGetEntry(alias: String?, protParam: KeyStore.ProtectionParameter?): KeyStore.Entry? {
            val key = getKey(alias ?: "")
            return key?.let { KeyStore.SecretKeyEntry(it) }
        }
    }

    @Suppress("unused")
    class FakeAesKeyGenerator : KeyGeneratorSpi() {
        private val wrapped = KeyGenerator.getInstance("AES")
        private var keyAlias: String? = null

        override fun engineInit(random: SecureRandom?) = Unit

        override fun engineInit(params: AlgorithmParameterSpec?, random: SecureRandom?) {
            // KeyGenParameterSpec에서 alias를 추출하려고 시도
            try {
                val aliasField = params?.javaClass?.getDeclaredField("mKeystoreAlias")
                aliasField?.isAccessible = true
                keyAlias = aliasField?.get(params) as? String
            } catch (e: Exception) {
                // reflection이 실패하면 기본값 사용
                keyAlias = "secret"
            }
        }

        override fun engineInit(keysize: Int, random: SecureRandom?) = Unit

        override fun engineGenerateKey(): SecretKey {
            val key = wrapped.generateKey()

            // 생성된 키를 전역 캐시에 저장
            keyAlias?.let { alias ->
                storeKey(alias, key)
            }

            return key
        }
    }
}
