package com.peekr.data.account.model.request

import com.peekr.domain.account.model.DisplayId
import com.squareup.moshi.JsonClass

/** [com.peekr.domain.account.model.DisplayId] 요청용 */
@JsonClass(generateAdapter = true)
data class DisplayIdRequest(
    val id: String,
)

fun DisplayId.toDataModel(): DisplayIdRequest = DisplayIdRequest(this.id)
