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
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
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
        // Trigger network refresh for the album in background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.lookupAlbumSongs(collectionId = collectionId)
                val currentTime = System.currentTimeMillis()
                val entities = response.results
                    .filter { it.kind == "song" }
                    .mapIndexed { index, dto -> 
                        // Use a dummy query to satisfy the schema, or rely on collectionId filtering
                        dto.toSongEntity(query = "album-$collectionId", lastFetched = currentTime, index = index) 
                    }
                if (entities.isNotEmpty()) {
                    dao.insertSongs(entities)
                }
            } catch (_: Exception) {
                // Ignore network failure, let Flow emit whatever is cached
            }
        }

        return dao.getSongsByCollectionId(collectionId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSongById(trackId: Long): Flow<Song?> {
        return dao.getSongById(trackId).map { it?.toDomainModel() }
    }

    override suspend fun loadNextPage(query: String) {
        mutex.withLock {
            if (isLoading || currentQuery != query) return
            isLoading = true
        }

        try {
            val newLimit = currentOffset + 20
            val response = apiService.search(term = query, limit = newLimit)
            val currentTime = System.currentTimeMillis()
            
            val existingIds = dao.getTrackIdsByQuery(query).toSet()
            val newEntities = response.results
                .filter { it.trackId != null && !existingIds.contains(it.trackId) }
                .mapIndexed { index, dto -> 
                    dto.toSongEntity(query, currentTime, index) 
                }

            if (newEntities.isNotEmpty()) {
                dao.insertSongs(newEntities)
            }
            
            mutex.withLock {
                // Update offset to the actual limit returned
                currentOffset = response.results.size
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
            val response = apiService.search(term = query, limit = 20)
            val currentTime = System.currentTimeMillis()
            val entities = response.results.mapIndexed { index, it -> 
                it.toSongEntity(query, currentTime, index) 
            }

            // Clear old search cache when starting a new search
            dao.clearSearchCache()
            
            if (entities.isNotEmpty()) {
                dao.insertSongs(entities)
                mutex.withLock {
                    currentOffset = response.results.size
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

    override suspend fun clearSearchCache() {
        dao.clearSearchCache()
    }
}
