package com.peekr.data.account.model.request

import com.peekr.domain.account.model.DisplayId

/** [com.peekr.domain.account.model.DisplayId] 요청용 */
data class DisplayIdRequest(
    val id: String,
)

fun DisplayId.toDataModel(): DisplayIdRequest = DisplayIdRequest(this.id)
