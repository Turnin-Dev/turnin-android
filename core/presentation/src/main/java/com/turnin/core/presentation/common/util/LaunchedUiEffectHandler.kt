package com.turnin.core.presentation.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * 상태형 일회성 이벤트를 수집할 때 사용한다.
 *
 * ##### 사용 예시
 * ```
 * @Composable
 * fun UserProfileScreen(
 *     viewModel: UserProfileViewModel = hiltViewModel()
 * ) {
 *   // ...
 *
 *   EffectHandler(viewModel.viewEffect, viewModel::processEvent)
 * }
 *
 * @Composable
 * private fun EffectHandler(effectFlow: Flow<ViewEffect?>, onEvent: (ViewEvent) -> Unit) {
 *     val navController = LocalNavHostController.current
 *
 *     LaunchedUiEffectHandler(
 *         effectFlow,
 *         onConsumeEffect = { onEvent(ViewEvent.ConsumeEffect) },
 *         onEffect = { effect ->
 *             when (effect) {
 *                 // All your cases here
 *             }
 *         },
 *     )
 * }
 * ```
 *
 * @param effectFlow 수집할 이벤트 플로우
 * @param onEffect 이벤트 처리
 * @param onConsumeEffect 이벤트 처리 후 재설정
 *
 * @see <a href="https://medium.com/proandroiddev/android-one-off-events-approaches-evolution-anti-patterns-add887cd0250">출처</a>
 */
@Composable
fun <EFFECT> LaunchedUiEffectHandler(
    effectFlow: Flow<EFFECT?>,
    onEffect: suspend (EFFECT) -> Unit,
    onConsumeEffect: () -> Unit,
) {
    val effect by effectFlow.collectAsStateWithLifecycle(null)
    val currentOnEffect by rememberUpdatedState(onEffect)
    val currentOnConsumeEffect by rememberUpdatedState(onConsumeEffect)

    LaunchedEffect(effect) {
        effect?.let {
            currentOnEffect(it)
            currentOnConsumeEffect()
        }
    }
}
