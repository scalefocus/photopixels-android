package com.scalefocus.domain.validation

internal val HOSTNAME_REGEX = Regex(
    "^(?!www\\.)" + // Negative lookahead to disallow "www." at the beginning
        "(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9])\\.)+" + // At least one subdomain
        "[a-zA-Z0-9]{2,}$" // TLD with at least two letters
)
