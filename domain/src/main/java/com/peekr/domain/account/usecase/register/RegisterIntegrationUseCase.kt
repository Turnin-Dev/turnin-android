package com.peekr.domain.account.usecase.register

import com.peekr.domain.account.model.ImageFileDetail
import com.peekr.domain.account.model.JWTToken
import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.account.usecase.SaveRefreshTokenUseCase
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import com.peekr.domain.shared.util.flatMapResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 회원가입을 진행한다.
 *
 * 회원가입에 필요한 정보를 통해 [RegisterUseCase]를 수행하고
 * 회원가입 성공 시 리프레쉬 토큰을 저장하고 [Boolean]타입의 결과를 반환한다.
 */
class RegisterIntegrationUseCase @Inject internal constructor(
    private val registerUseCase: RegisterUseCase,
    private val saveRefreshTokenUseCase: SaveRefreshTokenUseCase,
) {
    operator fun invoke(
        provider: SocialLoginProvider,
        providerId: String,
        displayId: String,
        name: String,
        imageFileDetail: ImageFileDetail?,
        introduce: String?,
    ): Flow<Result<Boolean, ErrorType>> =
        registerUseCase(
            provider = provider,
            providerId = providerId,
            displayId = displayId,
            name = name,
            imageFileDetail = imageFileDetail,
            introduce = introduce,
        ).flatMapResult { token: JWTToken -> saveRefreshTokenUseCase(token.refreshToken) }
}
