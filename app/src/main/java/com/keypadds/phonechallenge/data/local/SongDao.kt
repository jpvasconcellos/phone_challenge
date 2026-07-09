package com.keypadds.phonechallenge.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Query("SELECT * FROM songs WHERE `query` = :query ORDER BY trackNumber ASC")
    fun getSongsByQuery(query: String): Flow<List<SongEntity>>

    @Query("DELETE FROM songs WHERE `query` = :query")
    suspend fun deleteSongsByQuery(query: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSong(recentSong: RecentSongEntity)

    @Query("""
        SELECT s.* FROM songs s
        INNER JOIN recent_songs rs ON s.trackId = rs.trackId
        ORDER BY rs.playedAt DESC
    """)
    fun getRecentSongs(): Flow<List<SongEntity>>
}
