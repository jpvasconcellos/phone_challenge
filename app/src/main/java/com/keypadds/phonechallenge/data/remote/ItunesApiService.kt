package com.keypadds.phonechallenge.data.remote

import com.keypadds.phonechallenge.data.remote.dto.ItunesResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("search")
    suspend fun search(
        @Query("term") term: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 20
    ): ItunesResponseDto

    @GET("lookup")
    suspend fun lookupAlbumSongs(
        @Query("id") collectionId: Long,
        @Query("entity") entity: String = "song"
    ): ItunesResponseDto
}
