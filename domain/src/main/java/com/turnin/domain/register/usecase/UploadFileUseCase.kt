package com.turnin.domain.register.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.file.FileRepository
import com.turnin.core.domain.file.model.FileCategory
import com.turnin.core.domain.file.model.Mime
import com.turnin.domain.register.error.RegisterErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** 파일을 업로드하고 파일의 url을 반환한다. */
internal class UploadFileUseCase @Inject constructor(
    private val fileRepository: FileRepository,
) {
    /**
     * 파일을 업로드하고 파일의 url을 반환한다.
     *
     * @param presignedUrl 사전 정의된 URL
     * @param file [ByteArray]타입의 파일
     * @param fileName 파일명
     * @param mime [Mime]
     * @param fileCategory 파일 카테고리
     */
    operator fun invoke(
        presignedUrl: String,
        file: ByteArray,
        fileName: String,
        mime: Mime,
        fileCategory: FileCategory,
    ): Flow<Result<String?, RegisterErrorType>> =
        fileRepository
            .uploadFile(presignedUrl, file, fileName, mime, fileCategory)
            .mapError { commonError ->
                RegisterErrorType.CommonError(commonError)
            }
}
