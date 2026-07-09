package com.keypadds.phonechallenge.presentation.player

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
class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val song by viewModel.song.collectAsState()
            val playbackState by viewModel.playbackState.collectAsState()

            PhoneChallengeTheme {
                PlayerScreen(
                    song = song,
                    playbackState = playbackState,
                    onBack = { findNavController().popBackStack() },
                    onPlay = { viewModel.play() },
                    onPause = { viewModel.pause() },
                    onAlbumClick = {
                        song?.collectionId?.let { collectionId ->
                            val action = PlayerFragmentDirections
                                .actionPlayerToAlbum(collectionId)
                            findNavController().navigate(action)
                        }
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resume()
    }
}
