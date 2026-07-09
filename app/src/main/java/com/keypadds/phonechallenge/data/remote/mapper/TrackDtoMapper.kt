package com.keypadds.phonechallenge.data.remote.mapper

import com.keypadds.phonechallenge.data.local.SongEntity
import com.keypadds.phonechallenge.data.remote.dto.TrackDto

fun TrackDto.toSongEntity(query: String, lastFetched: Long): SongEntity {
    return SongEntity(
        trackId = trackId ?: 0L,
        collectionId = collectionId ?: 0L,
        collectionName = collectionName ?: "",
        query = query,
        trackName = trackName ?: "",
        artistName = artistName ?: "",
        previewUrl = previewUrl ?: "",
        artworkUrl = artworkUrl100 ?: "",
        trackNumber = trackNumber ?: 0,
        lastFetched = lastFetched
    )
}
