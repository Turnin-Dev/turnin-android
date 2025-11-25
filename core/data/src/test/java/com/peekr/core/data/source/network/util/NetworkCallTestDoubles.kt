package com.peekr.core.data.source.network.util

import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.GET

@JsonClass(generateAdapter = true)
internal data class TestModel(
    val message: String,
)

internal interface ApiService {
    @GET("/test")
    suspend fun testCall(): Response<TestModel>
}
