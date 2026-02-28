package com.peekr.core.domain.account.repository

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import kotlinx.coroutines.flow.Flow

/** Account 리포지토리 */
interface AccountRepository {
    /**
     * 계정 삭제 (일부 데이터는 비활성화한다.)
     */
    fun deleteAccount(): Flow<Result<Unit, CommonErrorType>>
}
