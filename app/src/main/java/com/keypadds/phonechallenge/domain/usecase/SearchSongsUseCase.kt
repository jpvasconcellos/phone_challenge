package com.keypadds.phonechallenge.domain.usecase

import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class SearchSongsUseCase @Inject constructor(private val repository: SongRepository) {
    operator fun invoke(query: String): Flow<List<Song>> {
        if (query.isBlank()) return emptyFlow()
        return repository.searchSongs(query)
    }
}
