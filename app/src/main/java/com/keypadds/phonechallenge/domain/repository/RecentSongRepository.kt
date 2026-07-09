package com.keypadds.phonechallenge.domain.repository

import com.keypadds.phonechallenge.domain.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing recently played [Song]s.
 * 
 * Provides an offline-first mechanism for tracking the user's playback history.
 */
interface RecentSongRepository {
    
    /**
     * Retrieves the list of recently played songs.
     *
     * @return A reactive stream of recently played songs.
     */
    fun getRecentSongs(): Flow<List<Song>>

    /**
     * Marks a specific song as played by its track ID.
     *
     * This will add the song to the recent history, or update its timestamp if it was already played,
     * moving it to the top of the recently played list.
     *
     * @param trackId The unique identifier of the song to mark as played.
     */
    suspend fun markAsPlayed(trackId: Long)
}
