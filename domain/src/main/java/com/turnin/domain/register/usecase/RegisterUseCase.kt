package com.turnin.domain.register.usecase

import com.turnin.core.domain.auth.model.Register
import com.turnin.core.domain.auth.model.RegisterResult
import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.flatMapResult
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.common.error.mapError
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
 * 회원가입 기능을 수행한다.
 *
 * 회원가입에 성공하면 [RegisterResult]을 반환한다.
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
    ): Flow<Result<RegisterResult, RegisterErrorType>> {
        val getImageUrlFlow = imageFileDetail?.let {
            getFileUrlUseCase(it.bytes, it.name, it.mime)
        } ?: flowOf(Result.Success(null))

        return getImageUrlFlow.flatMapResult { profileImageUrl ->
            val register = Register(
                provider = provider,
                providerId = providerId,
                displayId = displayId,
                name = name,
                profileImageUrl = profileImageUrl,
                introduce = introduce,
            )
            authRepository
                .register(register)
                .mapError { commonError ->
                    when (commonError) {
                        is CommonErrorType.Network.Conflict -> RegisterErrorType.DuplicateUser
                        else -> RegisterErrorType.CommonError(commonError)
                    }
                }
        }
    }
}
