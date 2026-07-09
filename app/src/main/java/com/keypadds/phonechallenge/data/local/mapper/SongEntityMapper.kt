package com.keypadds.phonechallenge.data.local.mapper

import com.keypadds.phonechallenge.data.local.SongEntity
import com.keypadds.phonechallenge.domain.model.Song

fun SongEntity.toDomainModel(): Song {
    return Song(
        trackId = trackId,
        collectionId = collectionId,
        collectionName = collectionName,
        query = query,
        trackName = trackName,
        artistName = artistName,
        previewUrl = previewUrl,
        artworkUrl = artworkUrl,
        trackNumber = trackNumber,
        lastFetched = lastFetched
    )
}
