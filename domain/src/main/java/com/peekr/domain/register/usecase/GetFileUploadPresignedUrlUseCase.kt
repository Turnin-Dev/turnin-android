package com.peekr.domain.register.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.mapError
import com.peekr.core.domain.file.FileErrorType
import com.peekr.core.domain.file.FileRepository
import com.peekr.core.domain.file.model.Mime
import com.peekr.core.domain.file.model.PresignedUrl
import com.peekr.domain.register.error.RegisterErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** 파일 업로드에 사용할 사전 정의된 url을 요청한다. */
internal class GetFileUploadPresignedUrlUseCase @Inject constructor(
    private val fileRepository: FileRepository,
) {
    operator fun invoke(fileName: String, mime: Mime): Flow<Result<PresignedUrl, RegisterErrorType>> =
        fileRepository
            .getFileUploadPresignedUrl(fileName, mime)
            .mapError { fileErrorType ->
                when (fileErrorType) {
                    is FileErrorType.Unexpected -> RegisterErrorType.Unexpected(fileErrorType.cause)
                    else -> RegisterErrorType.FileError(fileErrorType)
                }
            }
}
