package com.peekr.core.data.keyword.repository

import com.peekr.core.common.IO
import com.peekr.core.data.keyword.network.KeywordDataSource
import com.peekr.core.data.keyword.network.request.CreateKeywordRequest
import com.peekr.core.data.keyword.network.response.toDomainModel
import com.peekr.core.data.network.error.NetworkErrorType
import com.peekr.core.data.network.error.toCommonErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.domain.coroutine.safeResultFlow
import com.peekr.core.domain.keyword.error.KeywordErrorType
import com.peekr.core.domain.keyword.model.Keyword
import com.peekr.core.domain.keyword.repository.KeywordRepository
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.util.Result
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class KeywordRepositoryImpl @Inject constructor(
    private val keywordDataSource: KeywordDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : KeywordRepository {
    override fun getKeywordById(keywordId: KeywordId): Flow<Result<Keyword?, KeywordErrorType>> =
        safeResultFlow<Keyword?, KeywordErrorType>(ioDispatcher, { KeywordErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            when (val result = keywordDataSource.getKeywordById(keywordId)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    if (result.error == NetworkErrorType.Network.NotFound) {
                        emit(Result.Success(null))
                    } else {
                        val error = KeywordErrorType.CommonError(result.error.toCommonErrorType())
                        emit(Result.Error(error = error, message = result.message))
                    }
                }
            }
        }

    override fun getKeywordByName(keywordName: String): Flow<Result<Keyword?, KeywordErrorType>> =
        safeResultFlow<Keyword?, KeywordErrorType>(ioDispatcher, { KeywordErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            when (val result = keywordDataSource.getKeywordByName(keywordName)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    if (result.error == NetworkErrorType.Network.NotFound) {
                        emit(Result.Success(null))
                    } else {
                        val error = KeywordErrorType.CommonError(result.error.toCommonErrorType())
                        emit(Result.Error(error = error, message = result.message))
                    }
                }
            }
        }

    override fun createKeyword(keyword: String): Flow<Result<Keyword, KeywordErrorType>> =
        safeResultFlow<Keyword, KeywordErrorType>(ioDispatcher, { KeywordErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            val createKeywordRequest = CreateKeywordRequest(keyword)
            when (val result = keywordDataSource.createKeyword(createKeywordRequest)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = KeywordErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }
}
