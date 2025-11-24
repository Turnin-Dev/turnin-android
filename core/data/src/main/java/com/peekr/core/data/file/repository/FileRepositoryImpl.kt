package com.peekr.core.data.file.repository

import com.peekr.core.common.IO
import com.peekr.core.data.AppConfig
import com.peekr.core.data.file.network.FileDataSource
import com.peekr.core.data.file.network.response.toDomainModel
import com.peekr.core.data.network.error.toCommonErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.file.FileErrorType
import com.peekr.core.domain.file.FileRepository
import com.peekr.core.domain.file.model.Mime
import com.peekr.core.domain.file.model.PresignedUrl
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class FileRepositoryImpl @Inject constructor(
    private val fileDataSource: FileDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : FileRepository {
    override fun getFileUploadPresignedUrl(fileName: String, mime: Mime): Flow<Result<PresignedUrl, FileErrorType>> =
        safeResultFlow<PresignedUrl, FileErrorType>(ioDispatcher, { FileErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = fileDataSource.getFileUploadPresignedUrl(fileName, mime.type)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = FileErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun uploadFile(
        presignedUrl: String,
        file: ByteArray,
        fileName: String,
        mime: Mime,
    ): Flow<Result<String?, FileErrorType>> =
        safeResultFlow<String?, FileErrorType>(ioDispatcher, { FileErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = fileDataSource.uploadFile(presignedUrl, file, mime.type)) {
                is NetworkResult.Success -> {
                    val imageUrl = createImageUrl(fileName)
                    if (result.data) {
                        emit(
                            Result
                                .Success(imageUrl),
                        )
                    } else {
                        emit(
                            Result
                                .Success(null),
                        )
                    }
                }

                is NetworkResult.Error -> {
                    val error = FileErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    private fun createImageUrl(fileName: String): String = buildString {
        append(AppConfig.cloudStorageServerUrl.trimEnd('/'))
        append('/')
        append(fileName.trimStart('/'))
    }
}
