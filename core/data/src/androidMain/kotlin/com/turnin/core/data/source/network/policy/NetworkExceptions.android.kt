package com.turnin.core.data.source.network.policy

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

internal actual fun Throwable.isPlatformTimeoutException(): Boolean =
    this is SocketTimeoutException || this is TimeoutException

internal actual fun Throwable.isUnknownHostException(): Boolean = this is UnknownHostException

internal actual fun Throwable.isConnectionFailure(): Boolean = this is ConnectException
