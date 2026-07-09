package com.keypadds.phonechallenge.presentation.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.keypadds.phonechallenge.ui.theme.PhoneChallengeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongsFragment : Fragment() {

    private val viewModel: SongsViewModel by viewModels()

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

            PhoneChallengeTheme {
                SongsScreen(
                    songs = songs,
                    isLoading = isLoading,
                    error = error,
                    onSearch = { query -> viewModel.search(query) },
                    onSongClick = { song ->
                        val action = SongsFragmentDirections
                            .actionSongsToPlayer(
                                trackId = song.trackId,
                                previewUrl = song.previewUrl
                            )
                        findNavController().navigate(action)
                    },
                    onLoadMore = { viewModel.loadNextPage() }
                )
            }
        }
    }
}
