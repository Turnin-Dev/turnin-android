package com.peekr.domain.register.usecase

import com.peekr.core.domain.auth.model.JWTToken
import com.peekr.core.domain.auth.model.Register
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.coroutine.flatMapResult
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.domain.register.model.ImageFileDetail
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 회원가입 기능을 수행한다.
 *
 * 회원가입에 성공하면 [JWTToken]을 반환한다.
 */
internal class RegisterUseCase @Inject internal constructor(
    private val authRepository: AuthRepository,
    private val getFileUrlUseCase: GetFileUrlUseCase,
) {
    operator fun invoke(
        provider: SocialLoginProvider,
        providerId: ProviderId,
        displayId: DisplayId,
        name: Name,
        imageFileDetail: ImageFileDetail?,
        introduce: Introduce?,
    ): Flow<Result<JWTToken, ErrorType>> = if (imageFileDetail != null) {
        getFileUrlUseCase(imageFileDetail.bytes, imageFileDetail.name, imageFileDetail.mime)
            .flatMapResult { profileImageUrl ->
                val register = Register(
                    provider = provider,
                    providerId = providerId,
                    displayId = displayId,
                    name = name,
                    profileImageUrl = profileImageUrl,
                    introduce = introduce,
                )
                authRepository.register(register)
            }
    } else {
        val register = Register(
            provider = provider,
            providerId = providerId,
            displayId = displayId,
            name = name,
            profileImageUrl = null,
            introduce = introduce,
        )
        authRepository.register(register)
    }
}
