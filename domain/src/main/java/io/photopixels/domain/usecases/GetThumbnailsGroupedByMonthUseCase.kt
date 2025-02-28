package io.photopixels.domain.usecases

import io.photopixels.domain.model.PhotoUiData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

class GetThumbnailsGroupedByMonthUseCase @Inject constructor(
    private val getThumbnailsFromDbUseCase: GetThumbnailsFromDbUseCase
) {

    operator fun invoke(): Flow<Map<YearMonth, List<PhotoUiData>>> =
        getThumbnailsFromDbUseCase().map { photos ->
            photos.groupBy { photo ->
                epochMilliToYearMonth(photo.dateTaken)
            }
        }

    private fun epochMilliToYearMonth(epochMilli: Long): YearMonth =
        Instant.ofEpochMilli(epochMilli)
            .atZone(ZoneId.systemDefault())
            .let(YearMonth::from)
}
