package com.keypadds.phonechallenge.domain.usecase

import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.RecentSongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentSongsUseCase @Inject constructor(private val repository: RecentSongRepository) {
    operator fun invoke(): Flow<List<Song>> = repository.getRecentSongs()
}
