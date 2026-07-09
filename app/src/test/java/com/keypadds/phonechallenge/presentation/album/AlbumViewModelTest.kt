package com.keypadds.phonechallenge.presentation.album

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.usecase.GetAlbumSongsUseCase
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getAlbumSongsUseCase: GetAlbumSongsUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    private val song1 = Song(1L, 10L, "Album Title", "jack", "Track 1", "Artist", "", "", 1, 1000L)
    private val song2 = Song(2L, 10L, "Album Title", "jack", "Track 2", "Artist", "", "", 2, 1000L)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getAlbumSongsUseCase = mockk()
        savedStateHandle = SavedStateHandle(mapOf("collectionId" to 10L))
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun album_songs_emits_filtered_list_by_collection_id() = runTest(testDispatcher) {
        every { getAlbumSongsUseCase(10L) } returns flowOf(listOf(song1, song2))

        val viewModel = AlbumViewModel(getAlbumSongsUseCase, savedStateHandle)
        advanceUntilIdle()

        viewModel.songs.test {
            assertEquals(listOf(song1, song2), awaitItem())
        }
    }

    @Test
    fun empty_album_shows_empty_state() = runTest(testDispatcher) {
        every { getAlbumSongsUseCase(10L) } returns flowOf(emptyList())

        val viewModel = AlbumViewModel(getAlbumSongsUseCase, savedStateHandle)
        advanceUntilIdle()

        viewModel.songs.test {
            assertEquals(emptyList<Song>(), awaitItem())
        }
    }
}
