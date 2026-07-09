package com.keypadds.phonechallenge.presentation.songs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.ui.theme.PhoneChallengeTheme

private val BackgroundColor = Color(0xFF0D0D0D)
private val SurfaceColor = Color(0xFF1A1A1A)
private val AccentColor = Color(0xFF1DB954)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsScreen(
    songs: List<Song>,
    isLoading: Boolean,
    error: String?,
    onSearch: (String) -> Unit,
    onSongClick: (Song) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    var query by remember { mutableStateOf("") }

    // Show error in snackbar
    LaunchedEffect(error) {
        if (!error.isNullOrBlank()) {
            snackbarHostState.showSnackbar(error)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Title
            Text(
                text = "Songs",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp)
            )

            // Search bar
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text("Search", color = Color(0xFF757575))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF757575)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceColor,
                    unfocusedContainerColor = SurfaceColor,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = AccentColor,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    onSearch(query)
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Song list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(songs, key = { _, song -> song.trackId }) { index, song ->
                    SongListItem(
                        song = song,
                        onClick = { onSongClick(song) }
                    )
                    if (index < songs.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 82.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFF222222)
                        )
                    }
                }

                // Load more button at bottom
                if (songs.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = onLoadMore,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SurfaceColor,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Load more")
                            }
                        }
                    }
                }
            }
        }

        // Loading indicator
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                color = AccentColor,
                strokeWidth = 3.dp
            )
        }

        // Snackbar for errors
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF323232),
                contentColor = Color.White
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewSongs = listOf(
    Song(1L, 10L, "jack", "Upside Down", "Jack Johnson", "", "", 1, 0L),
    Song(2L, 10L, "jack", "Better Together", "Jack Johnson", "", "", 2, 0L),
    Song(3L, 10L, "jack", "Banana Pancakes", "Jack Johnson", "", "", 3, 0L),
    Song(4L, 10L, "jack", "Taylor", "Jack Johnson", "", "", 4, 0L),
    Song(5L, 10L, "jack", "Sitting, Waiting, Wishing", "Jack Johnson", "", "", 5, 0L),
)

@Preview(showBackground = true, showSystemUi = true, name = "SongsScreen — populated")
@Composable
private fun SongsScreenPopulatedPreview() {
    PhoneChallengeTheme {
        SongsScreen(
            songs = previewSongs,
            isLoading = false,
            error = null,
            onSearch = {},
            onSongClick = {},
            onLoadMore = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "SongsScreen — loading")
@Composable
private fun SongsScreenLoadingPreview() {
    PhoneChallengeTheme {
        SongsScreen(
            songs = emptyList(),
            isLoading = true,
            error = null,
            onSearch = {},
            onSongClick = {},
            onLoadMore = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "SongsScreen — error")
@Composable
private fun SongsScreenErrorPreview() {
    PhoneChallengeTheme {
        SongsScreen(
            songs = previewSongs,
            isLoading = false,
            error = "Network error. Showing cached results.",
            onSearch = {},
            onSongClick = {},
            onLoadMore = {}
        )
    }
}
