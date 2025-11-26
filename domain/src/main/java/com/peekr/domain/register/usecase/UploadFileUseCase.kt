package com.peekr.domain.register.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.mapError
import com.peekr.core.domain.file.FileRepository
import com.peekr.core.domain.file.model.Mime
import com.peekr.domain.register.error.RegisterErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** 파일을 업로드하고 파일의 url을 반환한다. */
internal class UploadFileUseCase @Inject constructor(
    private val fileRepository: FileRepository,
) {
    operator fun invoke(
        presignedUrl: String,
        file: ByteArray,
        fileName: String,
        mime: Mime,
    ): Flow<Result<String?, RegisterErrorType>> =
        fileRepository
            .uploadFile(presignedUrl, file, fileName, mime)
            .mapError { commonError ->
                RegisterErrorType.CommonError(commonError)
            }
}
