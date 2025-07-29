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
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): Collection<TestData> = listOf(
            // short url host only
            TestData(
                inputUrl = "example1.com",
                serverAddress = ServerAddress(protocol = "https", host = "example1.com", port = 0),
                outputUrl = "https://example1.com"
            ),
            // short url host and path only
            TestData(
                inputUrl = "example2.com/path",
                serverAddress = ServerAddress(protocol = "https", host = "example2.com", port = 0, path = "path"),
                outputUrl = "https://example2.com/path"
            ),
            // full url without path
            TestData(
                inputUrl = "https://example3.com:8080",
                serverAddress = ServerAddress(protocol = "https", host = "example3.com", port = 8080),
                outputUrl = "https://example3.com:8080"
            ),
            // full url with empty path
            TestData(
                inputUrl = "https://example4.com:8080/",
                serverAddress = ServerAddress(protocol = "https", host = "example4.com", port = 8080),
                outputUrl = "https://example4.com:8080"
            ),
            // full url with longer path
            TestData(
                inputUrl = "https://example5.com:8080//longer/path/",
                serverAddress = ServerAddress(
                    protocol = "https",
                    host = "example5.com",
                    port = 8080,
                    path = "longer/path"
                ),
                outputUrl = "https://example5.com:8080/longer/path"
            ),
            // default HTTP port
            TestData(
                inputUrl = "http://example6.com",
                serverAddress = ServerAddress(protocol = "http", host = "example6.com", port = 0),
                outputUrl = "http://example6.com"
            ),
            // default HTTPS port
            TestData(
                inputUrl = "https://example7.com",
                serverAddress = ServerAddress(protocol = "https", host = "example7.com", port = 0),
                outputUrl = "https://example7.com"
            ),
            // ip address with port and path
            TestData(
                inputUrl = "http://192.168.0.1:8080/path",
                serverAddress = ServerAddress(protocol = "http", host = "192.168.0.1", port = 8080, path = "path"),
                outputUrl = "http://192.168.0.1:8080/path"
            ),
        )
    }
}
