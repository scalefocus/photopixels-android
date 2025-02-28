package io.photopixels.presentation.utils

import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val monthOnlyFormatter = DateTimeFormatter.ofPattern("MMMM")
private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

fun YearMonth.toThumbnailsGroupString(): String {
    val formater = if (this.year == YearMonth.now().year) monthOnlyFormatter else monthYearFormatter
    return this.format(formater)
}
