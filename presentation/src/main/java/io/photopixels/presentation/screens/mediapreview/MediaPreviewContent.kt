package io.photopixels.presentation.screens.mediapreview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import io.photopixels.domain.model.MediaType
import io.photopixels.presentation.R
import io.photopixels.presentation.base.composeviews.CircularIndicator
import io.photopixels.presentation.base.composeviews.ShowAlertDialog
import io.photopixels.presentation.base.composeviews.mediaplayer.MediaPlayer
import io.photopixels.presentation.screens.mediapreview.MediaPreviewScreenState.MediaPreview
import io.photopixels.presentation.theme.PhotoPixelsTheme
import io.photopixels.presentation.theme.SFSecondaryLightBlue
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

private const val BEYOND_BOUNDS_PAGE_COUNT = 1

@Composable
fun MediaPreviewContent(screenState: MediaPreviewScreenState, onSubmitActions: (MediaPreviewActions) -> Unit) {
    var currentImageIndex by remember { mutableIntStateOf(0) }

    if (screenState.isLoading) {
        CircularIndicator()
    }

    if (screenState.isDeleteDialogVisible) {
        ShowDeletePromptDialog(
            onPositiveClick = { onSubmitActions(MediaPreviewActions.OnDeleteMediaClick(currentImageIndex)) },
            onNegativeClick = { onSubmitActions(MediaPreviewActions.OnDeleteDialogCancelClick) }
        )
    }

    if (screenState.mediaItems.isNotEmpty()) {
        val pagerState = rememberPagerState(
            initialPage = screenState.mediaToLoadFirstIndex,
            pageCount = { screenState.mediaItems.size }
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = BEYOND_BOUNDS_PAGE_COUNT,
                    key = { index -> screenState.mediaItems[index].id },
                    pageSize = PageSize.Fill
                ) { index ->
                    MediaPreview(screenState.mediaItems[index], isSelected = index == pagerState.currentPage)
                }

                DeleteButton(
                    visible = screenState.mediaItems.getOrNull(pagerState.currentPage) is MediaPreview.Remote,
                    modifier = Modifier
                        .padding(bottom = 100.dp)
                        .align(Alignment.BottomCenter),
                    onClick = {
                        onSubmitActions(MediaPreviewActions.OnDeleteIconClicked)
                        currentImageIndex = pagerState.currentPage
                    }
                )
                // SwipeArrows() Arrows removed for now
            }
        }
    }
}

@Composable
private fun MediaPreview(photo: MediaPreview, isSelected: Boolean) {
    val uri = when (photo) {
        is MediaPreview.Remote -> photo.mediaUrl
        is MediaPreview.Local -> photo.contentUri
    }

    when (photo.mediaType) {
        MediaType.IMAGE -> ZoomableFullScreenImage(painter = rememberAsyncImagePainter(uri))
        MediaType.VIDEO -> MediaPlayer(uri, isVisible = isSelected)
    }
}

@Composable
private fun ZoomableFullScreenImage(painter: Painter) {
    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            .zoomable(rememberZoomState())
    )
}

@Composable
private fun DeleteButton(visible: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Image(
            modifier = Modifier.clickable(onClick = onClick),
            painter = painterResource(id = android.R.drawable.ic_menu_delete),
            contentDescription = "delete",
        )
    }
}

private const val SWITE_ARROWS_ALPHA = 0.5f

@Suppress("UnusedPrivateMember")
@Composable
private fun BoxScope.SwipeArrows() {
    Icon(
        modifier = Modifier
            .size(60.dp, 60.dp)
            .alpha(SWITE_ARROWS_ALPHA)
            .align(Alignment.CenterStart)
            .padding(start = 20.dp),
        tint = SFSecondaryLightBlue,
        painter = painterResource(id = R.drawable.ic_arrow_back_24),
        contentDescription = null
    )

    Icon(
        modifier = Modifier
            .size(60.dp, 60.dp)
            .alpha(SWITE_ARROWS_ALPHA)
            .align(Alignment.CenterEnd)
            .padding(end = 20.dp),
        tint = SFSecondaryLightBlue,
        painter = painterResource(id = R.drawable.ic_arrow_forward_24),
        contentDescription = null
    )
}

@Composable
private fun ShowDeletePromptDialog(onPositiveClick: () -> Unit, onNegativeClick: () -> Unit) {
    ShowAlertDialog(
        title = stringResource(R.string.media_preview_delete_title),
        description = stringResource(R.string.media_preview_delete_msg),
        positiveButtonText = stringResource(R.string.button_delete),
        negativeButtonText = stringResource(R.string.button_cancel),
        onPositiveClick = onPositiveClick,
        onNegativeClick = onNegativeClick
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewPhotosContent() {
    PhotoPixelsTheme {
        MediaPreviewContent(
            screenState = MediaPreviewScreenState(
                mediaItems = listOf(
                    MediaPreview.Remote(
                        id = "",
                        mediaType = MediaType.IMAGE,
                        mediaUrl = "https://www.scalefocus.com/wp-content/uploads/2022/06/SF_brand_banner.png",
                    )
                )
            ),
            onSubmitActions = {}
        )
    }
}
