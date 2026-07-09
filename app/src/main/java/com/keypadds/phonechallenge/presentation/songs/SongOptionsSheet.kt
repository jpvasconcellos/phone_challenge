package com.keypadds.phonechallenge.presentation.songs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.ui.theme.PhoneChallengeTheme
import com.keypadds.phonechallenge.ui.theme.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongOptionsSheet(
    song: Song,
    onDismissRequest: () -> Unit,
    onViewAlbumClick: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val colors = MaterialTheme.appColors

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = colors.surfaceLight,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = song.trackName,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = song.artistName,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewAlbumClick() }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LibraryMusic,
                    contentDescription = "View album",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "View album",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "SongOptionsSheet")
@Composable
private fun SongOptionsSheetPreview() {
    PhoneChallengeTheme {
        SongOptionsSheet(
            song = Song(
                trackId = 1L,
                collectionId = 10L,
                query = "jack",
                trackName = "Song name",
                artistName = "Artist name",
                previewUrl = "",
                artworkUrl = "",
                trackNumber = 1,
                lastFetched = 0L
            ),
            onDismissRequest = {},
            onViewAlbumClick = {}
        )
    }
}
