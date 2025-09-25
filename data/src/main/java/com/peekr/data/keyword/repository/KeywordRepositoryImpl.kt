package com.peekr.data.keyword.repository

import com.peekr.core.common.IO
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.toErrorType
import com.peekr.core.domain.coroutine.safeResultFlow
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.data.keyword.model.request.CreateKeywordRequest
import com.peekr.data.keyword.model.response.toDomainModel
import com.peekr.data.keyword.network.KeywordDataSource
import com.peekr.domain.keyword.model.Keyword
import com.peekr.domain.keyword.repository.KeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class KeywordRepositoryImpl @Inject constructor(
    private val keywordDataSource: KeywordDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : KeywordRepository {
    override fun getKeyword(keywordId: KeywordId): Flow<Result<Keyword, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)

            when (val result = keywordDataSource.getKeyword(keywordId)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toErrorType(), message = result.message))
                }
            }
        }

    override fun createKeyword(keyword: String): Flow<Result<Keyword, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)

            val createKeywordRequest = CreateKeywordRequest(keyword)
            when (val result = keywordDataSource.createKeyword(createKeywordRequest)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toErrorType(), message = result.message))
                }
            }
        }
}
