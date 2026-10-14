package com.turnin.core.data.repository

import com.turnin.core.common.coroutine.IO
import com.turnin.core.data.HttpStatusCode
import com.turnin.core.data.source.network.datasource.KeywordNetworkDataSource
import com.turnin.core.data.source.network.dto.keyword.request.CreateKeywordRequest
import com.turnin.core.data.source.network.dto.keyword.response.toDomainModel
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.safeResultFlow
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.keyword.model.Keyword
import com.turnin.core.domain.keyword.repository.KeywordRepository
import com.turnin.core.domain.model.KeywordId
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class KeywordRepositoryImpl @Inject constructor(
    private val keywordNetworkDataSource: KeywordNetworkDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : KeywordRepository {
    override fun getKeywordById(keywordId: KeywordId): Flow<Result<Keyword?, CommonErrorType>> =
        safeResultFlow<Keyword?, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            when (val result = keywordNetworkDataSource.getKeywordById(keywordId)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    if (result.error is NetworkErrorType.Network.HttpError &&
                        result.error.status == HttpStatusCode.NotFound.code
                    ) {
                        emit(Result.Success(null))
                    } else {
                        val error = result.error.toCommonErrorType()
                        emit(Result.Error(error = error, message = result.message))
                    }
                }
            }
        }

    override fun getKeywordByName(keywordName: String): Flow<Result<Keyword?, CommonErrorType>> =
        safeResultFlow<Keyword?, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            when (val result = keywordNetworkDataSource.getKeywordByName(keywordName)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    if (result.error is NetworkErrorType.Network.HttpError &&
                        result.error.status == HttpStatusCode.NotFound.code
                    ) {
                        emit(Result.Success(null))
                    } else {
                        val error = result.error.toCommonErrorType()
                        emit(Result.Error(error = error, message = result.message))
                    }
                }
            }
        }

    override fun createKeyword(keyword: String): Flow<Result<Keyword, CommonErrorType>> =
        safeResultFlow<Keyword, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            val createKeywordRequest = CreateKeywordRequest(keyword)
            when (val result = keywordNetworkDataSource.createKeyword(createKeywordRequest)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }
}
