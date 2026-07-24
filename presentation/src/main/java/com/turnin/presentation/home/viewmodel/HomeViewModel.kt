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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFeedsUseCase: GetFeedsUseCase,
) : ViewModel() {
    private val tag = this::class.java.simpleName

    // TODO: 분리 예정
    val feedsPagingData = getFeedsUseCase(FeedType.ALL)
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

    fun loadAllFeeds() {
    }

    fun loadFriendFeeds() {
    }
}
