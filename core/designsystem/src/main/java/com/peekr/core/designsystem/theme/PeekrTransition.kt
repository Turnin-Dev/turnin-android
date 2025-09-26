package com.peekr.core.designsystem.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

data class PeekrTransition(
    val fadeIn: EnterTransition = fadeIn(),
    val fadeOut: ExitTransition = fadeOut(),
    val dialogEnter: EnterTransition = AnimationTokens.fadeInSpring + AnimationTokens.scaleInSpring,
    val dialogExit: ExitTransition = AnimationTokens.fadeOutTween + AnimationTokens.scaleOutTween,
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
    val dialogEnter = transition.dialogEnter
    val dialogExit = transition.dialogExit
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
    val fadeInSpring = fadeIn(spring(stiffness = Spring.StiffnessHigh))
    val scaleInSpring = scaleIn(
        initialScale = .8f,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
    )
    val fadeOutTween = fadeOut(tween(easing = EaseInBack))
    val scaleOutTween = scaleOut(
        targetScale = .8f,
        animationSpec = tween(easing = EaseInBack),
    )
}

private const val SLIDE_ANIMATION_DELAY = 100
