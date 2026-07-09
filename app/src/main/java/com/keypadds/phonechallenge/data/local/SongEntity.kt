package com.keypadds.phonechallenge.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey
    val trackId: Long,
    val collectionId: Long,
    val collectionName: String,
    val query: String,
    val trackName: String,
    val artistName: String,
    val previewUrl: String,
    val artworkUrl: String,
    val trackNumber: Int,
    val lastFetched: Long
)
