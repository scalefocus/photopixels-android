package io.photopixels.domain.utils

import java.time.Duration
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

    fun formatDurationFromMillis(milliseconds: Long): String {
        val duration = Duration.ofMillis(milliseconds)

        val hours = duration.toHours()
        val minutes = duration.minusHours(hours).toMinutes()
        val seconds = duration.minusHours(hours).minusMinutes(minutes).seconds

        return if (hours > 0) {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }
}
