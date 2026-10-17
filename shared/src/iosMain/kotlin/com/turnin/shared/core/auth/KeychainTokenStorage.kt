package com.turnin.shared.core.auth

import com.turnin.shared.core.network.auth.TokenStorage
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDataCreate
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

/**
 * Keychain 기반 토큰 저장소
 *
 * - 토큰은 OS가 암호화하는 Keychain(GenericPassword)에 직접 저장한다.
 * - 접근 등급: `AfterFirstUnlockThisDeviceOnly` (재부팅 후 첫 잠금 해제 이후 백그라운드에서도 접근 가능, 백업·다른 기기로 이전되지 않음)
 * - Keychain 항목은 앱을 삭제해도 남으므로, 첫 실행 시 [clear]로 정리한다. (`SharedSdk` 참고)
 * - 조회·삭제 실패는 예외 없이 처리하고(조회: null), 저장 실패만 예외로 알린다.
 *   (Keychain은 앱 프로세스에서만 사용 가능하므로 Kotlin/Native 단위 테스트에서는 errSecNotAvailable이 발생한다.)
 */
@OptIn(ExperimentalForeignApi::class, ExperimentalUnsignedTypes::class)
internal class KeychainTokenStorage(
    private val service: String = DEFAULT_SERVICE,
) : TokenStorage {
    override suspend fun getAccessToken(): String? = read(ACCOUNT_ACCESS_TOKEN)

    override suspend fun getRefreshToken(): String? = read(ACCOUNT_REFRESH_TOKEN)

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        write(ACCOUNT_ACCESS_TOKEN, accessToken)
        write(ACCOUNT_REFRESH_TOKEN, refreshToken)
    }

    override suspend fun clear() {
        delete(ACCOUNT_ACCESS_TOKEN)
        delete(ACCOUNT_REFRESH_TOKEN)
    }

    private fun read(account: String): String? = memScoped {
        val query = query(account) {
            CFDictionaryAddValue(it, kSecReturnData, kCFBooleanTrue)
            CFDictionaryAddValue(it, kSecMatchLimit, kSecMatchLimitOne)
        }
        val result = alloc<CFTypeRefVar>()
        val status = SecItemCopyMatching(query, result.ptr)
        CFRelease(query)
        if (status != errSecSuccess) return null

        val data: CFDataRef = result.value?.reinterpret() ?: return null
        val bytes = CFDataGetBytePtr(data)?.readBytes(CFDataGetLength(data).toInt())
        CFRelease(data)
        bytes?.decodeToString()
    }

    private fun write(account: String, value: String) {
        delete(account)
        val bytes = value.encodeToByteArray().asUByteArray()
        val data = bytes.usePinned { pinned ->
            CFDataCreate(kCFAllocatorDefault, pinned.addressOf(0), bytes.size.convert())
        }
        val query = query(account) {
            CFDictionaryAddValue(it, kSecValueData, data)
            CFDictionaryAddValue(it, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly)
        }
        val status = SecItemAdd(query, null)
        CFRelease(query)
        data?.let { CFRelease(it) }
        check(status == errSecSuccess) { "Keychain 저장 실패 (status: $status)" }
    }

    private fun delete(account: String) {
        val query = query(account)
        SecItemDelete(query)
        CFRelease(query)
    }

    /** service·account로 식별하는 GenericPassword 쿼리 (호출 측에서 CFRelease 필요) */
    private fun query(
        account: String,
        extra: (CFMutableDictionaryRef?) -> Unit = {},
    ): CFMutableDictionaryRef? {
        val dictionary = CFDictionaryCreateMutable(
            kCFAllocatorDefault,
            0,
            kCFTypeDictionaryKeyCallBacks.ptr,
            kCFTypeDictionaryValueCallBacks.ptr,
        )
        val serviceRef = cfString(service)
        val accountRef = cfString(account)
        CFDictionaryAddValue(dictionary, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(dictionary, kSecAttrService, serviceRef)
        CFDictionaryAddValue(dictionary, kSecAttrAccount, accountRef)
        // 딕셔너리가 값을 retain하므로 생성한 참조는 바로 해제한다.
        serviceRef?.let { CFRelease(it) }
        accountRef?.let { CFRelease(it) }
        extra(dictionary)
        return dictionary
    }

    private fun cfString(value: String): CFStringRef? =
        CFStringCreateWithCString(kCFAllocatorDefault, value, kCFStringEncodingUTF8)

    companion object {
        const val DEFAULT_SERVICE = "com.turnin.app.auth"
        private const val ACCOUNT_ACCESS_TOKEN = "accessToken"
        private const val ACCOUNT_REFRESH_TOKEN = "refreshToken"
    }
}
