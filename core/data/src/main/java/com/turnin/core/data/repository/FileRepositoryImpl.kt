package com.turnin.core.data.repository

import com.turnin.core.common.coroutine.IO
import com.turnin.core.data.AppConfig
import com.turnin.core.data.source.network.datasource.FileNetworkDataSource
import com.turnin.core.data.source.network.dto.file.response.toDomainModel
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.safeResultFlow
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.file.FileRepository
import com.turnin.core.domain.file.model.FileCategory
import com.turnin.core.domain.file.model.Mime
import com.turnin.core.domain.file.model.PresignedUrl
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class FileRepositoryImpl @Inject constructor(
    private val fileNetworkDataSource: FileNetworkDataSource,
    @param:IO private val ioDispatcher: CoroutineDispatcher,
) : FileRepository {
    override fun getFileUploadPresignedUrl(
        fileName: String,
        mime: Mime,
        fileCategory: FileCategory,
    ): Flow<Result<PresignedUrl, CommonErrorType>> =
        safeResultFlow<PresignedUrl, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (
                val result =
                    fileNetworkDataSource.getFileUploadPresignedUrl(fileName, mime.type, fileCategory)
            ) {
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
        fileCategory: FileCategory,
    ): Flow<Result<PresignedUrl, CommonErrorType>> =
        safeResultFlow<PresignedUrl, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (
                val result =
                    fileNetworkDataSource.getFileUpdatePresignedUrl(newFileName, mime.type, fileCategory)
            ) {
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
        fileCategory: FileCategory,
    ): Flow<Result<String?, CommonErrorType>> =
        safeResultFlow<String?, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = fileNetworkDataSource.uploadFile(presignedUrl, file, mime.type)) {
                is NetworkResult.Success -> {
                    val imageUrl = createFileUrl(fileName, fileCategory)
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

    /**
     * 파일 카테고리와 파일명으로 파일 URL을 생성한다.
     *
     * @param fileName 파일명
     * @param fileCategory 파일 카테고리
     */
    private fun createFileUrl(
        fileName: String,
        fileCategory: FileCategory,
    ): String {
        val newFileName = "${fileCategory.prefix}/$fileName"

        return buildString {
            append(AppConfig.cloudStorageServerUrl.trimEnd('/'))
            append('/')
            append(newFileName.trimStart('/'))
        }
    }
}
