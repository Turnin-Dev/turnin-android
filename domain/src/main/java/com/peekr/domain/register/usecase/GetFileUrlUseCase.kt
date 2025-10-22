package com.peekr.domain.register.usecase

import com.peekr.core.domain.coroutine.flatMapResult
import com.peekr.core.domain.file.model.Mime
import com.peekr.core.domain.file.model.PresignedUrl
import com.peekr.core.domain.util.Result
import com.peekr.domain.register.error.RegisterErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 파일 업로드용 사전 정의된 url을 가져오고,
 * 사전 정의된 url로 파일 업로드를 한다.
 *
 * 파일 업로드가 완료되면 파일의 url을 반환한다,
 * 파일 업로드가 정상적으로 이루어지지 않았을 경우 `null` 혹은 [com.peekr.core.common.util.Result.Error]을 반환한다.
 */
internal class GetFileUrlUseCase @Inject internal constructor(
    private val getFileUploadPresignedUrlUseCase: GetFileUploadPresignedUrlUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
) {
    operator fun invoke(
        file: ByteArray,
        fileName: String,
        mime: Mime,
    ): Flow<Result<String?, RegisterErrorType>> =
        getFileUploadPresignedUrlUseCase(fileName, mime)
            .flatMapResult { result: PresignedUrl ->
                uploadFileUseCase(result.presignedUrl, file, fileName, mime)
            }
}
