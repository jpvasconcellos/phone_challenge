package com.keypadds.phonechallenge.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class MusicPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context).build().also { player ->
            player.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = isPlaying,
                        currentPositionMs = player.currentPosition,
                        durationMs = player.duration.coerceAtLeast(0L)
                    )
                    if (isPlaying) {
                        startProgressPolling()
                    } else {
                        stopProgressPolling()
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _playbackState.value = _playbackState.value.copy(
                            durationMs = player.duration.coerceAtLeast(0L),
                            currentPositionMs = player.currentPosition
                        )
                    }
                    if (playbackState == Player.STATE_ENDED) {
                        _playbackState.value = _playbackState.value.copy(
                            isPlaying = false,
                            currentPositionMs = 0L
                        )
                        stopProgressPolling()
                    }
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    _playbackState.value = _playbackState.value.copy(
                        isLooping = (repeatMode == Player.REPEAT_MODE_ONE)
                    )
                }
            })
        }
    }

    private val playerScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    /**
     * Start playback of the given URL, updating the current trackId.
     * If the same track is already loaded and paused, it resumes instead.
     */
    fun play(url: String, trackId: Long) {
        if (_playbackState.value.trackId == trackId && exoPlayer.playbackState != Player.STATE_IDLE) {
            exoPlayer.play()
            return
        }

        _playbackState.value = PlaybackState(trackId = trackId)
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
        stopProgressPolling()
    }

    fun resume() {
        exoPlayer.play()
        startProgressPolling()
    }

    fun toggleLoop() {
        val newMode = if (exoPlayer.repeatMode == Player.REPEAT_MODE_ONE) {
            Player.REPEAT_MODE_OFF
        } else {
            Player.REPEAT_MODE_ONE
        }
        exoPlayer.repeatMode = newMode
    }

    private fun startProgressPolling() {
        progressJob?.cancel()
        progressJob = playerScope.launch {
            while (true) {
                _playbackState.value = _playbackState.value.copy(
                    currentPositionMs = exoPlayer.currentPosition
                )
                delay(100L.milliseconds) // Poll every 100ms
            }
        }
    }

    private fun stopProgressPolling() {
        progressJob?.cancel()
        progressJob = null
    }

    /**
     * Release the underlying ExoPlayer. Should be called when the host
     * (Application / Activity) is being destroyed permanently.
     */
    fun release() {
        exoPlayer.release()
        _playbackState.value = PlaybackState()
    }
}
