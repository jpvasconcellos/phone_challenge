package com.keypadds.phonechallenge.domain.repository

import com.keypadds.phonechallenge.domain.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing [Song] data.
 * 
 * Provides an offline-first mechanism for searching, fetching, and paginating songs.
 * Exposes data as reactive Streams (Flow) so the UI can observe database changes directly.
 */
interface SongRepository {
    
    /**
     * Searches for songs by the given [query].
     *
     * Returns a [Flow] that emits the current list of matching songs from the local database.
     * The initial local query may be empty or incomplete until remote fetch completes.
     * Use [loadNextPage] and [refreshSearch] to trigger remote fetches.
     *
     * @param query The search term (e.g., artist or song name).
     * @return A reactive stream of the matching songs.
     */
    fun searchSongs(query: String): Flow<List<Song>>

    /**
     * Retrieves all songs belonging to a specific album.
     *
     * Returns a [Flow] that emits songs matching the given [collectionId].
     * This relies on local cache. Ensure the album's songs have been fetched prior
     * to observing this, usually achieved when searching.
     *
     * @param collectionId The unique identifier of the album/collection.
     * @return A reactive stream of the album's songs.
     */
    fun getAlbumSongs(collectionId: Long): Flow<List<Song>>

    /**
     * Retrieves a single song by its unique track ID.
     *
     * @param trackId The unique identifier of the song.
     * @return A reactive stream emitting the song if found, or null otherwise.
     */
    fun getSongById(trackId: Long): Flow<Song?>

    /**
     * Requests the next page of search results from the remote API for the specified [query].
     * 
     * This method fetches additional items from the network and saves them into the local database.
     * Once saved, the [Flow] returned by [searchSongs] will automatically emit the updated list.
     *
     * @param query The search term being paginated.
     */
    suspend fun loadNextPage(query: String)

    /**
     * Refreshes the search results for the specified [query] from the remote API.
     * 
     * Typically clears the local cache for this query and fetches the first page fresh.
     * Once saved, the [Flow] returned by [searchSongs] will automatically emit the new list.
     *
     * @param query The search term to refresh.
     */
    suspend fun refreshSearch(query: String)

    /**
     * Clears the local cache of search results.
     * This removes all songs that are not part of an album or recent songs.
     */
    suspend fun clearSearchCache()
}
