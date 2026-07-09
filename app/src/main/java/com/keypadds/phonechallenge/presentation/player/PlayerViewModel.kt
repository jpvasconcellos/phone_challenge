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

    val playbackState: StateFlow<PlaybackState> = musicPlayer.playbackState

    init {
        if (trackId != -1L) {
            viewModelScope.launch {
                recentSongRepository.markAsPlayed(trackId)
            }
            viewModelScope.launch {
                songRepository.getSongById(trackId).collectLatest { loadedSong ->
                    _song.value = loadedSong
                }
            }
            if (previewUrl != null) {
                play()
            }
        }
    }

    fun play() {
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
}
