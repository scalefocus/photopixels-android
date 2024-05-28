package com.scalefocus.presentation.screens.photos

import com.bumptech.glide.load.model.GlideUrl

// TODO Add photo urls, and prepare Auth headers in VM
data class PhotosPreviewScreenState(
    val photoToLoadFirstIndex: Int = 0,
    val photosGlideUrls: List<GlideUrl> = emptyList(),
    val isLoading: Boolean = false
)
