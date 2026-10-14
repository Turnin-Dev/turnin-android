package com.turnin.core.data.repository

import com.turnin.core.common.coroutine.IO
import com.turnin.core.common.logger.AppLogger
import com.turnin.core.data.source.local.database.dao.FeedDao
import com.turnin.core.data.source.local.database.dao.MyKeywordDao
import com.turnin.core.data.source.local.database.dao.MyProfileDao
import com.turnin.core.data.source.local.database.entity.toDomainModel
import com.turnin.core.data.source.local.database.entity.toEntity
import com.turnin.core.data.source.local.database.entity.toUserKeywordDetail
import com.turnin.core.data.source.local.datastore.DataStoreKey
import com.turnin.core.data.source.local.datastore.DataStoreManager
import com.turnin.core.data.source.local.memory.MemoryCache
import com.turnin.core.data.source.network.datasource.UserKeywordNetworkDataSource
import com.turnin.core.data.source.network.datasource.UserNetworkDataSource
import com.turnin.core.data.source.network.dto.common.toDomainModel
import com.turnin.core.data.source.network.dto.userKeyword.request.toDataModel
import com.turnin.core.data.source.network.dto.userKeyword.response.toDomainModel
import com.turnin.core.data.source.network.dto.userKeyword.response.toEntity
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.safeResultFlow
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.KeywordDescription
import com.turnin.core.domain.model.KeywordId
import com.turnin.core.domain.model.KeywordName
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.userKeyword.model.CreateUserKeyword
import com.turnin.core.domain.userKeyword.model.PatchUserKeyword
import com.turnin.core.domain.userKeyword.model.UserInfo
import com.turnin.core.domain.userKeyword.model.UserKeyword
import com.turnin.core.domain.userKeyword.model.UserKeywordDetail
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

