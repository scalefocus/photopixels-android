package com.scalefocus.domain.validation

internal object PixelsFieldValidator {

    fun isFieldValid(value: String, validationRules: ValidationRules): Boolean {
        return validationRules.getRegex().matches(value)
    }
}

enum class ValidationRules(private val regex: Regex) {
    SERVER_ADDRESS(HOSTNAME_REGEX),
    NAME(NAME_REGEX),
    EMAIL(EMAIL_REGEX),
    PASSWORD(PASSWORD_REGEX);

    fun getRegex() = regex
}
