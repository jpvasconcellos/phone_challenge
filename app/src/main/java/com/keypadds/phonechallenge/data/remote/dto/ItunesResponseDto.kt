package com.keypadds.phonechallenge.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ItunesResponseDto(
    @param:Json(name = "resultCount") val resultCount: Int,
    @param:Json(name = "results") val results: List<TrackDto>
)
