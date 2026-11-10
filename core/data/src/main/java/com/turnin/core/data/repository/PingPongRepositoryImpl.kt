package com.turnin.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.turnin.core.common.coroutine.IO
import com.turnin.core.data.paging.TurninCursorPagingSource
import com.turnin.core.data.source.network.datasource.PingPongNetworkDataSource
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongDetailResponse
import com.turnin.core.data.source.network.dto.pingPong.response.toDomainModel
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.safeResultFlow
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.PingPongId
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.pingPong.model.PingPong
import com.turnin.core.domain.pingPong.model.PingPongAnswer
import com.turnin.core.domain.pingPong.model.PingPongAnswerContent
import com.turnin.core.domain.pingPong.model.PingPongDetail
import com.turnin.core.domain.pingPong.model.PingPongPagingTokens
import com.turnin.core.domain.pingPong.model.PingPongQuestionContent
import com.turnin.core.domain.pingPong.repository.PingPongRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PingPongRepositoryImpl @Inject constructor(
    private val pingPongNetworkDataSource: PingPongNetworkDataSource,
    @param:IO private val ioDispatcher: CoroutineDispatcher,
) : PingPongRepository {
    override fun getPingPongs(userKeywordId: UserKeywordId): Flow<PagingData<PingPongDetail>> {
        val pageSize = PingPongPagingTokens.PAGE_SIZE
        val prefetchDistance = PingPongPagingTokens.PREFETCH_DISTANCE

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance,
                initialLoadSize = pageSize + prefetchDistance,
            ),
            pagingSourceFactory = {
                TurninCursorPagingSource<Long, PingPongDetailResponse>(
                    apiCall = { nextCursor ->
                        pingPongNetworkDataSource.getPingPongs(
                            userKeywordId = userKeywordId.value,
                            cursor = nextCursor,
                            size = pageSize,
                        )
                    },
                )
            },
        )
            .flow
            .map { pagingData ->
                pagingData.map(PingPongDetailResponse::toDomainModel)
            }
    }

    override fun createPingPong(
        userKeywordId: UserKeywordId,
        question: PingPongQuestionContent,
    ): Flow<Result<PingPong, CommonErrorType>> =
        safeResultFlow<PingPong, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            val result = pingPongNetworkDataSource.createPingPong(
                userKeywordId = userKeywordId.value,
                question = question.value,
            )
            when (result) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun createAnswer(
        pingPongId: PingPongId,
        answer: PingPongAnswerContent,
    ): Flow<Result<PingPongAnswer, CommonErrorType>> =
        safeResultFlow<PingPongAnswer, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            val result = pingPongNetworkDataSource.createAnswer(
                pingPongId = pingPongId.value,
                answer = answer.value,
            )
            when (result) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun deletePingPong(pingPongId: PingPongId): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = pingPongNetworkDataSource.deletePingPong(pingPongId.value)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun deletePingPongAnswer(pingPongId: PingPongId): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = pingPongNetworkDataSource.deletePingPongAnswer(pingPongId.value)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }
}
