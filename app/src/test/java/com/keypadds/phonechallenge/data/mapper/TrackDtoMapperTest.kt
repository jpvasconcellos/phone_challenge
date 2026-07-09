package com.keypadds.phonechallenge.data.mapper

import com.keypadds.phonechallenge.data.local.SongEntity
import com.keypadds.phonechallenge.data.local.mapper.toDomainModel
import com.keypadds.phonechallenge.data.remote.dto.TrackDto
import com.keypadds.phonechallenge.data.remote.mapper.toSongEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class TrackDtoMapperTest {

    @Test
    fun maps_track_dto_to_song_entity_correctly() {
        val dto = TrackDto(
            trackId = 1L,
            collectionId = 2L,
            collectionName = "Album Title",
            trackName = "Test Track",
            artistName = "Test Artist",
            previewUrl = "preview_url",
            artworkUrl100 = "artwork_url",
            trackNumber = 1,
            wrapperType = "track"
        )

        val entity = dto.toSongEntity(query = "test", lastFetched = 1000L)

        assertEquals(1L, entity.trackId)
        assertEquals(2L, entity.collectionId)
        assertEquals("test", entity.query)
        assertEquals("Test Track", entity.trackName)
        assertEquals("Test Artist", entity.artistName)
        assertEquals("preview_url", entity.previewUrl)
        assertEquals("artwork_url", entity.artworkUrl)
        assertEquals(1, entity.trackNumber)
        assertEquals(1000L, entity.lastFetched)
    }

    @Test
    fun maps_song_entity_to_domain_song_correctly() {
        val entity = SongEntity(
            trackId = 1L,
            collectionId = 2L, collectionName = "Album Title",
            query = "test",
            trackName = "Test Track",
            artistName = "Test Artist",
            previewUrl = "preview_url",
            artworkUrl = "artwork_url",
            trackNumber = 1,
            lastFetched = 1000L
        )

        val domain = entity.toDomainModel()

        assertEquals(1L, domain.trackId)
        assertEquals(2L, domain.collectionId)
        assertEquals("test", domain.query)
        assertEquals("Test Track", domain.trackName)
        assertEquals("Test Artist", domain.artistName)
        assertEquals("preview_url", domain.previewUrl)
        assertEquals("artwork_url", domain.artworkUrl)
        assertEquals(1, domain.trackNumber)
        assertEquals(1000L, domain.lastFetched)
    }
}
