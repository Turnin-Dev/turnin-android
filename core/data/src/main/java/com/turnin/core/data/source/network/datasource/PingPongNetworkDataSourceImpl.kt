package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.api.PingPongApi
import com.turnin.core.data.source.network.dto.pingPong.request.CreatePingPongAnswerRequest
import com.turnin.core.data.source.network.dto.pingPong.request.CreatePingPongRequest
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongAnswerResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongCursorPageResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.data.source.network.util.networkCall
import com.turnin.core.data.source.network.util.networkCallWithoutResponse
import javax.inject.Inject

class PingPongNetworkDataSourceImpl @Inject constructor(
    private val pingPongApi: PingPongApi,
) : PingPongNetworkDataSource {
    override suspend fun getPingPongs(
        userKeywordId: Long,
        cursor: Long?,
        size: Int,
    ): NetworkResult<PingPongCursorPageResponse> =
        networkCall { pingPongApi.getPingPongs(userKeywordId, cursor, size) }

    override suspend fun createPingPong(
        userKeywordId: Long,
        question: String,
    ): NetworkResult<PingPongResponse> =
        networkCall {
            pingPongApi.createPingPong(userKeywordId, CreatePingPongRequest(question))
        }

    override suspend fun createAnswer(
        pingPongId: Long,
        answer: String,
    ): NetworkResult<PingPongAnswerResponse> =
        networkCall {
            pingPongApi.createAnswer(pingPongId, CreatePingPongAnswerRequest(answer))
        }

    override suspend fun deletePingPong(pingPongId: Long): NetworkResult<Unit> =
        networkCallWithoutResponse { pingPongApi.deletePingPong(pingPongId) }

    override suspend fun deletePingPongAnswer(pingPongId: Long): NetworkResult<Unit> =
        networkCallWithoutResponse { pingPongApi.deletePingPongAnswer(pingPongId) }
}
