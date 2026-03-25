package com.peekr.core.data.repository

import com.peekr.core.common.coroutine.IO
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.cleaner.AppDataCleaner
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.local.error.WritingDataException
import com.peekr.core.data.source.network.datasource.AccountNetworkDataSource
import com.peekr.core.data.source.network.datasource.AuthNetworkDataSource
import com.peekr.core.data.source.network.datasource.UserNetworkDataSource
import com.peekr.core.data.source.network.dto.auth.request.toDataModel
import com.peekr.core.data.source.network.dto.auth.response.ExistsResponse
import com.peekr.core.data.source.network.dto.auth.response.toDomainModel
import com.peekr.core.data.source.network.dto.user.request.FcmTokenRequest
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.auth.model.ExistsUser
import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.auth.model.LoginResult
import com.peekr.core.domain.auth.model.Register
import com.peekr.core.domain.auth.model.RegisterResult
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.SocialLoginProvider
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AuthRepositoryImpl @Inject constructor(
    private val authNetworkDataSource: AuthNetworkDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val userNetworkDataSource: UserNetworkDataSource,
    private val dataStoreManager: DataStoreManager,
    private val appDataCleaner: AppDataCleaner,
    @param:IO private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {
    private val tag = this::class.java.simpleName

    override suspend fun isLoggedIn(): Boolean {
        dataStoreManager.getLongData(DataStoreKey.User.UserId).firstOrNull()
            ?: return false

        val accessToken =
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken).firstOrNull()
        val refreshToken =
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken).firstOrNull()

        return accessToken != null && refreshToken != null
    }

    override fun login(loginCredentials: LoginCredentials): Flow<Result<LoginResult, CommonErrorType>> =
        safeResultFlow<LoginResult, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = authNetworkDataSource.login(loginCredentials.toDataModel())) {
                is NetworkResult.Success -> {
                    // 성공 시 사용자 ID와 로그인 플랫폼을 저장한다.
                    dataStoreManager.saveLongData(
                        key = DataStoreKey.User.UserId,
                        value = result.data.userId,
                    )
                    dataStoreManager.saveStringData(
                        key = DataStoreKey.User.LoginProvider,
                        value = loginCredentials.provider.name,
                    )
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(
                        Result.Error(
                            error = result.error.toCommonErrorType(),
                            message = result.message,
                        ),
                    )
                }
            }
        }

    override fun existsUser(existsUser: ExistsUser): Flow<Result<Boolean, CommonErrorType>> =
        safeResultFlow<Boolean, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            emit(mapExistsResult(authNetworkDataSource.existsUser(existsUser.toDataModel())))
        }

    override fun existsDisplayId(displayId: DisplayId): Flow<Result<Boolean, CommonErrorType>> =
        safeResultFlow<Boolean, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            emit(mapExistsResult(authNetworkDataSource.existsDisplayId(displayId)))
        }

    override fun register(register: Register): Flow<Result<RegisterResult, CommonErrorType>> =
        safeResultFlow<RegisterResult, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = authNetworkDataSource.register(register.toDataModel())) {
                is NetworkResult.Success -> {
                    // 성공 시 사용자 ID와 로그인 플랫폼을 저장한다.
                    dataStoreManager.saveLongData(
                        key = DataStoreKey.User.UserId,
                        value = result.data.userId,
                    )
                    dataStoreManager.saveStringData(
                        key = DataStoreKey.User.LoginProvider,
                        value = register.provider.name,
                    )
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(
                        Result.Error(
                            error = result.error.toCommonErrorType(),
                            message = result.message,
                        ),
                    )
                }
            }
        }

    override fun saveTokens(accessToken: String, refreshToken: String): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            try {
                dataStoreManager.saveEncryptedStringData(
                    key = DataStoreKey.Auth.AccessToken,
                    value = accessToken,
                )
                dataStoreManager.saveEncryptedStringData(
                    key = DataStoreKey.Auth.RefreshToken,
                    value = refreshToken,
                )
                emit(Result.Success(Unit))
            } catch (_: WritingDataException) {
                emit(Result.Error(CommonErrorType.Local.WritingDataFailed))
            }
        }

    override suspend fun getLoginType(): SocialLoginProvider? {
        val provider = dataStoreManager.getStringData(DataStoreKey.User.LoginProvider).firstOrNull()
            ?: return null
        return SocialLoginProvider.getType(provider)
    }

    override fun logout(token: String?): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            // 1. 로그아웃 API 호출
            val fcmToken = FcmTokenRequest(token)
            val shouldClear = when (val result = userNetworkDataSource.logout(fcmToken)) {
                is NetworkResult.Success -> true

                is NetworkResult.Error -> {
                    // 401 발생 시 이미 토큰이 만료된 상태이므로 로그아웃 계속 진행
                    if (result.error.toCommonErrorType() == CommonErrorType.Network.Unauthorized) {
                        true
                    } else {
                        val error = result.error.toCommonErrorType()
                        emit(Result.Error(error = error, message = result.message))
                        false
                    }
                }
            }

            // 2. 로컬 데이터 삭제
            if (shouldClear) {
                runCatching { appDataCleaner.clearAll() }
                    .onFailure { AppLogger.e(tag, it, "Failed to clear app data.") }
                emit(Result.Success(Unit))
            }
        }

    override fun deleteAccount(): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = accountNetworkDataSource.deleteAccount()) {
                is NetworkResult.Success -> {
                    runCatching { appDataCleaner.clearAll() }
                        .onFailure { AppLogger.e(tag, it, "Failed to clear app data.") }
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    private fun mapExistsResult(result: NetworkResult<ExistsResponse>): Result<Boolean, CommonErrorType> =
        when (result) {
            is NetworkResult.Success -> Result.Success(result.data.exists)
            is NetworkResult.Error -> Result.Error(
                error = result.error.toCommonErrorType(),
                message = result.message,
            )
        }
}
