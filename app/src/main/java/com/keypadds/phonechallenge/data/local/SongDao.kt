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

    @Query("SELECT * FROM songs WHERE `query` = :query ORDER BY lastFetched ASC")
    fun getSongsByQuery(query: String): Flow<List<SongEntity>>

    @Query("DELETE FROM songs WHERE `query` = :query")
    suspend fun deleteSongsByQuery(query: String)

    @androidx.room.Transaction
    suspend fun replaceSongsForQuery(query: String, songs: List<SongEntity>) {
        deleteSongsByQuery(query)
        if (songs.isNotEmpty()) {
            insertSongs(songs)
        }
    }

    @Query("SELECT trackId FROM songs WHERE `query` = :query")
    suspend fun getTrackIdsByQuery(query: String): List<Long>

    @Query("DELETE FROM songs WHERE `query` != '' AND `query` NOT LIKE 'album-%' AND trackId NOT IN (SELECT trackId FROM recent_songs)")
    suspend fun clearSearchCache()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSong(recentSong: RecentSongEntity)

    @Query("""
        SELECT s.* FROM songs s
        INNER JOIN recent_songs rs ON s.trackId = rs.trackId
        ORDER BY rs.playedAt DESC
    """)
    fun getRecentSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE collectionId = :collectionId ORDER BY trackNumber ASC")
    fun getSongsByCollectionId(collectionId: Long): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE trackId = :trackId LIMIT 1")
    fun getSongById(trackId: Long): Flow<SongEntity?>
}
