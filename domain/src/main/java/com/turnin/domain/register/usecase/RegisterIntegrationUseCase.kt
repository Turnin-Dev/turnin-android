package com.turnin.domain.register.usecase

import com.turnin.core.domain.auth.model.RegisterResult
import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.flatMapResult
import com.turnin.core.domain.common.coroutine.mapSuccess
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.eventBus.AuthEventBus
import com.turnin.core.domain.file.model.ImageFileDetail
import com.turnin.core.domain.model.DisplayId
import com.turnin.core.domain.model.Introduce
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.ProviderId
import com.turnin.core.domain.model.SocialLoginProvider
import com.turnin.domain.register.error.RegisterErrorType
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
    private val authEventBus: AuthEventBus,
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
            // 1. 토큰 저장
            val accessToken = registerResult.accessToken
            val refreshToken = registerResult.refreshToken

            authRepository
                .saveTokens(accessToken, refreshToken)
                .mapSuccess {
                    // 2. 로그인 이벤트 발행
                    authEventBus.emitLogin()
                }
                .mapError { commonError -> RegisterErrorType.CommonError(commonError) }
        }
    }.getOrElse { e -> flowOf(Result.Error(RegisterErrorType.Unexpected(e))) }
}
