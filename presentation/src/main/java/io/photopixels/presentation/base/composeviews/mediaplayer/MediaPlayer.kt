package io.photopixels.presentation.base.composeviews.mediaplayer

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent

@OptIn(UnstableApi::class)
@Composable
fun MediaPlayer(uri: String, isVisible: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var player by remember { mutableStateOf<Player?>(null) }
    val okHttpDataSourceFactory = LocalActivity.current?.let {
        hiltOkHttpDataSourceFactory(it)
    } ?: return

    LifecycleStartEffect(Unit) {
        player = ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(okHttpDataSourceFactory))
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(uri))
                prepare()
                playWhenReady = isVisible // auto play
                repeatMode = Player.REPEAT_MODE_ONE // auto repeat
            }

        onStopOrDispose {
            player?.release()
            player = null
        }
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            player?.play()
        } else if (player?.isPlaying == true) {
            player?.pause()
            player?.seekTo(0) // seek to start
        }
    }

    player?.let {
        MediaPlayer(player = it, modifier = modifier)
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun MediaPlayer(player: Player, modifier: Modifier = Modifier) {
    var showControls by remember { mutableStateOf(true) }

    val presentationState = rememberPresentationState(player)
    val scaledModifier = Modifier.resizeWithContentScale(ContentScale.Fit, presentationState.videoSizeDp)

    Box(modifier) {
        // Always leave PlayerSurface to be part of the Compose tree because it will be initialised in
        // the process. If this composable is guarded by some condition, it might never become visible
        // because the Player will not emit the relevant event, e.g. the first frame being ready.
        PlayerSurface(
            player = player,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
            modifier = scaledModifier.clickable(
                interactionSource = null,
                indication = null,
            ) {
                showControls = !showControls
            },
        )

        val overlayColor = remember { Color.Gray.copy(alpha = 0.5f) }
        if (showControls) {
            PlayPauseButton(
                player = player,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(overlayColor, CircleShape)
            )

            PlayerSeekBar(
                player = player,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(overlayColor)
                    .padding(horizontal = 8.dp)
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
            )
        }
    }
}

@Composable
fun hiltOkHttpDataSourceFactory(activity: Activity): OkHttpDataSource.Factory {
    return remember {
        EntryPointAccessors.fromActivity(activity, MediaEntryPoint::class.java).okHttpDataSourceFactory()
    }
}

@EntryPoint
@InstallIn(ActivityComponent::class)
interface MediaEntryPoint {
    fun okHttpDataSourceFactory(): OkHttpDataSource.Factory
}
