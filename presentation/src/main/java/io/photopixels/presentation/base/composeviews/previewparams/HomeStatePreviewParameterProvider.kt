package io.photopixels.presentation.base.composeviews.previewparams

import android.util.Base64
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.photopixels.domain.model.MediaType
import io.photopixels.domain.model.Thumbnail
import io.photopixels.presentation.screens.home.HomeScreenState
import java.time.YearMonth

private const val PREVIEW_IMAGE_ENCODED = "iVBORw0KGgoAAAANSUhEUgAAAMwAAADACAMAAAB/Pny7AAAAYFBMVEXw8PDUVp" +
    "/x8/Lt5Ony+vbXbanhpcXTUp3Waafy9vTp0d7RPJX0//nUUJzw7O7VW6HmwNTkudDfm8DSR5nbibfgoMPafbHjr8vWYqTelr3hqc" +
    "fnxdfr1+HPI43js83QMJGkknghAAACKElEQVR4nO3c61aCUBRFYW4ihKCCFyzS93/L7AFqnfLIWNScD7DdH45+7TBJiIiI6JfViy" +
    "nTmOllKeVKk+XprVlIfSEx1TpdSCUY08C4BsY1MK6BcQ2Ma2BcA+MaGNfAuAbGNTCugXENjGtgXAvBtGuR/hg1Ic6MAMwq3YoCHp" +
    "oaEWXGq8Ik3dtOtB/Vcx36OWbsJn3UVGfRoqtasUizK54/I+hAq/r8q1KL7MXnxJgRJTBgwIABAwYMGDBgwIABAwYMGDBgwIABA+" +
    "ZfYTJRrQ+FQwBGz6jVJgHaXDWN6v2p96vEbNWM21ku0ul3zlby/HZQx7V9rp6Yvs9dq8fPgCEH2rJ4/PtXE4qgPTRGXoLlkAhF2Q" +
    "MMGDBgwIABAwYMGDBgwIABAwYMGDBgwIAB86MhAaeECOn7XNhJQ95FjnX3/KLcZ7qyF12q1QxVB7VH/1ar7zcrRJukGtrn1+zUIo" +
    "W06ALO3jGy+T8AMGDAgAEDBgwYMGDAgAEDBgwYMGDAgAED5u9g5Jte82AC3jkLwMib1jQ2g0j/smSrRtyu+romv5fuclCdyuP39f" +
    "K3GFM543KSe+zVtSnLR3EXG9pjJh5YwKVYz5B7DLOczgPe5JxlRhSMywybRcC4LgLGdREwrouAcV0EjOsiYFwXAeO6CBjXRcC4Lg" +
    "LGdREwrouAcV0EjOsiYFwXiYRRd5G21Yt4zLgPOclXys5yEY8Z9ykb2WJmEBER0Rd9AHGo3HSypo6GAAAAAElFTkSuQmCC"

class HomeStatePreviewParameterProvider : PreviewParameterProvider<HomeScreenState> {

    private val previewImage by lazy {
        Base64.decode(PREVIEW_IMAGE_ENCODED, Base64.DEFAULT)
    }

    override val values: Sequence<HomeScreenState>
        get() = sequenceOf(
            // empty state
            HomeScreenState(photoThumbnails = emptyMap()),
            // state with thumbnails
            HomeScreenState(
                photoThumbnails = mapOf(
                    YearMonth.now() to List(4) { index ->
                        Thumbnail.RemoteThumbnail(
                            id = "key$index",
                            hash = "",
                            mediaType = MediaType.IMAGE,
                            thumbnailByteArray = previewImage,
                            dateCreated = "",
                            isNewlyUploaded = true,
                        )
                    },
                    YearMonth.now().minusYears(1) to List(8) { index ->
                        Thumbnail.RemoteThumbnail(
                            id = "key2$index",
                            hash = "",
                            mediaType = MediaType.IMAGE,
                            thumbnailByteArray = previewImage,
                            dateCreated = "",
                            isNewlyUploaded = false,
                        )
                    }
                )
            )
        )
}
