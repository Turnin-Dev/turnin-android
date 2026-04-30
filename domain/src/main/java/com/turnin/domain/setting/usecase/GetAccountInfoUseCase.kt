package com.turnin.domain.setting.usecase

import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.domain.setting.error.SettingErrorType
import com.turnin.domain.setting.model.AccountInfo
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * 계정 정보 조회
 *
 * @see invoke
 */
class GetAccountInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    /**
     * 계정 정보를 조회한다.
     *
     * 로컬 데이터를 우선적으로 조회하고 없는 경우 네트워크 리프레쉬를 수행한다.
     */
    operator fun invoke(): Flow<Result<AccountInfo, SettingErrorType>> = flow {
        emit(Result.Loading)

        // 1. 로컬 데이터가 없는 경우 리프레쉬 트리거를 수행한다.
        if (userRepository.myProfile.value == null) {
            val refreshResult = userRepository.getMyProfileRefresh().first { it != Result.Loading }
            if (refreshResult is Result.Error) {
                emit(Result.Error(SettingErrorType.CommonError(refreshResult.error)))
                return@flow
            }
        }

        // 2. 로그인 타입과 로컬 데이터를 조합한다.
        val loginType = authRepository.getLoginType()
        val myProfile = userRepository.myProfile.value

        val result = when {
            loginType == null -> Result.Error(
                SettingErrorType.CommonError(CommonErrorType.SocialAuth.LoginProviderNotFound),
            )

            myProfile == null -> Result.Error(SettingErrorType.MyProfileNotFound)
            else -> Result.Success(
                AccountInfo(
                    userId = myProfile.userId,
                    displayId = myProfile.displayId,
                    name = myProfile.name,
                    profileImageUrl = myProfile.profileImageUrl,
                    introduce = myProfile.introduce,
                    loginProvider = loginType,
                ),
            )
        }

        emit(result)
    }
        .catch { emit(Result.Error(SettingErrorType.Unexpected(it))) }
}
