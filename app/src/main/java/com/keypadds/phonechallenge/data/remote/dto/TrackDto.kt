package com.keypadds.phonechallenge.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackDto(
    @param:Json(name = "trackId") val trackId: Long?,
    @param:Json(name = "collectionId") val collectionId: Long?,
    @param:Json(name = "collectionName") val collectionName: String?,
    @param:Json(name = "trackName") val trackName: String?,
    @param:Json(name = "artistName") val artistName: String?,
    @param:Json(name = "previewUrl") val previewUrl: String?,
    @param:Json(name = "artworkUrl100") val artworkUrl100: String?,
    @param:Json(name = "trackNumber") val trackNumber: Int?,
    @param:Json(name = "wrapperType") val wrapperType: String?,
    @param:Json(name = "kind") val kind: String?
)
