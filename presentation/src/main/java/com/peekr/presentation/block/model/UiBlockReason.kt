package com.peekr.presentation.block.model

import com.peekr.core.domain.block.model.BlockReasonId
import com.peekr.core.presentation.ui.component.modal.SelectableReason

/**
 * UI용 차단 사유
 *
 * @property id 차단 사유 ID
 * @property code 차단 사유 코드
 * @property description 차단 사유 설명
 */
data class UiBlockReason(
    val id: BlockReasonId,
    val code: String,
    override val description: String,
) : SelectableReason
