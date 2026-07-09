package com.keypadds.phonechallenge.presentation.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.SongRepository
import com.keypadds.phonechallenge.domain.usecase.SearchSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.keypadds.phonechallenge.domain.repository.RecentSongRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val searchSongsUseCase: SearchSongsUseCase,
    private val songRepository: SongRepository,
    recentSongRepository: RecentSongRepository
) : ViewModel() {

    val recentSongs: StateFlow<List<Song>> = recentSongRepository.getRecentSongs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentQuery: String = ""

    fun search(query: String) {
        if (query.isBlank()) return

        currentQuery = query
        _isLoading.value = true
        _error.value = null
        _songs.value = emptyList()

        viewModelScope.launch {
            searchSongsUseCase(query)
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { result ->
                    _songs.value = result
                    _isLoading.value = false
                }
        }
    }

    fun loadNextPage() {
        if (currentQuery.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                songRepository.loadNextPage(currentQuery)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSearchCache() {
        viewModelScope.launch {
            songRepository.clearSearchCache()
            _songs.value = emptyList()
            currentQuery = ""
        }
    }
}
