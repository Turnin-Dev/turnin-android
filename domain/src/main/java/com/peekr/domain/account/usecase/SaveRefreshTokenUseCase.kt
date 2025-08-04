package com.peekr.domain.account.usecase

import com.peekr.domain.shared.dataStore.DataStoreKey
import com.peekr.domain.shared.dataStore.DataStoreManager
import com.peekr.domain.shared.dataStore.WritingDataException
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Refresh Token을 DataStore에 저장한다.
 */
class SaveRefreshTokenUseCase @Inject constructor(
    private val dataStoreManager: DataStoreManager,
) {
    /** @param token Refresh Token */
    operator fun invoke(token: String): Flow<Result<Boolean, ErrorType>> = flow {
        try {
            dataStoreManager.saveEncryptedStringData(
                key = DataStoreKey.Auth.RefreshToken,
                value = token,
            )
            emit(Result.Success(true))
        } catch (e: WritingDataException) {
            emit(Result.Error(error = ErrorType.Auth.SaveTokenFailed, message = e.message))
        } catch (e: Exception) {
            emit(Result.Error(error = ErrorType.Unexpected(e)))
        }
    }
}
