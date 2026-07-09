package com.keypadds.phonechallenge.presentation.songs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.ui.theme.PhoneChallengeTheme
import com.keypadds.phonechallenge.ui.theme.appColors

@Composable
fun SongListItem(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.appColors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Artwork thumbnail
        AsyncImage(
            model = song.artworkUrl,
            contentDescription = "Album art for ${song.trackName}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(colors.surfaceLight)
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Track info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.trackName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artistName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    color = colors.textSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewSong = Song(
    trackId = 1L,
    collectionId = 10L,
    query = "jack johnson",
    trackName = "Upside Down",
    artistName = "Jack Johnson",
    previewUrl = "",
    artworkUrl = "",
    trackNumber = 1,
    lastFetched = 0L
)

@Preview(showBackground = true, backgroundColor = 0xFF0D0D0D, name = "SongListItem")
@Composable
private fun SongListItemPreview() {
    PhoneChallengeTheme {
        SongListItem(song = previewSong, onClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D0D, name = "SongListItem — long title")
@Composable
private fun SongListItemLongTitlePreview() {
    PhoneChallengeTheme {
        SongListItem(
            song = previewSong.copy(
                trackName = "A Very Long Song Title That Should Be Ellipsized On One Line",
                artistName = "An Extremely Verbose Artist Name feat. Another Artist"
            ),
            onClick = {}
        )
    }
}
