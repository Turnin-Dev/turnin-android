package com.peekr.core.data.repository

import com.peekr.core.common.coroutine.IO
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.source.local.database.dao.MyProfileDao
import com.peekr.core.data.source.local.database.entity.toDomainModel
import com.peekr.core.data.source.local.database.entity.toEntity
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.local.memory.MemoryCache
import com.peekr.core.data.source.network.datasource.UserNetworkDataSource
import com.peekr.core.data.source.network.dto.user.request.IntroducePatchRequest
import com.peekr.core.data.source.network.dto.user.request.toDataModel
import com.peekr.core.data.source.network.dto.user.response.toDomainModel
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.model.CoreMyProfile
import com.peekr.core.domain.user.model.CoreUserProfile
import com.peekr.core.domain.user.model.User
import com.peekr.core.domain.user.model.UserPatch
import com.peekr.core.domain.user.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryImpl @Inject constructor(
    private val userNetworkDataSource: UserNetworkDataSource,
    private val dataStoreManager: DataStoreManager,
    private val memoryCache: MemoryCache<Long, CoreUserProfile>,
    private val myProfileDao: MyProfileDao,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {
    private val tag = this::class.java.simpleName

    override suspend fun getUserId(): UserId? {
        val userId = dataStoreManager.getLongData(DataStoreKey.User.UserId).firstOrNull()
        if (userId == null) return null
        return UserId(userId)
    }

    override fun getUser(): Flow<Result<User, CommonErrorType>> =
        safeResultFlow<User, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = userNetworkDataSource.getUser()) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getMyProfile(): Flow<CoreMyProfile?> = dataStoreManager
        .getLongData(DataStoreKey.User.UserId)
        .flatMapLatest { userId ->
            if (userId == null) {
                flowOf(null)
            } else {
                myProfileDao.getByUserId(userId).map {
                    it?.toDomainModel()
                }
            }
        }
        .flowOn(ioDispatcher)

    override fun getMyProfileRefresh(): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = userNetworkDataSource.getMyProfile()) {
                is NetworkResult.Success -> {
                    AppLogger.d(tag, "My profile refresh successful")
                    val myProfile = result.data.toDomainModel()
                    myProfileDao.upsert(myProfile.toEntity())
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    AppLogger.d(tag, "My profile refresh failure")
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getUserProfile(
        userId: UserId,
        forceRefresh: Boolean,
    ): Flow<Result<CoreUserProfile, CommonErrorType>> =
        safeResultFlow<CoreUserProfile, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            val cachedProfile = if (!forceRefresh) memoryCache[userId.value] else null

            if (cachedProfile != null) {
                emit(Result.Success(cachedProfile))
            } else {
                emit(Result.Loading)
                when (val result = userNetworkDataSource.getUserProfile(userId)) {
                    is NetworkResult.Success -> {
                        val profile = result.data.toDomainModel()
                        memoryCache[userId.value] = profile
                        emit(Result.Success(profile))
                    }

                    is NetworkResult.Error -> {
                        val error = result.error.toCommonErrorType()
                        emit(Result.Error(error = error, message = result.message))
                    }
                }
            }
        }

    override fun updateMyProfile(patch: UserPatch): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            val userId = dataStoreManager.getLongData(DataStoreKey.User.UserId).firstOrNull()
            if (userId == null) {
                AppLogger.e(tag, "User ID not found in local DataStore.")
                emit(Result.Error(CommonErrorType.Local.UserIdNotFound))
            } else {
                when (val result = userNetworkDataSource.updateUser(patch.toDataModel())) {
                    is NetworkResult.Success -> {
                        myProfileDao.updateProfile(
                            userId = userId,
                            displayId = patch.displayId.value,
                            name = patch.name.value,
                            profileImageUrl = patch.profileImageUrl,
                            introduce = patch.introduce.value,
                        )
                        emit(Result.Success(result.data))
                    }

                    is NetworkResult.Error -> {
                        val error = result.error.toCommonErrorType()
                        emit(Result.Error(error = error, message = result.message))
                    }
                }
            }
        }

    override fun updateMyIntroduce(introduce: Introduce): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            val introducePatchRequest = IntroducePatchRequest(introduce.value)
            val userId = dataStoreManager.getLongData(DataStoreKey.User.UserId).firstOrNull()
            if (userId == null) {
                AppLogger.e(tag, "User ID not found in local DataStore.")
                emit(Result.Error(CommonErrorType.Local.UserIdNotFound))
            } else {
                when (val result = userNetworkDataSource.updateIntroduce(introducePatchRequest)) {
                    is NetworkResult.Success -> {
                        myProfileDao.updateIntroduce(
                            userId = userId,
                            introduce = introduce.value,
                        )
                        emit(Result.Success(result.data))
                    }

                    is NetworkResult.Error -> {
                        val error = result.error.toCommonErrorType()
                        emit(Result.Error(error = error, message = result.message))
                    }
                }
            }
        }
}
