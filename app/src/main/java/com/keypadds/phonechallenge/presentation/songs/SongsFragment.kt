package com.keypadds.phonechallenge.presentation.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.ui.theme.PhoneChallengeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongsFragment : Fragment() {

    private val viewModel: SongsViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val songs by viewModel.songs.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val error by viewModel.error.collectAsState()
            val recentSongs by viewModel.recentSongs.collectAsState()

            var selectedSongForOptions by remember { mutableStateOf<Song?>(null) }

            PhoneChallengeTheme {
                SongsScreen(
                    songs = songs,
                    recentSongs = recentSongs,
                    isLoading = isLoading,
                    error = error,
                    onSearch = { query -> viewModel.search(query) },
                    onSongClick = { song ->
                        viewModel.clearSearchCache()
                        val action = SongsFragmentDirections
                            .actionSongsToPlayer(
                                trackId = song.trackId,
                                previewUrl = song.previewUrl
                            )
                        findNavController().navigate(action)
                    },
                    onOptionsClick = { song ->
                        selectedSongForOptions = song
                    },
                    onLoadMore = { viewModel.loadNextPage() }
                )

                selectedSongForOptions?.let { song ->
                    SongOptionsSheet(
                        song = song,
                        onDismissRequest = { selectedSongForOptions = null },
                        onViewAlbumClick = {
                            selectedSongForOptions = null
                            val action = SongsFragmentDirections
                                .actionSongsToAlbum(
                                    collectionId = song.collectionId
                                )
                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }
}
