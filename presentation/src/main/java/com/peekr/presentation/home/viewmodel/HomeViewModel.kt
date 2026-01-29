package com.peekr.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.feed.model.Feed
import com.peekr.domain.home.usecase.GetFeedsUseCase
import com.peekr.presentation.home.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFeedsUseCase: GetFeedsUseCase,
) : ViewModel() {
    private val tag = this::class.java.simpleName

    val feedsPagingData = getFeedsUseCase()
        .map { pagingData: PagingData<Feed> ->
            pagingData.map { feed ->
                feed.toUiModel()
            }
        }
        .catch { e ->
            AppLogger.e(tag, "Unexpected feed pagination error")
            emit(PagingData.empty())
        }
        .cachedIn(viewModelScope)
}
