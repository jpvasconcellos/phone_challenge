package com.keypadds.phonechallenge.presentation.songs

import app.cash.turbine.test
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.SongRepository
import com.keypadds.phonechallenge.domain.usecase.SearchSongsUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SongsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var searchSongsUseCase: SearchSongsUseCase
    private lateinit var songRepository: SongRepository
    private lateinit var viewModel: SongsViewModel

    private val song1 = Song(1L, 10L, "jack", "Upside Down", "Jack Johnson", "", "", 1, 1000L)
    private val song2 = Song(2L, 10L, "jack", "Better Together", "Jack Johnson", "", "", 2, 1000L)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchSongsUseCase = mockk()
        songRepository = mockk(relaxed = true)
        viewModel = SongsViewModel(searchSongsUseCase, songRepository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun search_emits_loading_then_results_on_success() = runTest(testDispatcher) {
        every { searchSongsUseCase("jack") } returns flowOf(listOf(song1, song2))

        viewModel.isLoading.test {
            assertFalse(awaitItem()) // initial: not loading

            viewModel.search("jack")
            assertTrue(awaitItem())  // set to true immediately

            advanceUntilIdle()
            assertFalse(awaitItem()) // set to false after collecting
        }

        assertEquals(listOf(song1, song2), viewModel.songs.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun search_clears_results_on_new_query() = runTest(testDispatcher) {
        every { searchSongsUseCase("jack") } returns flowOf(listOf(song1, song2))
        every { searchSongsUseCase("adele") } returns flowOf(emptyList())

        viewModel.search("jack")
        advanceUntilIdle()
        assertEquals(2, viewModel.songs.value.size)

        viewModel.search("adele")
        // Songs should be cleared immediately when new query starts
        assertEquals(emptyList<Song>(), viewModel.songs.value)

        advanceUntilIdle()
        assertEquals(emptyList<Song>(), viewModel.songs.value)
    }

    @Test
    fun load_next_page_delegates_to_repository() = runTest(testDispatcher) {
        every { searchSongsUseCase("jack") } returns flowOf(listOf(song1))

        // Must search first to set currentQuery
        viewModel.search("jack")
        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        coVerify { songRepository.loadNextPage("jack") }
    }
}
