package io.photopixels.presentation.screens.home

import androidx.annotation.StringRes
import io.photopixels.domain.model.PhotoUiData
import java.time.YearMonth

data class HomeScreenState(
    val isSyncStarted: Boolean = false,
    val isLoading: Boolean = false,
    @StringRes val errorMsgId: Int? = null,
    val photoThumbnails: Map<YearMonth, List<PhotoUiData>> = emptyMap()
)
