package com.keypadds.phonechallenge.domain.usecase

import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumSongsUseCase @Inject constructor(private val repository: SongRepository) {
    operator fun invoke(collectionId: Long): Flow<List<Song>> =
        repository.getAlbumSongs(collectionId)
}
