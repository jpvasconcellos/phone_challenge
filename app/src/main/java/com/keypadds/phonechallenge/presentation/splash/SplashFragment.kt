package com.keypadds.phonechallenge.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.keypadds.phonechallenge.R
import com.keypadds.phonechallenge.ui.theme.PhoneChallengeTheme

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PhoneChallengeTheme {
                    SplashScreen(
                        onTimeout = {
                            findNavController().navigate(R.id.action_splash_to_songs)
                        }
                    )
                }
            }
        }
    }
}
