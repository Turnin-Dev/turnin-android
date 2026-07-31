package com.turnin.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.turnin.core.common.logger.AppLogger
import com.turnin.core.domain.feed.model.Feed
import com.turnin.core.domain.feed.model.FeedType
import com.turnin.domain.home.usecase.GetFeedsUseCase
import com.turnin.presentation.home.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFeedsUseCase: GetFeedsUseCase,
) : ViewModel() {
    private val tag = this::class.java.simpleName

    private val isInitFriendsPagingData = MutableStateFlow(false)

    /** 전체 유형의 피드 */
    val allFeedsPagingData = getFeedsUseCase(FeedType.ALL)
        .catch { e ->
            AppLogger.e(tag, e, "Unexpected feed pagination error")
            emit(PagingData.empty())
        }
        .map { pagingData: PagingData<Feed> ->
            pagingData.map { feed ->
                feed.toUiModel()
            }
        }
        .cachedIn(viewModelScope)

    /** 친구 유형의 피드 */
    val friendsPagingData = isInitFriendsPagingData
        .filter { it }
        .flatMapLatest {
            AppLogger.d("FeedRemoteMediator", "Paging Triggered!")
            getFeedsUseCase(FeedType.FRIEND)
                .catch { e ->
                    AppLogger.e(tag, e, "Unexpected feed pagination error")
                    emit(PagingData.empty())
                }
                .map { pagingData: PagingData<Feed> ->
                    pagingData.map { feed ->
                        feed.toUiModel()
                    }
                }
        }
        .cachedIn(viewModelScope)

    /** 친구 유형 피드 페이징 데이터 초기 로드 트리거 */
    fun initialLoadFriendsPagingData() {
        if (!isInitFriendsPagingData.value) {
            isInitFriendsPagingData.value = true
        }
    }
}
