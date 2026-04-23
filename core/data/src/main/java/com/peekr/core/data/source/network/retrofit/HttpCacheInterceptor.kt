package com.peekr.core.data.source.network.retrofit

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Cacheable(val maxAge: Int)

/** HTTP 캐시 기간 */
object HttpCacheDuration {
    const val ONE_DAY = 60 * 60 * 24
    const val ONE_WEEK = ONE_DAY * 7
}

/** HTTP 캐시 인터셉터 */
class HttpCacheInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val tag = request.tag(Invocation::class.java)
        val annotation = tag?.method()?.getAnnotation(Cacheable::class.java)

        return if (annotation != null) {
            response.newBuilder()
                .header("Cache-Control", "public, max-age=${annotation.maxAge}")
                .removeHeader("Pragma")
                .build()
        } else {
            response
        }
    }
}
