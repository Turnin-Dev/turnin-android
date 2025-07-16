package com.peekr.data.shared.util.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.GET

@JsonClass(generateAdapter = true)
internal data class TestModel(
    @Json(name = "message") val message: String,
)

internal interface ApiService {
    @GET("/test")
    suspend fun testCall(): Response<TestModel>
}
