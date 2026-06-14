package com.turnin.core.data.repository

import com.turnin.core.common.coroutine.IO
import com.turnin.core.data.source.network.datasource.AnnouncementNetworkDataSource
import com.turnin.core.data.source.network.dto.announcement.response.toDomainModel
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.announcement.model.Announcement
import com.turnin.core.domain.announcement.repository.AnnouncementRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.safeResultFlow
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.AnnouncementId
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class AnnouncementRepositoryImpl @Inject constructor(
    private val announcementNetworkDataSource: AnnouncementNetworkDataSource,
    @param:IO private val ioDispatcher: CoroutineDispatcher,
) : AnnouncementRepository {
    override fun getAnnouncements(): Flow<Result<List<Announcement>, CommonErrorType>> =
        safeResultFlow<List<Announcement>, CommonErrorType>(
            ioDispatcher,
            { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)
            when (val result = announcementNetworkDataSource.getAnnouncements()) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.map { it.toDomainModel() }))
                }

                is NetworkResult.Error -> {
                    emit(
                        Result.Error(
                            error = result.error.toCommonErrorType(),
                            message = result.message,
                        ),
                    )
                }
            }
        }

    override fun markAsRead(announcementId: AnnouncementId): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(
            ioDispatcher,
            { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)
            when (val result = announcementNetworkDataSource.markAsRead(announcementId)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    emit(
                        Result.Error(
                            error = result.error.toCommonErrorType(),
                            message = result.message,
                        ),
                    )
                }
            }
        }
}
