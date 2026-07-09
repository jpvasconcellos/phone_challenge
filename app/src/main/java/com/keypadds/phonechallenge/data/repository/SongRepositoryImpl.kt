package com.keypadds.phonechallenge.data.repository

import com.keypadds.phonechallenge.data.local.SongDao
import com.keypadds.phonechallenge.data.local.mapper.toDomainModel
import com.keypadds.phonechallenge.data.remote.ItunesApiService
import com.keypadds.phonechallenge.data.remote.mapper.toSongEntity
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.SongRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SongRepositoryImpl(
    private val apiService: ItunesApiService,
    private val dao: SongDao
) : SongRepository {

    private var currentQuery: String? = null
    private var currentOffset: Int = 0
    private var isLoading = false
    private val mutex = Mutex()

    override fun searchSongs(query: String): Flow<List<Song>> {
        // Trigger network refresh in background
        CoroutineScope(Dispatchers.IO).launch {
            refreshSearch(query)
        }

        // Return cached data as Flow immediately
        return dao.getSongsByQuery(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getAlbumSongs(collectionId: Long): Flow<List<Song>> {
        return dao.getSongsByCollectionId(collectionId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun loadNextPage(query: String) {
        mutex.withLock {
            if (isLoading || currentQuery != query) return
            isLoading = true
        }

        try {
            val response = apiService.search(term = query, offset = currentOffset)
            val currentTime = System.currentTimeMillis()
            val entities = response.results.map { it.toSongEntity(query, currentTime) }

            if (entities.isNotEmpty()) {
                dao.insertSongs(entities)
                mutex.withLock {
                    currentOffset += entities.size
                }
            }
        } catch (_: Exception) {
            // Error handled gracefully, cached data remains untouched
        } finally {
            mutex.withLock {
                isLoading = false
            }
        }
    }

    override suspend fun refreshSearch(query: String) {
        mutex.withLock {
            if (isLoading && currentQuery == query) return
            isLoading = true
            currentQuery = query
            currentOffset = 0
        }

        try {
            val response = apiService.search(term = query, offset = 0)
            val currentTime = System.currentTimeMillis()
            val entities = response.results.map { it.toSongEntity(query, currentTime) }

            // Delete old cache and insert new results on successful network call
            dao.deleteSongsByQuery(query)
            if (entities.isNotEmpty()) {
                dao.insertSongs(entities)
                mutex.withLock {
                    currentOffset = entities.size
                }
            } else {
                mutex.withLock {
                    currentOffset = 0
                }
            }
        } catch (_: Exception) {
            // Network failure ignored; keep cached data
        } finally {
            mutex.withLock {
                isLoading = false
            }
        }
    }
}
