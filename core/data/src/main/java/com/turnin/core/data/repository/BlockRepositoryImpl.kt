package com.turnin.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.turnin.core.common.coroutine.IO
import com.turnin.core.data.paging.PeekrCursorPagingSource
import com.turnin.core.data.source.local.memory.MemoryCache
import com.turnin.core.data.source.network.datasource.BlockNetworkDataSource
import com.turnin.core.data.source.network.dto.block.request.toDataModel
import com.turnin.core.data.source.network.dto.block.response.BlockedUserResponse
import com.turnin.core.data.source.network.dto.block.response.toDomainModel
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.block.model.BlockPagingTokens
import com.turnin.core.domain.block.model.BlockReason
import com.turnin.core.domain.block.model.BlockedUser
import com.turnin.core.domain.block.model.CreateBlock
import com.turnin.core.domain.block.repository.BlockRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.safeResultFlow
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.BlockId
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.model.CoreUserProfile
import com.turnin.core.domain.userKeyword.model.UserKeywordDetail
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BlockRepositoryImpl @Inject constructor(
    private val blockNetworkDataSource: BlockNetworkDataSource,
    private val userMemoryCache: MemoryCache<UserId, CoreUserProfile>,
    private val keywordMemoryCache: MemoryCache<UserId, List<UserKeywordDetail>>,
    @param:IO private val ioDispatcher: CoroutineDispatcher,
) : BlockRepository {
    override fun getBlockedUsers(): Flow<PagingData<BlockedUser>> {
        val pageSize = BlockPagingTokens.PAGE_SIZE
        val prefetchDistance = BlockPagingTokens.PREFETCH_DISTANCE

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance,
                initialLoadSize = pageSize + prefetchDistance,
            ),
            pagingSourceFactory = {
                PeekrCursorPagingSource<Long, BlockedUserResponse>(
                    apiCall = { nextCursor ->
                        blockNetworkDataSource.getBlockedUsers(nextCursor, pageSize)
                    },
                )
            },
        )
            .flow
            .map { pagingData ->
                pagingData.map(BlockedUserResponse::toDomainModel)
            }
    }

    override fun getBlockReasons(): Flow<Result<List<BlockReason>, CommonErrorType>> =
        safeResultFlow<List<BlockReason>, CommonErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)
            when (val result = blockNetworkDataSource.getBlockReasons()) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.map { it.toDomainModel() }))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun createBlock(block: CreateBlock): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            // 사용자 프로필 관련 액션 수행 시 메모리 캐시 무효화
            userMemoryCache.remove(block.blockedId)
            keywordMemoryCache.remove(block.blockedId)

            // 네트워크 호출
            when (val result = blockNetworkDataSource.createBlock(block.toDataModel())) {
                is NetworkResult.Success -> {
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun deleteBlock(
        blockId: BlockId,
        userId: UserId,
    ): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            // 사용자 프로필 관련 액션 수행 시 메모리 캐시 무효화
            userMemoryCache.remove(userId)
            keywordMemoryCache.remove(userId)

            // 네트워크 호출
            when (val result = blockNetworkDataSource.deleteBlock(blockId.value)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }
}
