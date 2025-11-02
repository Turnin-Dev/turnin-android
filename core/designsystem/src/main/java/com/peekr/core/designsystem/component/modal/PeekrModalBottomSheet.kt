package com.peekr.core.designsystem.component.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.theme.PeekrTheme
import kotlinx.coroutines.launch

/**
 * ModalBottomSheet 프레임
 *
 * ModalBottomSheet를 사용할 때 [content] 에 원하는 요소를 추가하면 된다.
 *
 * **그리고 반드시 [content]의 파라미터인 [Modifier]를 사용해야 한다. (디자인 일관성 보장)**
 *
 * @param sheetState SheetState
 * @param onDismissRequest ModalBottomSheet 가 사라질 때 수행할 동작
 * @param modifier [Modifier]
 * @param shouldDismissOnBackPress 뒤로가기 버튼으로 모달이 사라지게 할 지에 대한 여부
 * @param sheetGesturesEnabled 제스처 허용 여부
 * @param content ModalBottomSheet 컨텐츠
 *
 * @sample PeekrModalBottomSheetPreview
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeekrModalBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shouldDismissOnBackPress: Boolean = true,
    sheetGesturesEnabled: Boolean = true,
    content: @Composable (Modifier) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures { _, _ -> }
            },
        onDismissRequest = {
            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                onDismissRequest()
            }
        },
        sheetState = sheetState,
        dragHandle = null,
        containerColor = PeekrTheme.colorScheme.backgroundNormal,
        contentColor = PeekrTheme.colorScheme.textNormal,
        scrimColor = ScrimColor,
        shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress),
        sheetGesturesEnabled = sheetGesturesEnabled,
    ) {
        content(
            Modifier
                .background(PeekrTheme.colorScheme.backgroundNormal)
                .padding(BottomSheetContentPaddingValues),
        )
    }
}

private val BottomSheetContentPaddingValues =
    PaddingValues(top = 30.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)

private val ScrimColor = Color(0xB3353535)
