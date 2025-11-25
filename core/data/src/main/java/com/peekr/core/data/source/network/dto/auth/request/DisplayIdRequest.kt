package com.peekr.core.data.source.network.dto.auth.request

import com.peekr.core.domain.model.DisplayId
import com.squareup.moshi.JsonClass

/** [DisplayId] 요청용 */
@JsonClass(generateAdapter = true)
data class DisplayIdRequest(
    val id: String,
)

fun DisplayId.toDataModel(): DisplayIdRequest = DisplayIdRequest(this.value)
