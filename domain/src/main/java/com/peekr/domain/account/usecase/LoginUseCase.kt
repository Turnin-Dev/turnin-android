package com.peekr.domain.account.usecase

import com.peekr.domain.account.model.Login
import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

// TODO: 의존성 주입 필요

/**
 * [Login] 정보를 이용해 로그인 기능을 수행한다.
 *
 * 일반적으로 [SocialLoginUseCase]의 반환값을 그대로 넘겨 호출한다.
 *
 * @return [Result] – 성공 시 `true`, 실패 시 [ErrorType]
 */
class LoginUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    /** @param login 플랫폼·사용자 식별 정보를 담은 객체 */
    operator fun invoke(login: Login): Flow<Result<Boolean, ErrorType>> =
        accountRepository.login(login)
}
