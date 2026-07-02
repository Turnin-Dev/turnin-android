package com.turnin.presentation.util

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.yield

suspend fun <T : Any> PagingData<T>.collectDataForTest(
    mainDispatcher: CoroutineDispatcher,
    workerDispatcher: CoroutineDispatcher,
): List<T> {
    val dcb =
        object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(
                oldItem: T,
                newItem: T,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: T,
                newItem: T,
            ): Boolean = oldItem == newItem
        }

    val differ =
        AsyncPagingDataDiffer(
            diffCallback = dcb,
            updateCallback =
                object : ListUpdateCallback {
                    override fun onInserted(
                        position: Int,
                        count: Int,
                    ) {
                    }

                    override fun onRemoved(
                        position: Int,
                        count: Int,
                    ) {
                    }

                    override fun onMoved(
                        fromPosition: Int,
                        toPosition: Int,
                    ) {
                    }

                    override fun onChanged(
                        position: Int,
                        count: Int,
                        payload: Any?,
                    ) {
                    }
                },
            mainDispatcher = mainDispatcher,
            workerDispatcher = workerDispatcher,
        )

    differ.submitData(this)

    yield()

    return differ.snapshot().items
}
