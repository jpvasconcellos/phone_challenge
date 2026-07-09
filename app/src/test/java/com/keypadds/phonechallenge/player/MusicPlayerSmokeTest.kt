package com.keypadds.phonechallenge.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Smoke tests for [PlaybackState] state transitions.
 *
 * NOTE: Full integration tests for [MusicPlayer] require an Android device/emulator
 * because ExoPlayer depends on the Android media framework. These unit tests verify
 * the [PlaybackState] default values and data class behavior in isolation.
 */
class MusicPlayerSmokeTest {

    @Test
    fun playback_state_default_values_are_correct() {
        val state = PlaybackState()
        assertEquals(-1L, state.trackId)
        assertFalse(state.isPlaying)
        assertEquals(0L, state.durationMs)
        assertFalse(state.isLooping)
    }

    @Test
    fun playback_state_copy_updates_correctly() {
        val initial = PlaybackState()
        val playing = initial.copy(trackId = 42L, isPlaying = true, durationMs = 30_000L, isLooping = true)

        assertEquals(42L, playing.trackId)
        assertEquals(true, playing.isPlaying)
        assertEquals(30_000L, playing.durationMs)
        assertEquals(true, playing.isLooping)
    }

    @Test
    fun playback_state_equality_works_correctly() {
        val state1 = PlaybackState(trackId = 1L, isPlaying = true, isLooping = true)
        val state2 = PlaybackState(trackId = 1L, isPlaying = true, isLooping = true)
        val state3 = PlaybackState(trackId = 2L, isPlaying = false, isLooping = false)

        assertEquals(state1, state2)
        assert(state1 != state3)
    }
}
