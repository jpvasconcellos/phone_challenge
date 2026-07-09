package com.keypadds.phonechallenge.data.repository

import app.cash.turbine.test
import com.keypadds.phonechallenge.data.local.SongDao
import com.keypadds.phonechallenge.data.local.SongEntity
import com.keypadds.phonechallenge.data.remote.ItunesApiService
import com.keypadds.phonechallenge.data.remote.dto.ItunesResponseDto
import com.keypadds.phonechallenge.data.remote.dto.TrackDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SongRepositoryImplTest {

    private lateinit var apiService: ItunesApiService
    private lateinit var dao: SongDao
    private lateinit var repository: SongRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        apiService = mockk()
        dao = mockk(relaxed = true)
        
        repository = SongRepositoryImpl(apiService, dao)
    }

    @Test
    fun search_returns_cached_results_first() = runTest(testDispatcher) {
        val cachedEntity = SongEntity(
            trackId = 1L, collectionId = 2L, query = "jack", trackName = "Cached", 
            artistName = "Artist", previewUrl = "", artworkUrl = "", trackNumber = 1, lastFetched = 1L
        )
        every { dao.getSongsByQuery("jack") } returns flowOf(listOf(cachedEntity))
        coEvery { apiService.search(any(), any(), any(), any()) } returns ItunesResponseDto(0, emptyList())

        repository.searchSongs("jack").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Cached", result[0].trackName)
            awaitComplete()
        }
    }

    @Test
    fun search_refreshes_cache_after_network_success() = runTest(testDispatcher) {
        val dto = TrackDto(
            trackId = 2L, collectionId = 2L, trackName = "Network", 
            artistName = "Artist", previewUrl = "", artworkUrl100 = "", trackNumber = 1, wrapperType = "track"
        )
        coEvery { apiService.search(term = "jack", offset = 0) } returns ItunesResponseDto(1, listOf(dto))
        
        repository.refreshSearch("jack")

        coVerify { dao.deleteSongsByQuery("jack") }
        coVerify { dao.insertSongs(match { it[0].trackId == 2L && it[0].trackName == "Network" }) }
    }

    @Test
    fun search_returns_cached_when_network_fails() = runTest(testDispatcher) {
        coEvery { apiService.search(term = "jack", offset = 0) } throws IOException("No internet")
        
        repository.refreshSearch("jack")
        
        coVerify(exactly = 0) { dao.deleteSongsByQuery("jack") }
        coVerify(exactly = 0) { dao.insertSongs(any()) }
    }

    @Test
    fun load_next_page_appends_results_with_correct_offset() = runTest(testDispatcher) {
        val dto1 = TrackDto(
            trackId = 1L, collectionId = 2L, trackName = "Network1", 
            artistName = "Artist", previewUrl = "", artworkUrl100 = "", trackNumber = 1, wrapperType = "track"
        )
        coEvery { apiService.search(term = "jack", offset = 0) } returns ItunesResponseDto(1, listOf(dto1))
        repository.refreshSearch("jack")

        val dto2 = TrackDto(
            trackId = 2L, collectionId = 2L, trackName = "Network2", 
            artistName = "Artist", previewUrl = "", artworkUrl100 = "", trackNumber = 2, wrapperType = "track"
        )
        coEvery { apiService.search(term = "jack", offset = 1) } returns ItunesResponseDto(1, listOf(dto2))
        
        repository.loadNextPage("jack")

        coVerify { apiService.search(term = "jack", offset = 1) }
        coVerify { dao.insertSongs(match { it[0].trackId == 2L && it[0].trackName == "Network2" }) }
    }

    @Test
    fun load_next_page_does_nothing_when_already_loading() = runTest(testDispatcher) {
        // First, call refreshSearch so currentQuery is set to "jack"
        coEvery { apiService.search(term = "jack", offset = 0) } returns ItunesResponseDto(0, emptyList())
        repository.refreshSearch("jack")

        // Now mock the API to delay so we can simulate concurrent loadNextPage calls
        coEvery { apiService.search(term = "jack", offset = 0) } coAnswers {
            kotlinx.coroutines.delay(100)
            ItunesResponseDto(0, emptyList())
        }
        
        // Start load requests
        launch { repository.loadNextPage("jack") }
        launch { repository.loadNextPage("jack") }
        
        advanceUntilIdle()

        // 1 call from refreshSearch, 1 call from the first loadNextPage. 
        // The second loadNextPage should do nothing.
        coVerify(exactly = 2) { apiService.search(term = "jack", offset = 0) }
    }
}
