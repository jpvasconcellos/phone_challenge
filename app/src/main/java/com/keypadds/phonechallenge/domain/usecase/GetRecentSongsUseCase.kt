package com.keypadds.phonechallenge.domain.usecase

import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.RecentSongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving the user's recently played songs history.
 * 
 * **Purpose:**
 * Isolates the logic of fetching playback history from the UI layer, delegating
 * the data retrieval to the underlying [RecentSongRepository].
 * 
 * **Edge Cases:**
 * - If the user has never played a song, the returned Flow will emit an empty list.
 * - The history is stored locally. If local app data is cleared, the history is lost.
 */
class GetRecentSongsUseCase @Inject constructor(private val repository: RecentSongRepository) {
    /**
     * @return A [Flow] emitting the list of recently played songs, ordered by most recent first.
     */
    operator fun invoke(): Flow<List<Song>> = repository.getRecentSongs()
}
