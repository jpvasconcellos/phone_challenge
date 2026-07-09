package com.keypadds.phonechallenge.data.repository

import app.cash.turbine.test
import com.keypadds.phonechallenge.data.local.RecentSongEntity
import com.keypadds.phonechallenge.data.local.SongDao
import com.keypadds.phonechallenge.data.local.SongEntity
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecentSongRepositoryImplTest {

    private lateinit var dao: SongDao
    private lateinit var repository: RecentSongRepositoryImpl

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = RecentSongRepositoryImpl(dao)
    }

    @Test
    fun get_recent_songs_emits_joined_songs_ordered_by_played_at() = runTest {
        val entity1 = SongEntity(
            trackId = 1L, collectionId = 10L, query = "jack", trackName = "Song A",
            artistName = "Artist", previewUrl = "", artworkUrl = "", trackNumber = 1, lastFetched = 1000L
        )
        val entity2 = SongEntity(
            trackId = 2L, collectionId = 10L, query = "jack", trackName = "Song B",
            artistName = "Artist", previewUrl = "", artworkUrl = "", trackNumber = 2, lastFetched = 1000L
        )
        // DAO returns entities pre-ordered by playedAt DESC (DB handles ordering)
        every { dao.getRecentSongs() } returns flowOf(listOf(entity2, entity1))

        repository.getRecentSongs().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals(2L, result[0].trackId) // most recently played first
            assertEquals(1L, result[1].trackId)
            awaitComplete()
        }
    }

    @Test
    fun mark_as_played_inserts_new_recent_entry() = runTest {
        repository.markAsPlayed(trackId = 42L)

        coVerify {
            dao.insertRecentSong(match { it.trackId == 42L && it.playedAt > 0L })
        }
    }

    @Test
    fun mark_as_played_updates_timestamp_if_already_exists() = runTest {
        val captured = mutableListOf<RecentSongEntity>()

        // Mark as played twice
        repository.markAsPlayed(trackId = 42L)
        repository.markAsPlayed(trackId = 42L)

        // DAO is called twice with REPLACE strategy — second call updates the timestamp
        coVerify(exactly = 2) {
            dao.insertRecentSong(capture(captured))
        }
        assertEquals(2, captured.size)
        assertEquals(42L, captured[0].trackId)
        assertEquals(42L, captured[1].trackId)
    }
}
