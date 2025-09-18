package com.peekr.domain.account.usecase.register

import com.peekr.domain.account.model.ImageFileDetail
import com.peekr.domain.account.model.JWTToken
import com.peekr.domain.account.model.ProviderId
import com.peekr.domain.account.model.Register
import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.common.model.DisplayId
import com.peekr.domain.common.model.Introduce
import com.peekr.domain.common.model.Name
import com.peekr.domain.common.util.ErrorType
import com.peekr.domain.common.util.Result
import com.peekr.domain.common.util.flatMapResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 회원가입 기능을 수행한다.
 *
 * 회원가입에 성공하면 [JWTToken]을 반환한다.
 */
internal class RegisterUseCase @Inject internal constructor(
    private val accountRepository: AccountRepository,
    private val getFileUrlUseCase: GetFileUrlUseCase,
) {
    operator fun invoke(
        provider: SocialLoginProvider,
        providerId: String,
        displayId: String,
        name: String,
        imageFileDetail: ImageFileDetail?,
        introduce: String?,
    ): Flow<Result<JWTToken, ErrorType>> = if (imageFileDetail != null) {
        getFileUrlUseCase(imageFileDetail.bytes, imageFileDetail.name, imageFileDetail.mime)
            .flatMapResult { profileImageUrl ->
                val register = Register(
                    provider = provider,
                    providerId = ProviderId(providerId),
                    displayId = DisplayId(displayId),
                    name = Name(name),
                    profileImageUrl = profileImageUrl,
                    introduce = introduce?.let { Introduce(it) },
                )
                accountRepository.register(register)
            }
    } else {
        val register = Register(
            provider = provider,
            providerId = ProviderId(providerId),
            displayId = DisplayId(displayId),
            name = Name(name),
            profileImageUrl = null,
            introduce = introduce?.let { Introduce(it) },
        )
        accountRepository.register(register)
    }
}
