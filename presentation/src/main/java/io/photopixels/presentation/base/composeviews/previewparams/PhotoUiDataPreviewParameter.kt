package io.photopixels.presentation.base.composeviews.previewparams

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.photopixels.domain.model.ServerMedia

class PhotoUiDataPreviewParameter : PreviewParameterProvider<ServerMedia> {
    override val values: Sequence<ServerMedia>
        get() = sequenceOf(
            ServerMedia(
                id = "123",
                hash = "asd",
                thumbnailByteArray = byteArrayOf(),
                appleCloudId = "",
                androidCloudId = "",
                dateCreated = "",
            ),
            ServerMedia(
                id = "asd1",
                hash = "asd1",
                thumbnailByteArray = byteArrayOf(),
                isNewlyUploaded = true,
                appleCloudId = "",
                androidCloudId = "",
                dateCreated = "",
            ),
            ServerMedia(
                id = "njh",
                hash = "asd2",
                thumbnailByteArray = byteArrayOf(),
                appleCloudId = "",
                androidCloudId = "",
                dateCreated = "",
            ),
            ServerMedia(
                id = "asda",
                hash = "asd3",
                thumbnailByteArray = byteArrayOf(),
                appleCloudId = "",
                androidCloudId = "",
                dateCreated = "",
            )
        )
}
