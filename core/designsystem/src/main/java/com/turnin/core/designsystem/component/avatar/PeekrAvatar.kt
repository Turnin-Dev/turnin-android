package com.turnin.core.designsystem.component.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.turnin.core.designsystem.R
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.core.designsystem.util.click.clickableSingle
import com.turnin.core.designsystem.util.icon.PeekrIcons
import com.turnin.core.designsystem.util.icon.Profile

/**
 * 프로필 이미지에 사용하는 아바타
 *
 * @param model [AsyncImage]에서 사용 가능한 이미지 모델
 * @param contentDescription 이미지 설명
 * @param modifier [Modifier]
 * @param filterQuality 이미지 화질
 * @param onClick 클릭 시
 */
@Composable
fun PeekrAvatar(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    filterQuality: FilterQuality = FilterQuality.Medium,
    onClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val imageRequester = remember(model) {
        ImageRequest
            .Builder(context)
            .data(model)
            .build()
    }

    CoreAvatar(
        modifier = modifier,
        onClick = onClick,
        image = {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                model = imageRequester,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                filterQuality = filterQuality,
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Success -> {
                        SubcomposeAsyncImageContent()
                    }

                    else -> {
                        AvatarPlaceholder(modifier = Modifier.clip(CircleShape))
                    }
                }
            }
        },
    )
}

/**
 * 프로필 이미지에 사용하는 아바타
 *
 * @param model [ImageBitmap]타입의 이미지
 * @param contentDescription 이미지 설명
 * @param modifier [Modifier]
 * @param onClick 클릭 시
 */
@Composable
fun PeekrAvatar(
    model: ImageBitmap?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    CoreAvatar(
        modifier = modifier,
        onClick = onClick,
        image = {
            if (model == null) {
                AvatarPlaceholder(modifier = Modifier.clip(CircleShape))
            } else {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    bitmap = model,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                )
            }
        },
    )
}

@Composable
private fun CoreAvatar(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    image: @Composable BoxScope.() -> Unit,
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickableSingle(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFFDCDCDC))
            .then(clickableModifier),
        contentAlignment = Alignment.Center,
    ) {
        image()
    }
}

@Composable
private fun AvatarPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color(0xFFDCDCDC)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize(0.7f),
            imageVector = PeekrIcons.Filled.Normal.Profile.imageVector,
            tint = PeekrTheme.colorScheme.staticWhite,
            contentDescription = stringResource(R.string.avatar_placeholder_content_desc),
        )
    }
}
