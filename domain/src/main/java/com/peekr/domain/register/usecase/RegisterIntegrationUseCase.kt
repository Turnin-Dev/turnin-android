package com.peekr.domain.register.usecase

import com.peekr.core.domain.auth.model.RegisterResult
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.flatMapResult
import com.peekr.core.domain.common.mapError
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.domain.register.error.RegisterErrorType
import com.peekr.domain.register.model.ImageFileDetail
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * 회원가입을 진행한다.
 *
 * 회원가입에 필요한 정보를 통해 [RegisterUseCase]를 수행하고
 * 회원가입 성공 시 리프레쉬 토큰을 저장하고 [Boolean]타입의 결과를 반환한다.
 */
class RegisterIntegrationUseCase @Inject internal constructor(
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(
        provider: SocialLoginProvider,
        providerId: String,
        displayId: String,
        name: String,
        imageFileDetail: ImageFileDetail?,
        introduce: String?,
    ): Flow<Result<Unit, RegisterErrorType>> = runCatching {
        registerUseCase(
            provider = provider,
            providerId = ProviderId(providerId),
            displayId = DisplayId(displayId),
            name = Name(name),
            imageFileDetail = imageFileDetail,
            introduce = introduce?.let { Introduce(it) },
        ).flatMapResult { registerResult: RegisterResult ->
            val accessToken = registerResult.accessToken
            val refreshToken = registerResult.refreshToken
            authRepository
                .saveTokens(accessToken, refreshToken)
                .mapError { authErrorType ->
                    RegisterErrorType.CommonError(authErrorType)
                }
        }
    }.getOrElse { e -> flowOf(Result.Error(RegisterErrorType.Unexpected(e))) }
}