// TODO: 현재 클래스가 너무 비대한 점과 많은 책임을 가지고 있는 점을 해결해야 함
class UserKeywordRepositoryImpl @Inject constructor(
    private val userKeywordNetworkDataSource: UserKeywordNetworkDataSource,
    private val userNetworkDataSource: UserNetworkDataSource,
    private val myKeywordDao: MyKeywordDao,
    private val myProfileDao: MyProfileDao,
    private val feedDao: FeedDao,
    private val memoryListCache: MemoryCache<UserId, List<UserKeywordDetail>>,
    private val memoryCache: MemoryCache<UserKeywordId, UserKeywordDetail>,
    private val dataStoreManager: DataStoreManager,
    @param:IO private val ioDispatcher: CoroutineDispatcher,
) : UserKeywordRepository {
    private val tag = this::class.java.simpleName

    override fun getMyDetailFromLocal(
        userKeywordId: UserKeywordId,
    ): Flow<UserKeywordDetail?> = flow {
        val myUserId = dataStoreManager.getLongData(DataStoreKey.User.UserId).first()
        if (myUserId == null) {
            emit(null)
            return@flow
        }

        val myProfile = myProfileDao.getByUserId(myUserId).first()
        val myKeyword = myKeywordDao.getById(userKeywordId.value).first()

        if (myProfile != null && myKeyword != null) {
            val userKeywordDetail = UserKeywordDetail(
                userKeywordId = UserKeywordId(myKeyword.userKeywordId),
                keywordId = KeywordId(myKeyword.keywordId),
                keywordName = KeywordName(myKeyword.keywordName),
                description = KeywordDescription(myKeyword.description),
                userInfo = UserInfo(
                    userId = UserId(myProfile.userId),
                    userName = Name(myProfile.name),
                    profileImageUrl = myProfile.profileImageUrl,
                ),
                createdAt = myKeyword.createdAt,
                updatedAt = myKeyword.updatedAt,
            )
            emit(userKeywordDetail)
        } else {
            emit(null)
        }
    }

    override fun getDetail(
        userId: UserId,
        userKeywordId: UserKeywordId,
    ): Flow<Result<UserKeywordDetail, CommonErrorType>> =
        safeResultFlow<UserKeywordDetail, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            // 1. 메모리 캐시 확인 (있으면 즉시 반환)
            val cachedKeyword = memoryCache[userKeywordId]
            if (cachedKeyword != null) {
                emit(Result.Success(cachedKeyword))
                return@safeResultFlow
            }

            emit(Result.Loading)

            // 2. 로컬 DB 확인 (피드 페이징을 진행했었다면 DB에 데이터 존재), 메모리 캐시로 승격
            val localKeyword = feedDao.getById(userKeywordId.value)
            if (localKeyword != null) {
                val localDomainKeyword = localKeyword.toUserKeywordDetail()
                memoryCache[userKeywordId] = localDomainKeyword
                emit(Result.Success(localDomainKeyword))
                return@safeResultFlow
            }

            // 3. 네트워크 호출 후 메모리 캐시에 저장
            when (val result = userKeywordNetworkDataSource.getDetail(userKeywordId)) {
                is NetworkResult.Success -> {
                    val networkDomainKeyword = result.data.toDomainModel()
                    memoryCache[userKeywordId] = networkDomainKeyword
                    emit(Result.Success(networkDomainKeyword))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getDetailRefresh(
        userId: UserId,
        userKeywordId: UserKeywordId,
    ): Flow<Result<UserKeywordDetail, CommonErrorType>> =
        safeResultFlow<UserKeywordDetail, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            val myUserId = dataStoreManager.getLongData(DataStoreKey.User.UserId).first()
            if (myUserId == null) {
                emit(Result.Error(CommonErrorType.Local.UserIdNotFound))
                return@safeResultFlow
            }

            when (val result = userKeywordNetworkDataSource.getDetail(userKeywordId)) {
                is NetworkResult.Success -> {
                    val networkDomainKeyword = result.data.toDomainModel()
                    if (myUserId == userId.value) {
                        // 내 키워드라면 DB에 업데이트
                        myKeywordDao.upsert(networkDomainKeyword.toEntity())
                    } else {
                        // 타인의 키워드라면 메모리 캐시에 업데이트
                        memoryCache[userKeywordId] = networkDomainKeyword
                    }
                    emit(Result.Success(networkDomainKeyword))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getMyKeywords(): Flow<List<UserKeyword>> =
        myKeywordDao.getAll()
            .map { keywordEntities ->
                keywordEntities.map { keywordEntity ->
                    keywordEntity.toDomainModel()
                }
            }
            .flowOn(ioDispatcher)

    override fun getMyKeywordsRefresh(): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = userNetworkDataSource.getMyKeywords()) {
                is NetworkResult.Success -> {
                    AppLogger.d(tag, "My keywords refresh successful")
                    val myKeywords = result.data.map { it.toDomainModel() }
                    myKeywordDao.deleteAll()
                    myKeywordDao.upsertAll(myKeywords.map { it.toEntity() })
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    AppLogger.d(tag, "My keywords refresh failure")
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getUserKeywords(
        userId: UserId,
        forceRefresh: Boolean,
    ): Flow<Result<List<UserKeywordDetail>, CommonErrorType>> =
        safeResultFlow<List<UserKeywordDetail>, CommonErrorType>(
            ioDispatcher,
            { CommonErrorType.Unexpected(it) },
        ) {
            // 만약, 사용자 키워드 리스트 개수 제한이 없다면 메모리 캐시 대신 로컬 DB를 통해 페이징을 진행해야 한다.

            // 1. 메모리 리스트 캐시에서 조회 (있다면 즉시 반환)
            // 단, forceRefresh가 true면 강제로 null 반환
            val cachedDetails = if (!forceRefresh) memoryListCache[userId] else null

            if (cachedDetails != null) {
                emit(Result.Success(cachedDetails))
            } else {
                emit(Result.Loading)

                // 2. 네트워크 조회
                when (val result = userNetworkDataSource.getUserKeywords(userId.value)) {
                    is NetworkResult.Success -> {
                        val keywords = result.data.map { it.toDomainModel() }
                        // 3. 메모리 캐시에 저장 (리스트, 단 건 전부 저장)
                        memoryListCache[userId] = keywords
                        keywords.forEach { memoryCache[it.userKeywordId] = it }
                        emit(Result.Success(keywords))
                    }

                    is NetworkResult.Error -> {
                        val error = result.error.toCommonErrorType()
                        emit(Result.Error(error = error, message = result.message))
                    }
                }
            }
        }

    override fun createUserKeyword(create: CreateUserKeyword): Flow<Result<UserKeyword, CommonErrorType>> =
        safeResultFlow<UserKeyword, CommonErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordNetworkDataSource.createUserKeyword(create.toDataModel())) {
                is NetworkResult.Success -> {
                    val entity = result.data.toEntity()
                    myKeywordDao.upsert(entity)
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun update(
        patchUserKeyword: PatchUserKeyword,
    ): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordNetworkDataSource.patch(patchUserKeyword.toDataModel())) {
                is NetworkResult.Success -> {
                    myKeywordDao.update(
                        userKeywordId = patchUserKeyword.userKeywordId.value,
                        keywordName = patchUserKeyword.keywordName.value,
                        description = patchUserKeyword.description.value,
                    )
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun deleteUserKeyword(
        userKeywordId: UserKeywordId,
    ): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordNetworkDataSource.deleteUserKeyword(userKeywordId)) {
                is NetworkResult.Success -> {
                    myKeywordDao.deleteById(userKeywordId.value)
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }
}
