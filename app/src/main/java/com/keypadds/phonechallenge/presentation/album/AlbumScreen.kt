package com.keypadds.phonechallenge.presentation.album

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.presentation.songs.SongListItem
import com.keypadds.phonechallenge.ui.theme.PhoneChallengeTheme
import com.keypadds.phonechallenge.ui.theme.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    songs: List<Song>,
    onBackClick: () -> Unit,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.appColors
    val albumTitle = songs.firstOrNull()?.collectionName?.takeIf { it.isNotBlank() } ?: "Album Title"

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding().padding(top = 20.dp),
                title = { Text(albumTitle, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.backgroundDark
                )
            )
        },
        containerColor = colors.backgroundDark,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        if (songs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No album info found.",
                    color = colors.textSecondary,
                    fontSize = 16.sp
                )
            }
        } else {
            val firstSong = songs.first()
            val configuration = LocalConfiguration.current
            val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(firstSong.artworkUrl.replace("100x100bb", "600x600bb"))
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }

                    // Right Column
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = firstSong.collectionName.ifBlank { "Unknown Album" },
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = firstSong.artistName,
                                    color = colors.textSecondary,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        itemsIndexed(songs, key = { _, song -> song.trackId }) { _, song ->
                            SongListItem(
                                song = song,
                                onClick = { onSongClick(song) },
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(firstSong.artworkUrl.replace("100x100bb", "600x600bb"))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = firstSong.collectionName.ifBlank { "Unknown Album" },
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = firstSong.artistName,
                                color = colors.textSecondary,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    itemsIndexed(songs, key = { _, song -> song.trackId }) { _, song ->
                        SongListItem(
                            song = song,
                            onClick = { onSongClick(song) },
                        )
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewSongs = listOf(
    Song(1L, 10L, "Album Title", "jack", "Around the World", "Daft Punk", "", "", 1, 0L),
    Song(2L, 10L, "Album Title", "jack", "Aerodynamic", "Daft Punk", "", "", 2, 0L),
)

@Preview(showBackground = true, showSystemUi = true, name = "AlbumScreen — Populated")
@Composable
private fun AlbumScreenPopulatedPreview() {
    PhoneChallengeTheme {
        AlbumScreen(
            songs = previewSongs,
            onBackClick = {},
            onSongClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "AlbumScreen — Empty")
@Composable
private fun AlbumScreenEmptyPreview() {
    PhoneChallengeTheme {
        AlbumScreen(
            songs = emptyList(),
            onBackClick = {},
            onSongClick = {}
        )
    }
}
