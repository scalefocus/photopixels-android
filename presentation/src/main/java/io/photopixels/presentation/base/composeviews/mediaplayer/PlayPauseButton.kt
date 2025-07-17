package io.photopixels.presentation.base.composeviews.mediaplayer

import androidx.annotation.OptIn
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import io.photopixels.presentation.R
import io.photopixels.presentation.theme.PhotoPixelsTheme

@OptIn(UnstableApi::class)
@Composable
internal fun PlayPauseButton(player: Player, modifier: Modifier = Modifier) {
    val state = rememberPlayPauseButtonState(player)
    PlayPauseButton(
        atEnd = state.showPlay.not(),
        isEnabled = state.isEnabled,
        onClick = state::onClick,
        modifier = modifier
    )
}

@Composable
internal fun PlayPauseButton(
    atEnd: Boolean,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit = { },
) {
    val imageVectorPainter = rememberAnimatedVectorPainter(
        animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.ic_play_pause_animated_24),
        atEnd = atEnd
    )

    IconButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier
    ) {
        Icon(
            painter = imageVectorPainter,
            contentDescription = "Play/Pause Button",
            tint = Color.White,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Preview
@Composable
private fun PlayPauseButtonPreview() {
    PhotoPixelsTheme {
        PlayPauseButton(atEnd = false) { }
    }
}
