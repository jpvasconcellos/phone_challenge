package com.keypadds.phonechallenge.data.remote

import com.keypadds.phonechallenge.data.remote.dto.ItunesResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("search")
    suspend fun search(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): ItunesResponseDto
}
