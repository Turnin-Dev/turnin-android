package com.peekr.domain.register.usecase

import com.peekr.core.domain.file.FileRepository
import com.peekr.core.domain.file.model.Mime
import com.peekr.core.domain.file.model.PresignedUrl
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** 파일 업로드에 사용할 사전 정의된 url을 요청한다. */
internal class GetFileUploadPresignedUrlUseCase @Inject constructor(
    private val fileRepository: FileRepository,
) {
    operator fun invoke(fileName: String, mime: Mime): Flow<Result<PresignedUrl, ErrorType>> =
        fileRepository.getFileUploadPresignedUrl(fileName, mime)
}
