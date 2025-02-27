package io.photopixels.presentation.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import io.photopixels.domain.model.PhotoUiData
import io.photopixels.presentation.R
import io.photopixels.presentation.base.composeviews.SFButton
import io.photopixels.presentation.base.composeviews.ShowAlertDialog
import io.photopixels.presentation.base.composeviews.previewparams.PhotoUiDataPreviewParameter
import io.photopixels.presentation.theme.PhotoPixelsTheme
import io.photopixels.presentation.theme.SFSecondaryLightBlue
import io.photopixels.presentation.utils.toThumbnailsGroupString
import kotlinx.collections.immutable.ImmutableList
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LambdaParameterInRestartableEffect")
@Composable
fun HomeScreenContent(state: HomeScreenState, onSubmitActions: (HomeScreenActions) -> Unit) {
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = state.isLoading,
        onRefresh = { onSubmitActions(HomeScreenActions.LoadStartupData) }
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.errorMsgId?.let {
                ShowAlertDialog(
                    title = stringResource(id = R.string.error_title),
                    negativeButtonText = null,
                    description = stringResource(id = it),
                    onPositiveClick = { onSubmitActions(HomeScreenActions.CloseErrorDialog) }
                )
            }

            if (state.photoThumbnails.isEmpty() && !state.isLoading) {
                EmptyState(
                    isSyncStarted = state.isSyncStarted,
                    onBtnClick = { onSubmitActions(HomeScreenActions.OnSyncButtonClick) }
                )
            } else if (state.photoThumbnails.isNotEmpty()) {
                ThumbnailsGrid(
                    state = state,
                    onThumbnailClick = { onSubmitActions(HomeScreenActions.OnThumbnailClick(it)) }
                )
            }
        }
    }
}

@Composable
private fun ThumbnailsGrid(state: HomeScreenState, onThumbnailClick: (String) -> Unit) {
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 80.dp), state = gridState) {
        state.photoThumbnails.forEach { (yearMonth, photoThumbnails) ->
            // Month header
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = yearMonth.toThumbnailsGroupString(),
                )
            }

            // items for the current month
            items(count = photoThumbnails.size, key = { index -> photoThumbnails[index].id }) { index ->
                Box(
                    modifier = Modifier
                        .padding(1.dp)
                        .aspectRatio(1f)
                ) {
                    val photoData = photoThumbnails[index]
                    ThumbnailImage(
                        thumbnail = photoData.thumbnailByteArray,
                        isNewlyUploaded = photoData.isNewlyUploaded,
                        id = photoData.id,
                        onThumbnailClick = onThumbnailClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ThumbnailImage(
    id: String,
    thumbnail: ByteArray,
    isNewlyUploaded: Boolean,
    onThumbnailClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .clickable { onThumbnailClick(id) },
        shape = RectangleShape
    ) {
        Box(contentAlignment = Alignment.TopCenter) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(thumbnail)
                        .memoryCacheKey(id)
                        .diskCacheKey(id)
                        .build(),
                ),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )

            if (isNewlyUploaded) {
                SmallGreenCircle(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(isSyncStarted: Boolean, onBtnClick: () -> Unit) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val icon = painterResource(id = R.drawable.photo_library_24)
        Image(
            modifier = Modifier.size(100.dp),
            painter = icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(SFSecondaryLightBlue)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.home_screen_empty_msg),
            textAlign = TextAlign.Center,
            color = SFSecondaryLightBlue,
            style = TextStyle(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(30.dp))

        SFButton(
            text = stringResource(R.string.button_sync_photos),
            color = SFSecondaryLightBlue,
            enabled = !isSyncStarted,
            showLoader = isSyncStarted,
            onClick = onBtnClick
        )
    }
}

@Composable
private fun SmallGreenCircle(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(10.dp)) {
        val radius = 5.dp.toPx()
        val greenWithAlpha = Color.Green.copy(alpha = 0.8f)

        drawCircle(
            color = greenWithAlpha,
            radius = radius,
        )
    }
}

@Composable
@Preview(name = "EmptyState", group = "SingleViews", showBackground = true)
private fun PreviewEmptyState() {
    PhotoPixelsTheme {
        EmptyState(isSyncStarted = false, onBtnClick = {})
    }
}

@Composable
@Preview(name = "HomeScreen", showBackground = true, apiLevel = 33)
private fun PreviewHomeScreen(
    @PreviewParameter(PhotoUiDataPreviewParameter::class) photosUiList: ImmutableList<PhotoUiData>
) {
    PhotoPixelsTheme {
        HomeScreenContent(
            state = HomeScreenState(
                photoThumbnails = mapOf(
                    YearMonth.now() to photosUiList,
                    YearMonth.now().minusYears(1) to photosUiList
                )
            ),
            onSubmitActions = {}
        )
    }
}
