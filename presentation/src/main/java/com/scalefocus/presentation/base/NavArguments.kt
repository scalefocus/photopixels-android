package com.scalefocus.presentation.base

// String extension functions for appending the parameters to the route

// Creating route by directly appending the arguments Result: "profile/123/Alice"
fun String.addArguments(arguments: List<String?>): String =
    this.plus(arguments.joinToString(prefix = "/", separator = "/"))

// Creating route by adding a parameter placeHolders in route instead of actual values Result: "settings/{category}"
fun String.addArgumentLabels(argumentLabels: List<String>): String =
    this.plus(argumentLabels.joinToString(prefix = "/", separator = "/", transform = { "{$it}" }))

// Creating route with nullable arguments
// Result: "login/?email=karina@abv.bg&password=KariPhoto1!"
fun String.addNullableArguments(arguments: Map<String, String>): String {
    val builder = StringBuilder()
    builder.append("?")
    arguments.forEach { builder.append("${it.key}=${it.value}&") }
    return this.plus(builder.removeSuffix("&").toString())
}

// Creating route for nullable arguments by adding parameter placeHolders in route instead of actual values
// Result: "login/?email={email}&password={password}"
fun String.addNullableArgumentsLabels(argumentLabels: List<String>): String =
    this.plus(
        argumentLabels.joinToString(
            prefix = "?",
            separator = "&",
            transform = { "$it={$it}" }
        )
    )

object Constants {
    const val EMAIL_ARGUMENT_NAME = "email"
    const val PASSWORD_ARGUMENT_NAME = "password"
    const val THUMBNAIL_ID_ARGUMENT_NAME = "thumbnail_server_id"
}
