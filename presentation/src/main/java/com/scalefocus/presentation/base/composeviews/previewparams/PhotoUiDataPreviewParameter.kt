package com.scalefocus.presentation.base.composeviews.previewparams

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.scalefocus.domain.model.PhotoUiData

class PhotoUiDataPreviewParameter : PreviewParameterProvider<PhotoUiData> {
    override val values: Sequence<PhotoUiData>
        get() = sequenceOf(
            PhotoUiData(
                id = "123",
                thumbnail = "",
                hash = "asd",
                thumbnailByteArray = byteArrayOf(),
                appleCloudId = "",
                androidCloudId = ""
            ),
            PhotoUiData(
                id = "asd1",
                thumbnail = "",
                hash = "asd1",
                thumbnailByteArray = byteArrayOf(),
                isNewlyUploaded = true,
                appleCloudId = "",
                androidCloudId = ""
            ),
            PhotoUiData(
                id = "njh",
                thumbnail = "",
                hash = "asd2",
                thumbnailByteArray = byteArrayOf(),
                appleCloudId = "",
                androidCloudId = ""
            ),
            PhotoUiData(
                id = "asda",
                thumbnail = "",
                hash = "asd3",
                thumbnailByteArray = byteArrayOf(),
                appleCloudId = "",
                androidCloudId = ""
            )
        )
}
