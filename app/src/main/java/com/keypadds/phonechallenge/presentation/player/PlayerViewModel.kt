package com.keypadds.phonechallenge.presentation.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.RecentSongRepository
import com.keypadds.phonechallenge.domain.repository.SongRepository
import com.keypadds.phonechallenge.player.MusicPlayer
import com.keypadds.phonechallenge.player.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicPlayer: MusicPlayer,
    private val recentSongRepository: RecentSongRepository,
    private val songRepository: SongRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val trackId: Long = savedStateHandle.get<Long>("trackId") ?: -1L
    private val previewUrl: String? = savedStateHandle.get<String>("previewUrl")

    private val _song = MutableStateFlow<Song?>(null)
    val song: StateFlow<Song?> = _song.asStateFlow()

    private var recentSongsList: List<Song> = emptyList()

    val playbackState: StateFlow<PlaybackState> = musicPlayer.playbackState

    init {
        // Observe playback state to always show the currently playing song
        viewModelScope.launch {
            musicPlayer.playbackState.collectLatest { state ->
                if (state.trackId != -1L) {
                    songRepository.getSongById(state.trackId).collectLatest { loadedSong ->
                        _song.value = loadedSong
                    }
                }
            }
        }

        // Track recent songs for skip controls
        viewModelScope.launch {
            recentSongRepository.getRecentSongs().collectLatest { songs ->
                recentSongsList = songs
            }
        }

        // Play the requested track if passed from arguments
        if (trackId != -1L) {
            viewModelScope.launch {
                recentSongRepository.markAsPlayed(trackId)
            }
            if (previewUrl != null) {
                playInitial()
            }
        }
    }

    private fun playInitial() {
        if (previewUrl != null) {
            musicPlayer.play(previewUrl, trackId)
        }
    }

    fun pause() {
        musicPlayer.pause()
    }

    fun resume() {
        musicPlayer.resume()
    }

    fun skipNext() {
        val currentTrackId = playbackState.value.trackId
        if (currentTrackId == -1L || recentSongsList.isEmpty()) return

        val currentIndex = recentSongsList.indexOfFirst { it.trackId == currentTrackId }
        if (currentIndex != -1 && currentIndex > 0) {
            // Play the newer song in history
            val nextSong = recentSongsList[currentIndex - 1]
            musicPlayer.play(nextSong.previewUrl, nextSong.trackId)
            viewModelScope.launch { recentSongRepository.markAsPlayed(nextSong.trackId) }
        } else if (currentIndex == -1 && recentSongsList.isNotEmpty()) {
            val nextSong = recentSongsList.first()
            musicPlayer.play(nextSong.previewUrl, nextSong.trackId)
            viewModelScope.launch { recentSongRepository.markAsPlayed(nextSong.trackId) }
        }
    }

    fun skipPrevious() {
        val currentTrackId = playbackState.value.trackId
        if (currentTrackId == -1L || recentSongsList.isEmpty()) return

        val currentIndex = recentSongsList.indexOfFirst { it.trackId == currentTrackId }
        if (currentIndex != -1 && currentIndex < recentSongsList.lastIndex) {
            // Play the older song in history
            val prevSong = recentSongsList[currentIndex + 1]
            musicPlayer.play(prevSong.previewUrl, prevSong.trackId)
            viewModelScope.launch { recentSongRepository.markAsPlayed(prevSong.trackId) }
        } else if (currentIndex == -1 && recentSongsList.isNotEmpty()) {
            val prevSong = recentSongsList.first()
            musicPlayer.play(prevSong.previewUrl, prevSong.trackId)
            viewModelScope.launch { recentSongRepository.markAsPlayed(prevSong.trackId) }
        }
    }

    fun toggleLoop() {
        musicPlayer.toggleLoop()
    }
}
