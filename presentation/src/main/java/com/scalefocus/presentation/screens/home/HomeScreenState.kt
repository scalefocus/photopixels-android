package com.scalefocus.presentation.screens.home

import androidx.annotation.StringRes
import com.scalefocus.domain.model.PhotoUiData

data class HomeScreenState(
    val isLoading: Boolean = false,
    @StringRes val errorMsgId: Int? = null,
    val photoThumbnails: List<PhotoUiData> = emptyList()
)
