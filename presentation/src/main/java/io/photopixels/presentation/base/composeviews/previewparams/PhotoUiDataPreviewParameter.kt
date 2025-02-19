package io.photopixels.presentation.base.composeviews.previewparams

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.photopixels.domain.model.PhotoUiData

class PhotoUiDataPreviewParameter : PreviewParameterProvider<PhotoUiData> {
    override val values: Sequence<PhotoUiData>
        get() = sequenceOf(
            PhotoUiData(
                id = "123",
                hash = "asd",
                thumbnailByteArray = byteArrayOf(),
                appleCloudId = "",
                androidCloudId = "",
                dateTaken = 0,
            ),
            PhotoUiData(
                id = "asd1",
                hash = "asd1",
                thumbnailByteArray = byteArrayOf(),
                isNewlyUploaded = true,
                appleCloudId = "",
                androidCloudId = "",
                dateTaken = 0,
            ),
            PhotoUiData(
                id = "njh",
                hash = "asd2",
                thumbnailByteArray = byteArrayOf(),
                appleCloudId = "",
                androidCloudId = "",
                dateTaken = 0,
            ),
            PhotoUiData(
                id = "asda",
                hash = "asd3",
                thumbnailByteArray = byteArrayOf(),
                appleCloudId = "",
                androidCloudId = "",
                dateTaken = 0,
            )
        )
}
