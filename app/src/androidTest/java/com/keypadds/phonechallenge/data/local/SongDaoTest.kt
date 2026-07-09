package com.keypadds.phonechallenge.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SongDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var songDao: SongDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        songDao = database.songDao()
    }

    @After
    fun teardown() {
        if (::database.isInitialized) {
            database.close()
        }
    }

    @Test
    fun insert_and_get_songs_by_query_returns_correct_results() = runBlocking {
        val song = SongEntity(
            trackId = 1L,
            collectionId = 100L,
            query = "jack johnson",
            trackName = "Upside Down",
            artistName = "Jack Johnson",
            previewUrl = "url",
            artworkUrl = "url",
            trackNumber = 1,
            lastFetched = 1000L
        )

        songDao.insertSongs(listOf(song))
        val result = songDao.getSongsByQuery("jack johnson").first()
        
        assertEquals(1, result.size)
        assertEquals("Upside Down", result[0].trackName)
    }

    @Test
    fun insert_songs_replaces_on_conflict() = runBlocking {
        val song1 = SongEntity(1L, 100L, "query", "Name1", "Artist", "url", "url", 1, 1000L)
        val song2 = SongEntity(1L, 100L, "query", "Name2", "Artist", "url", "url", 1, 1000L)

        songDao.insertSongs(listOf(song1))
        songDao.insertSongs(listOf(song2))

        val result = songDao.getSongsByQuery("query").first()
        assertEquals(1, result.size)
        assertEquals("Name2", result[0].trackName)
    }

    @Test
    fun delete_songs_by_query_removes_all_matching_rows() = runBlocking {
        val song = SongEntity(1L, 100L, "query", "Name1", "Artist", "url", "url", 1, 1000L)
        songDao.insertSongs(listOf(song))
        
        songDao.deleteSongsByQuery("query")
        
        val result = songDao.getSongsByQuery("query").first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun get_recent_songs_returns_songs_ordered_by_played_at_desc() = runBlocking {
        val song1 = SongEntity(1L, 100L, "query", "Name1", "Artist", "url", "url", 1, 1000L)
        val song2 = SongEntity(2L, 100L, "query", "Name2", "Artist", "url", "url", 2, 1000L)
        songDao.insertSongs(listOf(song1, song2))

        songDao.insertRecentSong(RecentSongEntity(1L, 1000L))
        songDao.insertRecentSong(RecentSongEntity(2L, 2000L)) // 2L was played more recently

        val recentSongs = songDao.getRecentSongs().first()
        assertEquals(2, recentSongs.size)
        assertEquals(2L, recentSongs[0].trackId) // most recent first
        assertEquals(1L, recentSongs[1].trackId)
    }

    @Test
    fun mark_as_played_inserts_or_updates_recent_song() = runBlocking {
        val song1 = SongEntity(1L, 100L, "query", "Name1", "Artist", "url", "url", 1, 1000L)
        songDao.insertSongs(listOf(song1))

        songDao.insertRecentSong(RecentSongEntity(1L, 1000L))
        songDao.insertRecentSong(RecentSongEntity(1L, 2000L))

        val recentSongs = songDao.getRecentSongs().first()
        assertEquals(1, recentSongs.size)
    }
}
