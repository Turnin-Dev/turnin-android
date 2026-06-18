package com.turnin.domain.register.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.flatMapResult
import com.turnin.core.domain.file.model.FileCategory
import com.turnin.core.domain.file.model.Mime
import com.turnin.core.domain.file.model.PresignedUrl
import com.turnin.domain.register.error.RegisterErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 파일 업로드용 사전 정의된 url을 가져오고,
 * 사전 정의된 url로 파일 업로드를 한다.
 *
 * 파일 업로드가 완료되면 파일의 url을 반환한다,
 * 파일 업로드가 정상적으로 이루어지지 않았을 경우 `null` 혹은 에러를 반환한다.
 */
internal class GetFileUrlUseCase @Inject internal constructor(
    private val getFileUploadPresignedUrlUseCase: GetFileUploadPresignedUrlUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
) {
    /**
     * @param file [ByteArray]타입의 파일
     * @param fileName 파일명
     * @param mime [Mime]
     * @param fileCategory 파일 카테고리
     */
    operator fun invoke(
        file: ByteArray,
        fileName: String,
        mime: Mime,
        fileCategory: FileCategory,
    ): Flow<Result<String?, RegisterErrorType>> =
        getFileUploadPresignedUrlUseCase(fileName, mime, fileCategory)
            .flatMapResult { result: PresignedUrl ->
                uploadFileUseCase(result.presignedUrl, file, fileName, mime, fileCategory)
            }
}
