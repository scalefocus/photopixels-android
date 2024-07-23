package io.photopixels.domain.validation

internal val HOSTNAME_REGEX = Regex(
    "^" + // Start of string
        "(?:" + // Non-capturing group for protocol (optional)
        "(?:https?://)" + // Allow http or https protocols
        ")?" +
        "(?:" + // Non-capturing group for domain or IP (optional)
        "(?:(?:[a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9])\\.)+" + // One or more subdomains
        "[a-zA-Z]{2,}" + // Top-level domain with at least two letters
        "|" + // OR
        "(?:[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})" + // IPv4 address format, IPv6 can be added if needed
        ")" +
        "(?::([0-9]{1,5})?)?" + // Optional port number
        "(?:[/\\?#][^\\s]*?)?" + // Optional path, query string, or fragment
        "$" // End of string
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
