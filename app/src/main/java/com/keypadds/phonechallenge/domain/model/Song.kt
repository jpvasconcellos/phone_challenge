package com.keypadds.phonechallenge.domain.model

data class Song(
    val trackId: Long,
    val collectionId: Long,
    val query: String,
    val trackName: String,
    val artistName: String,
    val previewUrl: String,
    val artworkUrl: String,
    val trackNumber: Int,
    val lastFetched: Long
)
