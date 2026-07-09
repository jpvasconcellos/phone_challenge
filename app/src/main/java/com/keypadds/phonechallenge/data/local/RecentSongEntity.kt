package com.keypadds.phonechallenge.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_songs")
data class RecentSongEntity(
    @PrimaryKey
    val trackId: Long,
    val playedAt: Long
)
