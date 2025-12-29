package com.peekr.core.data.source.network.dto.keywordGraph.response

import com.peekr.core.domain.keywordGraph.model.NodeContext
import com.squareup.moshi.JsonClass

/**
 * 노드 컨텍스트 항목 응답 바디
 *
 * @property userNode 사용자 노드
 * @property keywordNodes 키워드 노드 목록
 */
@JsonClass(generateAdapter = true)
data class NodeContextResponse(
    val userNode: UserNodeResponse,
    val keywordNodes: List<KeywordNodeResponse>,
)

fun NodeContextResponse.toDomainModel(): NodeContext =
    NodeContext(
        userNode = userNode.toDomainModel(),
        keywordNodes = keywordNodes.map { it.toDomainModel() },
    )
