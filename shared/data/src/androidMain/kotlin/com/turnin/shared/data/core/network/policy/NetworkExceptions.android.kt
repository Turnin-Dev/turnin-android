package com.turnin.shared.data.core.network.policy

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

actual fun Throwable.isPlatformTimeoutException(): Boolean =
    this is SocketTimeoutException || this is TimeoutException

actual fun Throwable.isUnknownHostException(): Boolean = this is UnknownHostException

actual fun Throwable.isConnectionFailure(): Boolean = this is ConnectException
