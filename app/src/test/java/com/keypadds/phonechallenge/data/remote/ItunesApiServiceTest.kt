package com.keypadds.phonechallenge.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ItunesApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ItunesApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ItunesApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun search_returns_parsed_tracks_on_200() = runBlocking {
        val jsonResponse = """
            {
              "resultCount": 1,
              "results": [
                {
                  "wrapperType": "track",
                  "trackId": 12345,
                  "artistName": "Jack Johnson",
                  "trackName": "Upside Down",
                  "previewUrl": "https://example.com/preview",
                  "artworkUrl100": "https://example.com/artwork"
                }
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val response = apiService.search("jack johnson")

        assertEquals(1, response.resultCount)
        assertEquals("Upside Down", response.results[0].trackName)
        assertEquals(12345L, response.results[0].trackId)
    }

    @Test
    fun search_throws_on_non_200_response() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))

        val exception = assertThrows(HttpException::class.java) {
            runBlocking {
                apiService.search("unknown")
            }
        }

        assertEquals(404, exception.code())
    }
}
