package com.keypadds.phonechallenge.player

data class PlaybackState(
    val trackId: Long = -1L,
    val isPlaying: Boolean = false,
    val durationMs: Long = 0L,
    val isLooping: Boolean = false
)
