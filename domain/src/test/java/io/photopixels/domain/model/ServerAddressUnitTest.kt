package io.photopixels.domain.model

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals

@RunWith(value = Parameterized::class)
class ServerAddressUnitTest(
    private val testData: TestData
) {

    @Test
    fun `test server address parsing`() {
        val (from, expected, _) = testData
        val result = ServerAddress.fromString(urlString = from)
        assertEquals(expected, result)
    }

    @Test
    fun `test server address toString`() {
        val (_, from, expected) = testData
        val result = from.toString()
        assertEquals(expected, result)
    }

    data class TestData(
        val inputUrl: String,
        val serverAddress: ServerAddress,
        val outputUrl: String,
    ) {
        override fun toString(): String = "$inputUrl -> $outputUrl"
    }

    companion object {

        @Suppress("LongMethod")
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): Collection<TestData> = listOf(
            // short url host only
            TestData(
                inputUrl = "example.com",
                serverAddress = ServerAddress(protocol = "https", host = "example.com"),
                outputUrl = "https://example.com"
            ),
            // short url host and path only
            TestData(
                inputUrl = "example.com/path",
                serverAddress = ServerAddress(protocol = "https", host = "example.com", path = "path"),
                outputUrl = "https://example.com/path"
            ),
            // full url without path
            TestData(
                inputUrl = "https://example.com:8080",
                serverAddress = ServerAddress(protocol = "https", host = "example.com", port = 8080),
                outputUrl = "https://example.com:8080"
            ),
            // full url with empty path
            TestData(
                inputUrl = "https://example.com:8080/",
                serverAddress = ServerAddress(protocol = "https", host = "example.com", port = 8080),
                outputUrl = "https://example.com:8080"
            ),
            // full url with longer path
            TestData(
                inputUrl = "https://example.com:8080//longer/path/",
                serverAddress = ServerAddress(
                    protocol = "https",
                    host = "example.com",
                    port = 8080,
                    path = "longer/path"
                ),
                outputUrl = "https://example.com:8080/longer/path"
            ),
            // default HTTP port
            TestData(
                inputUrl = "http://example.com",
                serverAddress = ServerAddress(protocol = "http", host = "example.com"),
                outputUrl = "http://example.com"
            ),
            // default HTTPS port
            TestData(
                inputUrl = "https://example.com",
                serverAddress = ServerAddress(protocol = "https", host = "example.com"),
                outputUrl = "https://example.com"
            ),
            // capitalized HTTP protocol
            TestData(
                inputUrl = "HTTP://example.com",
                serverAddress = ServerAddress(protocol = "http", host = "example.com"),
                outputUrl = "http://example.com"
            ),
            // capitalized HTTPS protocol
            TestData(
                inputUrl = "HTTPS://example.com",
                serverAddress = ServerAddress(protocol = "https", host = "example.com"),
                outputUrl = "https://example.com"
            ),
            // ip address only
            TestData(
                inputUrl = "192.168.0.1",
                serverAddress = ServerAddress(protocol = "https", host = "192.168.0.1"),
                outputUrl = "https://192.168.0.1"
            ),
            // ip address with protocol
            TestData(
                inputUrl = "http://192.168.0.1",
                serverAddress = ServerAddress(protocol = "http", host = "192.168.0.1"),
                outputUrl = "http://192.168.0.1"
            ),
            // ip address with port
            TestData(
                inputUrl = "http://192.168.0.1:8080",
                serverAddress = ServerAddress(protocol = "http", host = "192.168.0.1", port = 8080),
                outputUrl = "http://192.168.0.1:8080"
            ),
            // ip address with port and path
            TestData(
                inputUrl = "http://192.168.0.1:8080/path",
                serverAddress = ServerAddress(protocol = "http", host = "192.168.0.1", port = 8080, path = "path"),
                outputUrl = "http://192.168.0.1:8080/path"
            ),
            // subdomains
            TestData(
                inputUrl = "sub.domain.example.co.uk",
                serverAddress = ServerAddress(protocol = "https", host = "sub.domain.example.co.uk"),
                outputUrl = "https://sub.domain.example.co.uk"
            ),
            // subdomains with protocol, port and path
            TestData(
                inputUrl = "http://sub.domain.example.co.uk:8080/path",
                serverAddress = ServerAddress(
                    protocol = "http",
                    host = "sub.domain.example.co.uk",
                    port = 8080,
                    path = "path"
                ),
                outputUrl = "http://sub.domain.example.co.uk:8080/path"
            ),
        )
    }
}
