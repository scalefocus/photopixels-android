package io.photopixels.presentation.screens.photos

import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.photopixels.presentation.R
import io.photopixels.presentation.base.composeviews.CircularIndicator
import io.photopixels.presentation.base.composeviews.ShowAlertDialog
import io.photopixels.presentation.theme.PhotoPixelsTheme
import io.photopixels.presentation.theme.SFSecondaryLightBlue
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

private const val BEYOND_BOUNDS_PAGE_COUNT = 1

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotosPreviewContent(screenState: PhotosPreviewScreenState, onSubmitActions: (PhotosPreviewActions) -> Unit) {
    var currentImageIndex by remember { mutableIntStateOf(0) }

    if (screenState.isLoading) {
        CircularIndicator()
    }

    if (screenState.isDeleteDialogVisible) {
        ShowDeletePromptDialog(
            onPositiveClick = { onSubmitActions(PhotosPreviewActions.OnDeletePhotoClick(currentImageIndex)) },
            onNegativeClick = { onSubmitActions(PhotosPreviewActions.OnDeleteDialogCancelClick) }
        )
    }

    if (screenState.photosGlideUrls.isNotEmpty()) {
        val pagerState = rememberPagerState(
            initialPage = screenState.photoToLoadFirstIndex,
            pageCount = { screenState.photosGlideUrls.size }
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondBoundsPageCount = BEYOND_BOUNDS_PAGE_COUNT,
                    key = { index -> screenState.photosGlideUrls[index].toStringUrl() },
                    pageSize = PageSize.Fill
                ) { index ->
                    FullScreenImage(imageGlideUrl = screenState.photosGlideUrls[index])
                }

                Row(
                    modifier = Modifier
                        .padding(bottom = 100.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Image(
                        modifier = Modifier.clickable {
                            onSubmitActions(PhotosPreviewActions.OnDeleteIconClicked)
                            currentImageIndex = pagerState.currentPage
                        },
                        painter = painterResource(id = android.R.drawable.ic_menu_delete),
                        contentDescription = "delete",
                    )
                }
                // SwipeArrows() Arrows removed for now
            }
        }
    }
}

@Composable
private fun FullScreenImage(imageGlideUrl: GlideUrl) {
    var image by remember { mutableStateOf<Drawable?>(null) }
    var showLoading by remember { mutableStateOf(true) }
    // val context = (LocalContext.current as Activity).applicationContext
    val context = LocalContext.current

    // Make image call only once per imageId
    LaunchedEffect(key1 = imageGlideUrl) {
        Glide
            .with(context)
            .load(imageGlideUrl)
            .apply(RequestOptions().centerCrop())
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    if (image == null || image !== resource) {
                        showLoading = false
                        image = resource
                    }
                }

                @Suppress("EmptyFunctionBlock")
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    if (showLoading) {
        CircularIndicator()
    }

    image?.let {
        Image(
            painter = rememberAsyncImagePainter(it),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .zoomable(rememberZoomState())
        )
    }
}

@Suppress("UnusedPrivateMember")
@Composable
private fun BoxScope.SwipeArrows() {
    Icon(
        modifier = Modifier
            .size(60.dp, 60.dp)
            .alpha(0.5f)
            .align(Alignment.CenterStart)
            .padding(start = 20.dp),
        tint = SFSecondaryLightBlue,
        painter = painterResource(id = R.drawable.ic_arrow_back_24),
        contentDescription = null
    )

    Icon(
        modifier = Modifier
            .size(60.dp, 60.dp)
            .alpha(0.5f)
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
        title = stringResource(R.string.photos_preview_delete_title),
        description = stringResource(R.string.photos_preview_delete_msg),
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
        PhotosPreviewContent(
            screenState = PhotosPreviewScreenState(
                photosGlideUrls =
                    listOf(GlideUrl("https://www.scalefocus.com/wp-content/uploads/2022/06/SF_brand_banner.png"))
            ),
            onSubmitActions = {}
        )
    }
}
