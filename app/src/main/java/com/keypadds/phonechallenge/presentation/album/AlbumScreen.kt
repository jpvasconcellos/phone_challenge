package com.keypadds.phonechallenge.presentation.album

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding().padding(top = 20.dp),
                title = { Text("Album", color = MaterialTheme.colorScheme.onBackground) },
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
            Column(modifier = Modifier.padding(paddingValues)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    itemsIndexed(songs, key = { _, song -> song.trackId }) { index, song ->
                        SongListItem(
                            song = song,
                            onClick = { onSongClick(song) },
                        )
                        if (index < songs.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 82.dp),
                                thickness = 0.5.dp,
                                color = colors.divider
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewSongs = listOf(
    Song(1L, 10L, "jack", "Upside Down", "Jack Johnson", "", "", 1, 0L),
    Song(2L, 10L, "jack", "Better Together", "Jack Johnson", "", "", 2, 0L),
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
