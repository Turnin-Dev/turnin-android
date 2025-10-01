package com.peekr.core.data.file.repository

import com.peekr.core.common.IO
import com.peekr.core.data.AppConfig
import com.peekr.core.data.auth.network.response.toDomainModel
import com.peekr.core.data.file.network.FileNetworkDataSource
import com.peekr.core.data.file.response.toDomainModel
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.toErrorType
import com.peekr.core.domain.coroutine.safeResultFlow
import com.peekr.core.domain.file.FileRepository
import com.peekr.core.domain.file.model.Mime
import com.peekr.core.domain.file.model.PresignedUrl
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class FileRepositoryImpl @Inject constructor(
    private val fileNetworkDataSource: FileNetworkDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : FileRepository {
    override fun getFileUploadPresignedUrl(fileName: String, mime: Mime): Flow<com.peekr.core.domain.util.Result<PresignedUrl, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)
            when (val result = fileNetworkDataSource.getFileUploadPresignedUrl(fileName, mime.type)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toErrorType(), message = result.message))
                }
            }
        }

    override fun uploadFile(
        presignedUrl: String,
        file: ByteArray,
        fileName: String,
        mime: Mime,
    ): Flow<Result<String?, ErrorType>> = safeResultFlow(ioDispatcher) {
        emit(Result.Loading)
        when (val result = fileNetworkDataSource.uploadFile(presignedUrl, file, mime.type)) {
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
                emit(Result.Error(error = result.error.toErrorType(), message = result.message))
            }
        }
    }

    private fun createImageUrl(fileName: String): String = buildString {
        append(AppConfig.cloudStorageServerUrl.trimEnd('/'))
        append('/')
        append(fileName.trimStart('/'))
    }
}
