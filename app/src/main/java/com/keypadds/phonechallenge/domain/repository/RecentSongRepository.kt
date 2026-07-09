package com.keypadds.phonechallenge.domain.repository

import com.keypadds.phonechallenge.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface RecentSongRepository {
    fun getRecentSongs(): Flow<List<Song>>
    suspend fun markAsPlayed(trackId: Long)
}
