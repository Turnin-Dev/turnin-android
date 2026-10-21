package com.turnin.shared.data.core.network.policy

import io.ktor.client.engine.darwin.DarwinHttpRequestException
import platform.Foundation.NSURLErrorCannotConnectToHost
import platform.Foundation.NSURLErrorCannotFindHost
import platform.Foundation.NSURLErrorDNSLookupFailed
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorTimedOut

// Darwin 엔진은 NSURLSession 에러를 DarwinHttpRequestException(origin: NSError)으로 전달한다.
// Android(java.net 예외)와 같은 분류가 되도록 NSURLErrorDomain 코드로 판정한다.

actual fun Throwable.isPlatformTimeoutException(): Boolean = nsUrlErrorCode() == NSURLErrorTimedOut

actual fun Throwable.isUnknownHostException(): Boolean =
    nsUrlErrorCode().let { it == NSURLErrorCannotFindHost || it == NSURLErrorDNSLookupFailed }

actual fun Throwable.isConnectionFailure(): Boolean = nsUrlErrorCode() == NSURLErrorCannotConnectToHost

private fun Throwable.nsUrlErrorCode(): Long? =
    (this as? DarwinHttpRequestException)?.origin?.takeIf { it.domain == NSURLErrorDomain }?.code
