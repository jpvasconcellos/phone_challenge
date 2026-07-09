package com.keypadds.phonechallenge.presentation.album

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
class AlbumFragment : Fragment() {

    private val viewModel: AlbumViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val songs by viewModel.songs.collectAsState()

            PhoneChallengeTheme {
                AlbumScreen(
                    songs = songs,
                    onBackClick = { findNavController().popBackStack() },
                    onSongClick = { song ->
                        val action = AlbumFragmentDirections
                            .actionAlbumToPlayer(
                                trackId = song.trackId,
                                previewUrl = song.previewUrl
                            )
                        findNavController().navigate(action)
                    }
                )
            }
        }
    }
}
