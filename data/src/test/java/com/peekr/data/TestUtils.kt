package com.peekr.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.mockk
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

/**
 * data 계층 단위 테스트에서 사용하는 테스트 유틸
 */
internal object TestUtils {
    private lateinit var server: MockWebServer
    private lateinit var moshi: Moshi
    private val mockTree = mockk<Timber.Tree>(relaxed = true)

    /** 테스트 유틸 초기화 */
    fun init() {
        // MockWebServer
        server = MockWebServer()
        server.start()

        // moshi
        moshi = Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        // Timer
        Timber.plant(mockTree)
    }

    inline fun <reified T> createNetworkApi(): T = Retrofit
        .Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(server.url("/"))
        .build()
        .create(T::class.java)

    fun getServer(): MockWebServer = server

    inline fun <reified T> decodeFromJson(json: String): T {
        val adapter = moshi.adapter(T::class.java)
        val decoded = adapter.fromJson(json)
        check(decoded != null) { "Decoded Failed" }
        return decoded
    }

    inline fun <reified T> encodeToJson(obj: T): String {
        val adapter = moshi.adapter(T::class.java)
        val encoded = adapter.toJson(obj)
        return encoded
    }

    fun cleanUp() {
        server.shutdown()
    }
}
