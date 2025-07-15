package io.photopixels.presentation.base.composeviews.mediaplayer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import io.photopixels.domain.utils.DateHelper
import io.photopixels.presentation.theme.PhotoPixelsTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.seconds

private const val UPDATES_PER_SECOND = 30

@Composable
internal fun PlayerSeekBar(player: Player, modifier: Modifier = Modifier) {
    var currentPositionMillis by remember { mutableLongStateOf(0L) }
    var mediaDurationMillis by remember { mutableLongStateOf(0L) }
    var isPlaying by remember { mutableStateOf(player.isPlaying) }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
        }
    }

    if (isPlaying) {
        LaunchedEffect(Unit) {
            while (true) {
                currentPositionMillis = player.currentPosition
                mediaDurationMillis = player.duration
                delay(1.seconds / UPDATES_PER_SECOND)
            }
        }
    }

    PlayerSeekBar(
        currentPositionMillis = currentPositionMillis,
        mediaDurationMillis = mediaDurationMillis,
        modifier = modifier,
        onPositionChange = { newPosition ->
            currentPositionMillis = newPosition
            player.seekTo(newPosition)
        }
    )
}

@Composable
internal fun PlayerSeekBar(
    currentPositionMillis: Long,
    mediaDurationMillis: Long,
    modifier: Modifier = Modifier,
    onPositionChange: (Long) -> Unit = {},
) {
    val sliderPosition = remember(currentPositionMillis) { currentPositionMillis.toFloat() }
    val sliderRange = remember(mediaDurationMillis) { 0f..mediaDurationMillis.toFloat() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = DateHelper.formatDurationFromMillis(currentPositionMillis),
            color = Color.White,
        )

        Slider(
            value = sliderPosition,
            valueRange = sliderRange,
            onValueChange = {
                onPositionChange(it.roundToLong())
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
        )

        Text(
            text = DateHelper.formatDurationFromMillis(mediaDurationMillis),
            color = Color.White,
        )
    }
}

@Preview
@Composable
private fun PlayerSeekBarPreview() {
    PhotoPixelsTheme {
        PlayerSeekBar(
            currentPositionMillis = 15.seconds.inWholeMilliseconds,
            mediaDurationMillis = 45.seconds.inWholeMilliseconds
        ) {}
    }
}
