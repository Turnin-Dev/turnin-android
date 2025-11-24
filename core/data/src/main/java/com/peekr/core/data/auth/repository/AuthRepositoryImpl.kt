package com.peekr.core.data.auth.repository

import com.peekr.core.common.coroutine.IO
import com.peekr.core.data.auth.network.AuthDataSource
import com.peekr.core.data.auth.network.request.toDataModel
import com.peekr.core.data.auth.network.response.ExistsResponse
import com.peekr.core.data.auth.network.response.toDomainModel
import com.peekr.core.data.datastore.DataStoreKey
import com.peekr.core.data.datastore.DataStoreManager
import com.peekr.core.data.network.error.toCommonErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.domain.auth.error.AuthErrorType
import com.peekr.core.domain.auth.model.ExistsUser
import com.peekr.core.domain.auth.model.Login
import com.peekr.core.domain.auth.model.LoginResult
import com.peekr.core.domain.auth.model.Register
import com.peekr.core.domain.auth.model.RegisterResult
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.common.toErrorCode
import com.peekr.core.domain.model.DisplayId
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val dataStoreManager: DataStoreManager,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {
    override fun login(login: Login): Flow<Result<LoginResult, AuthErrorType>> =
        safeResultFlow<LoginResult, AuthErrorType>(ioDispatcher, { AuthErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = authDataSource.login(login.toDataModel())) {
                is NetworkResult.Success -> {
                    dataStoreManager.saveLongData(
                        key = DataStoreKey.User.UserId,
                        value = result.data.userId,
                    )
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = AuthErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun existsUser(existsUser: ExistsUser): Flow<Result<Boolean, AuthErrorType>> =
        safeResultFlow<Boolean, AuthErrorType>(ioDispatcher, { AuthErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            emit(mapExistsResult(authDataSource.existsUser(existsUser.toDataModel())))
        }

    override fun existsDisplayId(displayId: DisplayId): Flow<Result<Boolean, AuthErrorType>> =
        safeResultFlow<Boolean, AuthErrorType>(ioDispatcher, { AuthErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            emit(mapExistsResult(authDataSource.existsDisplayId(displayId)))
        }

    override fun register(register: Register): Flow<Result<RegisterResult, AuthErrorType>> =
        safeResultFlow<RegisterResult, AuthErrorType>(ioDispatcher, { AuthErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = authDataSource.register(register.toDataModel())) {
                is NetworkResult.Success -> {
                    dataStoreManager.saveLongData(
                        key = DataStoreKey.User.UserId,
                        value = result.data.userId,
                    )
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = AuthErrorType.CommonError(result.error.toCommonErrorType())
                    emit(
                        Result.Error(
                            error = error,
                            message = result.message,
                            code = result.code?.toErrorCode(),
                        ),
                    )
                }
            }
        }

    override fun saveRefreshToken(token: String): Flow<Result<Unit, AuthErrorType>> =
        safeResultFlow<Unit, AuthErrorType>(ioDispatcher, { AuthErrorType.Unexpected(it) }) {
            dataStoreManager.saveEncryptedStringData(
                key = DataStoreKey.Auth.RefreshToken,
                value = token,
            )
        }

    private fun mapExistsResult(result: NetworkResult<ExistsResponse>): Result<Boolean, AuthErrorType> =
        when (result) {
            is NetworkResult.Success -> Result.Success(result.data.exists)
            is NetworkResult.Error -> Result.Error(
                error = AuthErrorType.CommonError(result.error.toCommonErrorType()),
                message = result.message,
            )
        }
}
