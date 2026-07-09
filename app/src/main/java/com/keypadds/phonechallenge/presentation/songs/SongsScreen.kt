package com.keypadds.phonechallenge.presentation.songs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keypadds.phonechallenge.R
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.ui.theme.PhoneChallengeTheme
import com.keypadds.phonechallenge.ui.theme.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsScreen(
    songs: List<Song>,
    recentSongs: List<Song>,
    isLoading: Boolean,
    error: String?,
    onSearch: (String) -> Unit,
    onSongClick: (Song) -> Unit,
    onOptionsClick: (Song) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.appColors
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
            .background(colors.backgroundDark)
    ) {
        val displaySongs = if (query.isBlank()) recentSongs else songs

        Column(modifier = Modifier.fillMaxSize()) {

            // Title
            Text(
                text = "Songs",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 20.dp, top = 20.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(colors.surface, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_icon),
                    contentDescription = "Search",
                    tint = colors.searchDarkGrey,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { 
                        query = it 
                        onSearch(it)
                    },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        focusManager.clearFocus()
                    }),
                    cursorBrush = SolidColor(colors.accent),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text("Search", color = colors.searchDarkGrey, fontSize = 16.sp)
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (displaySongs.isEmpty() && !isLoading) {
                if (error != null) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Oops! Something went wrong:\n$error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else if (query.isNotBlank()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No songs found for \"$query\"",
                            color = colors.textTertiary,
                            modifier = Modifier.padding(32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Search for a song to get started",
                            color = colors.textTertiary,
                            modifier = Modifier.padding(32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // Song list
                val listState = androidx.compose.foundation.lazy.rememberLazyListState()
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    itemsIndexed(displaySongs, key = { _, song -> song.trackId }) { index, song ->
                        SongListItem(
                            song = song,
                            onClick = { onSongClick(song) },
                            onOptionsClick = { onOptionsClick(song) }
                        )
                        if (index < displaySongs.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 82.dp),
                                thickness = 0.5.dp,
                                color = colors.divider
                            )
                        }
                        // Infinite scroll trigger
                        if (query.isNotBlank() && index >= displaySongs.lastIndex - 3) {
                            LaunchedEffect(query, displaySongs.size) {
                                onLoadMore()
                            }
                        }
                    }

                    // Loading indicator at bottom for pagination
                    if (query.isNotBlank() && displaySongs.isNotEmpty() && isLoading) {
                        item(key = "pagination_loading") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = colors.accent,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Loading indicator for initial search
        if (isLoading && displaySongs.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                color = colors.accent,
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
                containerColor = colors.surfaceLighter,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewSongs = listOf(
    Song(1L, 10L, "Album Title", "jack", "Upside Down", "Jack Johnson", "", "", 1, 0L),
    Song(2L, 10L, "Album Title", "jack", "Better Together", "Jack Johnson", "", "", 2, 0L),
    Song(3L, 10L, "Album Title", "jack", "Banana Pancakes", "Jack Johnson", "", "", 3, 0L),
    Song(4L, 10L, "Album Title", "jack", "Taylor", "Jack Johnson", "", "", 4, 0L),
    Song(5L, 10L, "Album Title", "jack", "Sitting, Waiting, Wishing", "Jack Johnson", "", "", 5, 0L),
)

@Preview(showBackground = true, showSystemUi = true, name = "SongsScreen — populated")
@Composable
private fun SongsScreenPopulatedPreview() {
    PhoneChallengeTheme {
        SongsScreen(
            songs = previewSongs,
            recentSongs = emptyList(),
            isLoading = false,
            error = null,
            onSearch = {},
            onSongClick = {},
            onOptionsClick = {},
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
            recentSongs = emptyList(),
            isLoading = true,
            error = null,
            onSearch = {},
            onSongClick = {},
            onOptionsClick = {},
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
            recentSongs = emptyList(),
            isLoading = false,
            error = "Network error. Showing cached results.",
            onSearch = {},
            onSongClick = {},
            onOptionsClick = {},
            onLoadMore = {}
        )
    }
}
