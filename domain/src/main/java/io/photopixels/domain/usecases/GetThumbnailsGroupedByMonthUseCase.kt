package io.photopixels.domain.usecases

import io.photopixels.domain.model.Thumbnail
import io.photopixels.domain.utils.DateHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import javax.inject.Inject

class GetThumbnailsGroupedByMonthUseCase @Inject constructor(
    private val getThumbnailsFromDbUseCase: GetThumbnailsFromDbUseCase,
) {

    operator fun invoke(): Flow<Map<YearMonth, List<Thumbnail>>> =
        getThumbnailsFromDbUseCase().map { photos ->
            photos.groupBy { photo ->
                DateHelper.dateTimeStringToYearMonth(photo.dateCreated)
            }
        }
}
