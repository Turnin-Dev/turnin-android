package com.turnin.shared.feature.announcement.data

import com.turnin.shared.core.error.NetworkErrorType
import com.turnin.shared.core.network.NetworkResult
import com.turnin.shared.core.result.Result
import com.turnin.shared.feature.announcement.model.Announcement
import com.turnin.shared.feature.announcement.model.AnnouncementId
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

internal class AnnouncementRepositoryImpl(
    private val announcementNetworkDataSource: AnnouncementNetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : AnnouncementRepository {
    override fun getAnnouncements(): Flow<Result<List<Announcement>, NetworkErrorType>> =
        flow {
            emit(Result.Loading)
            emit(announcementNetworkDataSource.getAnnouncements().toResult { list -> list.map { it.toDomainModel() } })
        }.catch { exception ->
            if (exception is CancellationException) throw exception
            emit(Result.Error(error = NetworkErrorType.Unexpected(exception), message = exception.message))
        }.flowOn(ioDispatcher)

    override suspend fun markAsRead(announcementId: AnnouncementId): Result<Unit, NetworkErrorType> =
        withContext(ioDispatcher) {
            announcementNetworkDataSource.markAsRead(announcementId).toResult { }
        }

    private inline fun <T, R> NetworkResult<T>.toResult(transform: (T) -> R): Result<R, NetworkErrorType> =
        when (this) {
            is NetworkResult.Success -> Result.Success(transform(data))
            is NetworkResult.Error -> Result.Error(error = error, message = message)
        }
}
