package com.keypadds.phonechallenge.presentation.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.res.Configuration
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.keypadds.phonechallenge.R
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.player.PlaybackState
import com.keypadds.phonechallenge.presentation.songs.SongOptionsSheet
import com.keypadds.phonechallenge.ui.theme.PhoneChallengeTheme
import com.keypadds.phonechallenge.ui.theme.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    song: Song?,
    playbackState: PlaybackState,
    currentPositionProvider: () -> Long,
    isLooping: Boolean = false,
    onBack: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onToggleLoop: () -> Unit = {},
    onAlbumClick: () -> Unit,
) {
    val colors = MaterialTheme.appColors
    var showOptionsSheet by remember { mutableStateOf(false) }



    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.backgroundBlack)
    ) {

        // Top bar — back arrow + "Now playing" title + overflow menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 20.dp, bottom = 8.dp, start = 0.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // IconButton internal padding is 12dp. Offset by -5dp puts arrow at exactly 7dp from left edge.
                IconButton(onClick = onBack, modifier = Modifier.offset(x = (-5).dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Now playing",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.offset(x = (-5).dp)
                )
            }
            
            IconButton(onClick = { showOptionsSheet = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Artwork
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
                        .aspectRatio(1f)
                        .shadow(elevation = 32.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surface)
                        .clickable(enabled = song != null) { onAlbumClick() }
                ) {
                    AsyncImage(
                        model = song?.artworkUrl?.replace("100x100bb", "600x600bb"),
                        contentDescription = "Album artwork for ${song?.trackName}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Right Column: Controls
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 24.dp)
                ) {
                    // Track name + artist
                    Text(
                        text = song?.trackName ?: "Loading…",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = song?.artistName ?: "",
                        color = colors.textTertiary,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    PlayerProgressSection(
                        currentPositionProvider = currentPositionProvider,
                        durationMs = playbackState.durationMs,
                        colors = colors
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Playback controls — play circle | [skip back · skip forward] | [spacer] | repeat
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play / Pause — large dark gray circle
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(colors.surfaceLight, shape = CircleShape)
                                .clip(CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { if (playbackState.isPlaying) onPause() else onPlay() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(32.dp))

                        // Skip back + Skip forward pair
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Skip back
                            IconButton(
                                onClick = onSkipPrevious,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_player_skip_forward),
                                    contentDescription = "Previous",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            // Skip forward
                            IconButton(
                                onClick = onSkipNext,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_player_skip_back),
                                    contentDescription = "Next",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Repeat / Loop — accent color when enabled
                        IconButton(onClick = onToggleLoop) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_player_repeat),
                                contentDescription = "Repeat",
                                tint = if (isLooping) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(32.dp))

            // Large artwork
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .aspectRatio(1f)
                    .shadow(elevation = 32.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surface)
                    .clickable(enabled = song != null) { onAlbumClick() }
            ) {
                AsyncImage(
                    model = song?.artworkUrl?.replace("100x100bb", "600x600bb"),
                    contentDescription = "Album artwork for ${song?.trackName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Track name + artist
            Text(
                text = song?.trackName ?: "Loading…",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = song?.artistName ?: "",
                color = colors.textTertiary,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            PlayerProgressSection(
                currentPositionProvider = currentPositionProvider,
                durationMs = playbackState.durationMs,
                colors = colors
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Playback controls — play circle | [skip back · skip forward] | [spacer] | repeat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause — large dark gray circle
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(colors.surfaceLight, shape = CircleShape)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { if (playbackState.isPlaying) onPause() else onPlay() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                // Skip back + Skip forward pair
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Skip back
                    IconButton(
                        onClick = onSkipPrevious,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_player_skip_forward),
                            contentDescription = "Previous",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Skip forward
                    IconButton(
                        onClick = onSkipNext,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_player_skip_back),
                            contentDescription = "Next",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Repeat / Loop — accent color when enabled
                IconButton(onClick = onToggleLoop) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_player_repeat),
                        contentDescription = "Repeat",
                        tint = if (isLooping) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }

    if (showOptionsSheet && song != null) {
        SongOptionsSheet(
            song = song,
            onDismissRequest = { showOptionsSheet = false },
            onViewAlbumClick = {
                showOptionsSheet = false
                onAlbumClick()
            }
        )
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerProgressSection(
    currentPositionProvider: () -> Long,
    durationMs: Long,
    colors: com.keypadds.phonechallenge.ui.theme.AppColors
) {
    val currentPositionMs = currentPositionProvider()
    val progress = if (durationMs > 0)
        (currentPositionMs.toFloat() / durationMs).coerceIn(0f, 1f)
    else 0f

    // Seekbar: 4dp track, 16dp handle, no gap
    Slider(
        value = progress,
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.onBackground,
            activeTrackColor = MaterialTheme.colorScheme.onBackground,
            inactiveTrackColor = colors.progressTrack
        ),
        thumb = {
            Icon(
                painter = painterResource(id = R.drawable.ic_player_handle),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(16.dp)
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                modifier = Modifier.height(4.dp),
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.onBackground,
                    inactiveTrackColor = colors.progressTrack
                ),
                drawStopIndicator = {},
                thumbTrackGapSize = 0.dp,
                trackInsideCornerSize = 2.dp
            )
        }
    )

    // Time labels
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = currentPositionMs.toTimeString(),
            color = colors.textTertiary,
            fontSize = 12.sp
        )
        Text(
            text = "-${(durationMs - currentPositionMs).coerceAtLeast(0).toTimeString()}",
            color = colors.textTertiary,
            fontSize = 12.sp
        )
    }
}

private fun Long.toTimeString(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewSong = Song(
    trackId = 1L, collectionId = 10L, collectionName = "Album Title", query = "daft punk",
    trackName = "Get Lucky", artistName = "Daft Punk feat. Pharrell Williams",
    previewUrl = "", artworkUrl = "", trackNumber = 1, lastFetched = 0L
)

@Preview(showBackground = true, showSystemUi = true, name = "PlayerScreen — playing")
@Composable
private fun PlayerScreenPlayingPreview() {
    PhoneChallengeTheme {
        PlayerScreen(
            song = previewSong,
            playbackState = PlaybackState(trackId = 1L, isPlaying = true, durationMs = 174_000L),
            currentPositionProvider = { 86_000L },
            onBack = {},
            onPlay = {},
            onPause = {},
            onSkipPrevious = {},
            onSkipNext = {},
            onAlbumClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "PlayerScreen — paused")
@Composable
private fun PlayerScreenPausedPreview() {
    PhoneChallengeTheme {
        PlayerScreen(
            song = previewSong,
            playbackState = PlaybackState(trackId = 1L, isPlaying = false, durationMs = 174_000L),
            currentPositionProvider = { 20_000L },
            onBack = {},
            onPlay = {},
            onPause = {},
            onSkipPrevious = {},
            onSkipNext = {},
            onAlbumClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "PlayerScreen — loading")
@Composable
private fun PlayerScreenLoadingPreview() {
    PhoneChallengeTheme {
        PlayerScreen(
            song = null,
            playbackState = PlaybackState(),
            currentPositionProvider = { 0L },
            onBack = {},
            onPlay = {},
            onPause = {},
            onSkipPrevious = {},
            onSkipNext = {},
            onAlbumClick = {}
        )
    }
}
