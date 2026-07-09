package com.keypadds.phonechallenge.domain.usecase

import app.cash.turbine.test
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.RecentSongRepository
import com.keypadds.phonechallenge.domain.repository.SongRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetRecentAndAlbumUseCaseTest {

    private lateinit var songRepository: SongRepository
    private lateinit var recentSongRepository: RecentSongRepository
    private lateinit var getRecentSongsUseCase: GetRecentSongsUseCase
    private lateinit var getAlbumSongsUseCase: GetAlbumSongsUseCase

    private val song1 = Song(
        trackId = 1L, collectionId = 10L, collectionName = "Album Title", query = "jack",
        trackName = "Song A", artistName = "Artist", previewUrl = "",
        artworkUrl = "", trackNumber = 1, lastFetched = 1000L
    )
    private val song2 = Song(
        trackId = 2L, collectionId = 10L, collectionName = "Album Title", query = "jack",
        trackName = "Song B", artistName = "Artist", previewUrl = "",
        artworkUrl = "", trackNumber = 2, lastFetched = 1000L
    )

    @Before
    fun setup() {
        songRepository = mockk()
        recentSongRepository = mockk()
        getRecentSongsUseCase = GetRecentSongsUseCase(recentSongRepository)
        getAlbumSongsUseCase = GetAlbumSongsUseCase(songRepository)
    }

    @Test
    fun get_recent_songs_returns_flow_from_repository() = runTest {
        every { recentSongRepository.getRecentSongs() } returns flowOf(listOf(song2, song1))

        getRecentSongsUseCase().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals(2L, result[0].trackId)
            assertEquals(1L, result[1].trackId)
            awaitComplete()
        }
    }

    @Suppress("UNUSED_EXPRESSION")
    @Test
    fun get_album_songs_filters_by_collection_id() = runTest {
        every { songRepository.getAlbumSongs(10L) } returns flowOf(listOf(song1, song2))

        getAlbumSongsUseCase(10L).test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.collectionId == 10L })
            awaitComplete()
        }
    }

    @Suppress("UNUSED_EXPRESSION")
    @Test
    fun get_album_songs_returns_empty_when_no_match() = runTest {
        every { songRepository.getAlbumSongs(999L) } returns flowOf(emptyList())

        getAlbumSongsUseCase(999L).test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            awaitComplete()
        }
    }
}
