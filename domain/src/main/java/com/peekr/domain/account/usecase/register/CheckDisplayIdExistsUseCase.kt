package com.peekr.domain.account.usecase.register

import com.peekr.domain.account.model.DisplayId
import com.peekr.domain.account.model.ExistsResult
import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 사용자 표시 ID 중복검사
 */
class CheckDisplayIdExistsUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    operator fun invoke(displayId: String): Flow<Result<ExistsResult, ErrorType>> =
        accountRepository.existsDisplayId(DisplayId(displayId)).map { result ->
            when (result) {
                Result.Loading -> Result.Loading
                is Result.Error -> result
                is Result.Success -> Result.Success(ExistsResult(result.data))
            }
        }
}
