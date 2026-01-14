package com.peekr.core.data.repository

import com.peekr.core.common.coroutine.IO
import com.peekr.core.data.source.network.datasource.UserKeywordNetworkDataSource
import com.peekr.core.data.source.network.dto.userKeyword.request.toDataModel
import com.peekr.core.data.source.network.dto.userKeyword.response.toDomainModel
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail
import com.peekr.core.domain.userKeyword.model.UserKeywords
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class UserKeywordRepositoryImpl @Inject constructor(
    private val userKeywordNetworkDataSource: UserKeywordNetworkDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : UserKeywordRepository {
    override fun getUserKeywords(userId: UserId): Flow<Result<UserKeywords, CommonErrorType>> =
        safeResultFlow<UserKeywords, CommonErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordNetworkDataSource.getUserKeywords(userId)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getDetail(
        userKeywordId: UserKeywordId,
        withUserInfo: Boolean,
    ): Flow<Result<UserKeywordDetail, CommonErrorType>> =
        safeResultFlow<UserKeywordDetail, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            when (val result = userKeywordNetworkDataSource.getDetail(userKeywordId, withUserInfo)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getDescription(
        userKeywordId: UserKeywordId,
    ): Flow<Result<KeywordDescription, CommonErrorType>> =
        safeResultFlow<KeywordDescription, CommonErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordNetworkDataSource.getDescription(userKeywordId)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun createUserKeyword(create: CreateUserKeyword): Flow<Result<UserKeyword, CommonErrorType>> =
        safeResultFlow<UserKeyword, CommonErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordNetworkDataSource.createUserKeyword(create.toDataModel())) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun patchDescription(
        userKeywordId: UserKeywordId,
        patchDescription: PatchDescription,
    ): Flow<Result<PatchDescription, CommonErrorType>> =
        safeResultFlow<PatchDescription, CommonErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (
                val result = userKeywordNetworkDataSource.patchDescription(
                    userKeywordId,
                    patchDescription.toDataModel(),
                )
            ) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun deleteUserKeyword(
        userKeywordId: UserKeywordId,
    ): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordNetworkDataSource.deleteUserKeyword(userKeywordId)) {
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
