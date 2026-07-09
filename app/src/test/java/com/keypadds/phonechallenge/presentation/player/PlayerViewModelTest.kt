package com.keypadds.phonechallenge.presentation.player

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.RecentSongRepository
import com.keypadds.phonechallenge.domain.repository.SongRepository
import com.keypadds.phonechallenge.player.MusicPlayer
import com.keypadds.phonechallenge.player.PlaybackState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class PlayerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var musicPlayer: MusicPlayer
    private lateinit var recentSongRepository: RecentSongRepository
    private lateinit var songRepository: SongRepository
    private lateinit var savedStateHandle: SavedStateHandle

    private val testSong = Song(1L, 10L, "jack", "Upside Down", "Jack Johnson", "http://preview.com", "", 1, 1000L)
    private val testSong2 = Song(2L, 10L, "jack", "Banana Pancakes", "Jack Johnson", "url2", "", 2, 1000L)
    private val testSong3 = Song(3L, 10L, "jack", "Better Together", "Jack Johnson", "url3", "", 3, 1000L)
    private val recentSongs = listOf(testSong3, testSong2, testSong)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        musicPlayer = mockk(relaxed = true)
        recentSongRepository = mockk(relaxed = true)
        songRepository = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle(mapOf("trackId" to 1L, "previewUrl" to "http://preview.com"))
        
        every { musicPlayer.playbackState } returns MutableStateFlow(PlaybackState(trackId = 1L))
        every { songRepository.getSongById(1L) } returns flowOf(testSong)
        every { recentSongRepository.getRecentSongs() } returns flowOf(recentSongs)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loads_song_and_marks_as_played() = runTest(testDispatcher) {
        val viewModel = PlayerViewModel(musicPlayer, recentSongRepository, songRepository, savedStateHandle)
        advanceUntilIdle()

        coVerify { recentSongRepository.markAsPlayed(1L) }
        verify { musicPlayer.play("http://preview.com", 1L) }
        assertEquals(testSong, viewModel.song.value)
    }



    @Test
    fun pause_delegates_to_music_player() = runTest(testDispatcher) {
        val viewModel = PlayerViewModel(musicPlayer, recentSongRepository, songRepository, savedStateHandle)
        
        viewModel.pause()

        verify { musicPlayer.pause() }
    }

    @Test
    fun resume_delegates_to_music_player() = runTest(testDispatcher) {
        val viewModel = PlayerViewModel(musicPlayer, recentSongRepository, songRepository, savedStateHandle)
        
        viewModel.resume()

        verify { musicPlayer.resume() }
    }

    @Test
    fun toggleLoop_delegates_to_music_player() = runTest(testDispatcher) {
        val viewModel = PlayerViewModel(musicPlayer, recentSongRepository, songRepository, savedStateHandle)
        
        viewModel.toggleLoop()

        verify { musicPlayer.toggleLoop() }
    }

    @Test
    fun playback_state_reflects_music_player_state() = runTest(testDispatcher) {
        val stateFlow = MutableStateFlow(PlaybackState())
        every { musicPlayer.playbackState } returns stateFlow

        val viewModel = PlayerViewModel(musicPlayer, recentSongRepository, songRepository, savedStateHandle)

        viewModel.playbackState.test {
            assertEquals(PlaybackState(), awaitItem())

            stateFlow.value = PlaybackState(trackId = 1L, isPlaying = true)
            assertEquals(PlaybackState(trackId = 1L, isPlaying = true), awaitItem())
        }
    }

    @Test
    fun skipNext_plays_next_song_in_list() = runTest(testDispatcher) {
        every { musicPlayer.playbackState } returns MutableStateFlow(PlaybackState(trackId = 2L)) // testSong2 (index 1)
        val viewModel = PlayerViewModel(musicPlayer, recentSongRepository, songRepository, savedStateHandle)
        advanceUntilIdle()

        viewModel.skipNext()
        advanceUntilIdle()

        // Index 1 plus 1 = 2 (testSong)
        verify { musicPlayer.play("http://preview.com", 1L) }
    }

    @Test
    fun skipPrevious_plays_previous_song_in_list() = runTest(testDispatcher) {
        every { musicPlayer.playbackState } returns MutableStateFlow(PlaybackState(trackId = 2L)) // testSong2 (index 1)
        val viewModel = PlayerViewModel(musicPlayer, recentSongRepository, songRepository, savedStateHandle)
        advanceUntilIdle()

        viewModel.skipPrevious()
        advanceUntilIdle()

        // Index 1 minus 1 = 0 (testSong3)
        verify { musicPlayer.play("url3", 3L) }
    }
}
