package com.turnin.core.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.mockk
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.ExternalResource
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

/**
 * 네트워크 관련 로직 테스트 룰
 *
 * [okhttp3.mockwebserver.MockWebServer], [com.squareup.moshi.Moshi], [timber.log.Timber] 가 포함되어 있다.
 */
class ServerTestRule : ExternalResource() {
    private var _server: MockWebServer? = null
    val server: MockWebServer
        get() = checkNotNull(_server) { "Server not initialized." }

    private val mockTree = mockk<Timber.Tree>(relaxed = true)

    val moshi: Moshi by lazy {
        Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    override fun before() {
        // Log Mock
        MockLog.mock()

        // MockWebServer
        _server?.shutdown()
        _server = MockWebServer().apply { start() }

        // Timber
        if (Timber.Forest.forest().none { it == mockTree }) {
            Timber.Forest.plant(mockTree)
        }
    }

    override fun after() {
        // MockWebServer
        _server?.shutdown()
        _server = null

        // Timber
        if (Timber.Forest.forest().contains(mockTree)) {
            Timber.Forest.uproot(mockTree)
        }

        // Log Mock
        MockLog.cleanUp()
    }

    /**
     * Retrofit API 를 생성한다.
     */
    inline fun <reified T> createNetworkApi(moshi: Moshi): T = Retrofit
        .Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(server.url("/"))
        .build()
        .create(T::class.java)

    /**
     * json에서 객체로 역직렬화
     */
    inline fun <reified T> decodeFromJson(json: String): T {
        val adapter = moshi.adapter(T::class.java)
        val decoded = adapter.fromJson(json)
        check(decoded != null) { "Decoded Failed" }
        return decoded
    }

    /**
     * 객체에서 json으로 직렬화
     */
    inline fun <reified T> encodeToJson(obj: T): String {
        val adapter = moshi.adapter(T::class.java)
        val encoded = adapter.toJson(obj)
        return encoded
    }
}
