package com.keypadds.phonechallenge.domain.usecase

import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all songs that belong to a specific album.
 * 
 * Isolates the logic of fetching an album's tracklist from the UI layer, delegating
 * the data retrieval to the underlying [SongRepository].
 * 
 * **Edge Cases:**
 * - If the `collectionId` is invalid or refers to an album not present in the local database,
 *   the returned Flow will emit an empty list.
 * - This use case does not trigger network requests; it relies entirely on the local cache
 *   populated by previous searches. Therefore, if the album wasn't fetched during a search,
 *   it won't appear here.
 */
class GetAlbumSongsUseCase @Inject constructor(private val repository: SongRepository) {
    /**
     * @param collectionId The unique identifier of the album.
     * @return A [Flow] emitting the list of songs in the album.
     */
    operator fun invoke(collectionId: Long): Flow<List<Song>> =
        repository.getAlbumSongs(collectionId)
}
