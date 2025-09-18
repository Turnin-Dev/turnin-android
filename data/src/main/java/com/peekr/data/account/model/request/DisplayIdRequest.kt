package com.peekr.data.account.model.request

import com.peekr.domain.common.model.DisplayId
import com.squareup.moshi.JsonClass

/** [DisplayId] 요청용 */
@JsonClass(generateAdapter = true)
data class DisplayIdRequest(
    val id: String,
)

fun DisplayId.toDataModel(): DisplayIdRequest = DisplayIdRequest(this.value)
