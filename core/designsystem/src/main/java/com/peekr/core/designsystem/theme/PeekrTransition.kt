package com.peekr.core.designsystem.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

data class PeekrTransition(
    val fadeIn: EnterTransition = fadeIn(),
    val fadeOut: ExitTransition = fadeOut(),
) {
    fun slideInTransition(direction: PeekrTransitionDirection): EnterTransition =
        when (direction) {
            PeekrTransitionDirection.Left -> {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(delayMillis = SLIDE_ANIMATION_DELAY),
                )
            }

            PeekrTransitionDirection.Right -> {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(delayMillis = SLIDE_ANIMATION_DELAY),
                )
            }

            PeekrTransitionDirection.Top -> {
                slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(delayMillis = SLIDE_ANIMATION_DELAY),
                )
            }

            PeekrTransitionDirection.Bottom -> {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(delayMillis = SLIDE_ANIMATION_DELAY),
                )
            }
        }

    fun slideOutTransition(direction: PeekrTransitionDirection): ExitTransition =
        when (direction) {
            PeekrTransitionDirection.Left -> {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(delayMillis = SLIDE_ANIMATION_DELAY),
                )
            }

            PeekrTransitionDirection.Right -> {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(delayMillis = SLIDE_ANIMATION_DELAY),
                )
            }

            PeekrTransitionDirection.Top -> {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(delayMillis = SLIDE_ANIMATION_DELAY),
                ) // + fadeOut()
            }

            PeekrTransitionDirection.Bottom -> {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(delayMillis = SLIDE_ANIMATION_DELAY),
                ) // + fadeOut()
            }
        }
}

object PeekrTransitionObject {
    private val transition = PeekrTransition()
    val fadeIn = transition.fadeIn
    val fadeOut = transition.fadeOut
    val enterTransition: (PeekrTransitionDirection) -> EnterTransition = {
        transition.slideInTransition(it)
    }
    val exitTransition: (PeekrTransitionDirection) -> ExitTransition = {
        transition.slideOutTransition(it)
    }
}

enum class PeekrTransitionDirection {
    Left,
    Right,
    Top,
    Bottom,
}

private object AnimationTokens {
    // ------------------------------ V2 ------------------------------
    val fadeInTween = fadeIn(tween(durationMillis = 120, easing = FastOutSlowInEasing))
    val slideInVerticallyTween = slideInVertically(
        initialOffsetY = { 32 },
        animationSpec = tween(
            durationMillis = 200,
            easing = CubicBezierEasing(0.25f, 0.46f, 0.45f, 0.94f),
        ),
    )
    val fadeOutTween = fadeOut(tween(durationMillis = 100, easing = FastOutLinearInEasing))
    val slideOutVerticallyTween = slideOutVertically(
        targetOffsetY = { 24 },
        animationSpec = tween(
            durationMillis = 150,
            easing = CubicBezierEasing(0.55f, 0.06f, 0.68f, 0.19f),
        ),
    )

    // ------------------------------ V1 ------------------------------
//    val fadeInSpring = fadeIn(spring(stiffness = Spring.StiffnessHigh))
//    val scaleInSpring = scaleIn(
//        initialScale = .9f,
//        animationSpec = spring(
//            dampingRatio = Spring.DampingRatioMediumBouncy,
//            stiffness = Spring.StiffnessMediumLow,
//        ),
//    )
//    val fadeOutTween = fadeOut(tween(easing = EaseInBack))
//    val scaleOutTween = scaleOut(
//        targetScale = .9f,
//        animationSpec = tween(easing = EaseInBack),
//    )
}

private const val SLIDE_ANIMATION_DELAY = 100
