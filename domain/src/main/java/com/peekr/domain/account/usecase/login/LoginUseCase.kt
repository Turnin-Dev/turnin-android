package com.peekr.domain.account.usecase.login

import com.peekr.domain.account.model.JWTToken
import com.peekr.domain.account.model.Login
import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * [Login] 정보를 이용해 로그인 기능을 수행한다.
 *
 * 일반적으로 [SocialLoginUseCase]의 반환값을 그대로 넘겨 호출한다.
 *
 * 그리고 로그인 성공 시 토큰을 DataStore 내에 저장한다.
 *
 * @return [Result] – 성공 시 `true`, 실패 시 [ErrorType]
 */
class LoginUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    /** @param login 플랫폼·사용자 식별 정보를 담은 객체 */
    operator fun invoke(login: Login): Flow<Result<JWTToken, ErrorType>> =
        accountRepository.login(login)
}
