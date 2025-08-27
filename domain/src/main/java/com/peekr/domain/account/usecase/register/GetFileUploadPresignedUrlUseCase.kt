package com.peekr.domain.account.usecase.register

import com.peekr.domain.account.model.Mime
import com.peekr.domain.account.model.PresignedUrl
import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** 파일 업로드에 사용할 사전 정의된 url을 요청한다. */
internal class GetFileUploadPresignedUrlUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    operator fun invoke(fileName: String, mime: Mime): Flow<Result<PresignedUrl, ErrorType>> =
        accountRepository.getFileUploadPresignedUrl(fileName, mime)
}
