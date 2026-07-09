package com.keypadds.phonechallenge.domain.usecase

import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

/**
 * Use case for searching songs based on a text query.
 * 
 * Isolates the search logic from the UI layer, preventing invalid searches and
 * delegating data retrieval to the underlying [SongRepository].
 * 
 * **Edge Cases:**
 * - If the `query` is blank or empty, the use case immediately returns an empty flow
 *   without contacting the repository. This prevents unnecessary database lookups or network calls.
 * - The returned flow initially emits local database results and subsequently updates
 *   when network responses are cached.
 */
class SearchSongsUseCase @Inject constructor(private val repository: SongRepository) {
    /**
     * @param query The search text (e.g., artist name, song title).
     * @return A [Flow] emitting the list of matching songs, or an empty flow if the query is blank.
     */
    operator fun invoke(query: String): Flow<List<Song>> {
        if (query.isBlank()) return emptyFlow()
        return repository.searchSongs(query)
    }
}
