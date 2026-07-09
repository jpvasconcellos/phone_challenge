package com.keypadds.phonechallenge.domain.usecase

import app.cash.turbine.test
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.SongRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@Suppress("UnusedFlow")
class SearchSongsUseCaseTest {

    private lateinit var repository: SongRepository
    private lateinit var useCase: SearchSongsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SearchSongsUseCase(repository)
    }

    @Test
    fun invoke_delegates_to_repository_with_correct_query() = runTest {
        val query = "jack johnson"
        every { repository.searchSongs(query) } returns flowOf(emptyList())

        useCase(query).test {
            awaitItem()
            awaitComplete()
        }

        verify(exactly = 1) { repository.searchSongs(query) }
    }

    @Test
    fun invoke_returns_flow_from_repository() = runTest {
        val songs = listOf(
            Song(
                trackId = 1L, collectionId = 10L, query = "jack",
                trackName = "Upside Down", artistName = "Jack Johnson",
                previewUrl = "", artworkUrl = "", trackNumber = 1, lastFetched = 1000L
            )
        )
        every { repository.searchSongs("jack") } returns flowOf(songs)

        useCase("jack").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Upside Down", result[0].trackName)
            awaitComplete()
        }
    }

    @Test
    fun empty_query_returns_empty_flow_without_hitting_repository() = runTest {
        useCase("   ").test {
            awaitComplete()
        }

        verify(exactly = 0) { repository.searchSongs(any()) }
    }
}
