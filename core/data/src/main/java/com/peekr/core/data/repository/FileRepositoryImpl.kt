package com.peekr.core.data.repository

import com.peekr.core.common.coroutine.IO
import com.peekr.core.data.AppConfig
import com.peekr.core.data.source.network.datasource.FileNetworkDataSource
import com.peekr.core.data.source.network.dto.file.response.toDomainModel
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.file.FileRepository
import com.peekr.core.domain.file.model.Mime
import com.peekr.core.domain.file.model.PresignedUrl
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class FileRepositoryImpl @Inject constructor(
    private val fileNetworkDataSource: FileNetworkDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : FileRepository {
    override fun getFileUploadPresignedUrl(fileName: String, mime: Mime): Flow<Result<PresignedUrl, CommonErrorType>> =
        safeResultFlow<PresignedUrl, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = fileNetworkDataSource.getFileUploadPresignedUrl(fileName, mime.type)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getFileUpdatePresignedUrl(
        newFileName: String,
        mime: Mime,
    ): Flow<Result<PresignedUrl, CommonErrorType>> =
        safeResultFlow<PresignedUrl, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = fileNetworkDataSource.getFileUpdatePresignedUrl(newFileName, mime.type)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun uploadFile(
        presignedUrl: String,
        file: ByteArray,
        fileName: String,
        mime: Mime,
    ): Flow<Result<String?, CommonErrorType>> =
        safeResultFlow<String?, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = fileNetworkDataSource.uploadFile(presignedUrl, file, mime.type)) {
                is NetworkResult.Success -> {
                    val imageUrl = createImageUrl(fileName)
                    if (result.data) {
                        emit(Result.Success(imageUrl))
                    } else {
                        emit(Result.Success(null))
                    }
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
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
