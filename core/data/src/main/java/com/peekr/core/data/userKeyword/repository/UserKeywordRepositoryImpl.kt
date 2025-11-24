package com.peekr.core.data.userKeyword.repository

import com.peekr.core.common.coroutine.IO
import com.peekr.core.data.network.error.toCommonErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.userKeyword.network.UserKeywordDataSource
import com.peekr.core.data.userKeyword.network.request.toDataModel
import com.peekr.core.data.userKeyword.network.response.toDomainModel
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.error.UserKeywordErrorType
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.userKeyword.model.PatchOffset
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywords
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class UserKeywordRepositoryImpl @Inject constructor(
    private val userKeywordDataSource: UserKeywordDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : UserKeywordRepository {
    override fun getUserKeywords(): Flow<Result<UserKeywords, UserKeywordErrorType>> =
        safeResultFlow<UserKeywords, UserKeywordErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { UserKeywordErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordDataSource.getUserKeywords()) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = UserKeywordErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getDescription(
        userKeywordId: UserKeywordId,
    ): Flow<Result<KeywordDescription, UserKeywordErrorType>> =
        safeResultFlow<KeywordDescription, UserKeywordErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { UserKeywordErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordDataSource.getDescription(userKeywordId)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = UserKeywordErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun createUserKeyword(create: CreateUserKeyword): Flow<Result<UserKeyword, UserKeywordErrorType>> =
        safeResultFlow<UserKeyword, UserKeywordErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { UserKeywordErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordDataSource.createUserKeyword(create.toDataModel())) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = UserKeywordErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun patchOffset(
        userKeywordId: UserKeywordId,
        patchOffset: PatchOffset,
    ): Flow<Result<PatchOffset, UserKeywordErrorType>> =
        safeResultFlow<PatchOffset, UserKeywordErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { UserKeywordErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (
                val result = userKeywordDataSource.patchOffset(
                    userKeywordId,
                    patchOffset.toDataModel(),
                )
            ) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = UserKeywordErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun patchDescription(
        userKeywordId: UserKeywordId,
        patchDescription: PatchDescription,
    ): Flow<Result<PatchDescription, UserKeywordErrorType>> =
        safeResultFlow<PatchDescription, UserKeywordErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { UserKeywordErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (
                val result = userKeywordDataSource.patchDescription(
                    userKeywordId,
                    patchDescription.toDataModel(),
                )
            ) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = UserKeywordErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun deleteUserKeyword(
        userKeywordId: UserKeywordId,
    ): Flow<Result<Unit, UserKeywordErrorType>> =
        safeResultFlow<Unit, UserKeywordErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { UserKeywordErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordDataSource.deleteUserKeyword(userKeywordId)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    val error = UserKeywordErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }
}
