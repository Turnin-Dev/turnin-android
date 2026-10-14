package com.turnin.core.data.source.network.dto.auth.request

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.model.DisplayId

/** [DisplayId] 요청용 */
@JsonClass(generateAdapter = true)
data class DisplayIdRequest(
    val id: String,
)

fun DisplayId.toDataModel(): DisplayIdRequest = DisplayIdRequest(this.value)
