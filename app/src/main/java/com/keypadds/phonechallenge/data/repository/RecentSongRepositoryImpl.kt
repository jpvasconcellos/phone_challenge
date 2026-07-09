package com.keypadds.phonechallenge.data.repository

import com.keypadds.phonechallenge.data.local.RecentSongEntity
import com.keypadds.phonechallenge.data.local.SongDao
import com.keypadds.phonechallenge.data.local.mapper.toDomainModel
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.RecentSongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecentSongRepositoryImpl(
    private val dao: SongDao
) : RecentSongRepository {

    override fun getRecentSongs(): Flow<List<Song>> {
        return dao.getRecentSongs().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun markAsPlayed(trackId: Long) {
        val playedAt = System.currentTimeMillis()
        dao.insertRecentSong(RecentSongEntity(trackId = trackId, playedAt = playedAt))
    }
}
