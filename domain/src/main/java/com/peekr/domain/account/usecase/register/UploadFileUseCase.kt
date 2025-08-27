package com.peekr.domain.account.usecase.register

import com.peekr.domain.account.model.Mime
import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** 파일을 업로드하고 파일의 url을 반환한다. */
internal class UploadFileUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    operator fun invoke(
        presignedUrl: String,
        file: ByteArray,
        fileName: String,
        mime: Mime,
    ): Flow<Result<String?, ErrorType>> =
        accountRepository.uploadFile(presignedUrl, file, fileName, mime)
}
