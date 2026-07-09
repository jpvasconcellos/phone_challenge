package com.keypadds.phonechallenge.domain.repository

import com.keypadds.phonechallenge.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun searchSongs(query: String): Flow<List<Song>>
    fun getAlbumSongs(collectionId: Long): Flow<List<Song>>
    fun getSongById(trackId: Long): Flow<Song?>
    suspend fun loadNextPage(query: String)
    suspend fun refreshSearch(query: String)
}
