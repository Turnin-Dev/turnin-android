package com.peekr.core.data.auth.network.request

import com.peekr.core.domain.user.model.DisplayId
import com.squareup.moshi.JsonClass

/** [DisplayId] 요청용 */
@JsonClass(generateAdapter = true)
data class DisplayIdRequest(
    val id: String,
)

fun DisplayId.toDataModel(): DisplayIdRequest = DisplayIdRequest(this.value)
