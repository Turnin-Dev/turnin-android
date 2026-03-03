package com.peekr.domain.setting.usecase

import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.domain.setting.error.SettingErrorType
import com.peekr.domain.setting.model.AccountInfo
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

/**
 * 계정 정보 조회
 *
 * @see invoke
 */
@OptIn(ExperimentalCoroutinesApi::class)
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
        // 1. 로컬 데이터가 없는 경우 리프레쉬 트리거를 수행한다.
        if (userRepository.getMyProfile().first() == null) {
            userRepository.getMyProfileRefresh().first { it != Result.Loading }
        }

        // 2. 로그인 타입과 로컬 데이터를 combine으로 결합한 후 AccountInfo를 방출한다.
        emitAll(
            combine(
                flow { emit(authRepository.getLoginType()) },
                userRepository.getMyProfile(),
            ) { loginType, myProfile ->
                when {
                    loginType == null -> Result.Error(SettingErrorType.LoginProviderNotFound)
                    myProfile == null -> Result.Error(SettingErrorType.MyProfileNotFound)
                    else -> {
                        Result.Success(
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
                }
            },
        )
    }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(SettingErrorType.Unexpected(it))) }
}
