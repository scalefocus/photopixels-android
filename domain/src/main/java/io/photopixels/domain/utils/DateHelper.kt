package io.photopixels.domain.utils

import java.time.Instant
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateHelper {
    fun epochSecondsToDateTimeString(epochSeconds: Long): String =
        Instant.ofEpochSecond(epochSeconds)
            .atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    fun epochMilliToDateTimeString(epochMilli: Long): String =
        Instant.ofEpochMilli(epochMilli)
            .atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    fun dateTimeStringToYearMonth(dateTimeString: String): YearMonth =
        OffsetDateTime.parse(dateTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .let(YearMonth::from)
}
