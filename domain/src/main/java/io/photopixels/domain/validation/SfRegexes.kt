package io.photopixels.domain.validation

internal val HOSTNAME_REGEX = Regex(
    "^(?!www\\.)" + // Negative lookahead to disallow "www." at the beginning
        "(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9])\\.)+" + // At least one subdomain
        "[a-zA-Z0-9]{2,}$" // TLD with at least two letters
)

internal val NAME_REGEX = Regex("^[a-zA-ZÀ-ÖØ-öø'\\-\\.]+\$")

@Suppress("MaximumLineLength")
internal val EMAIL_REGEX =
    Regex(
        "^[a-zA-Z0-9.!#\$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}" +
            "[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*\$"
    )

internal val PASSWORD_REGEX =
    Regex(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)[A-Za-z\\d\\W]{8,}\$"
    ) // At least 8 chars with one Uppercase letter and one char
