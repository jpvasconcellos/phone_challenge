package com.keypadds.phonechallenge.presentation.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.usecase.GetAlbumSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val getAlbumSongsUseCase: GetAlbumSongsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val collectionId: Long = savedStateHandle.get<Long>("collectionId") ?: -1L

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    init {
        if (collectionId != -1L) {
            viewModelScope.launch {
                getAlbumSongsUseCase(collectionId).collectLatest { loadedSongs ->
                    _songs.value = loadedSongs
                }
            }
        }
    }
}
