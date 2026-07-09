package com.keypadds.phonechallenge.presentation.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.keypadds.phonechallenge.domain.model.Song
import com.keypadds.phonechallenge.player.PlaybackState
import com.keypadds.phonechallenge.ui.theme.PhoneChallengeTheme
import com.keypadds.phonechallenge.ui.theme.appColors

@Composable
fun PlayerScreen(
    song: Song?,
    playbackState: PlaybackState,
    onBack: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.appColors

    val progress = if (playbackState.durationMs > 0)
        (playbackState.currentPositionMs.toFloat() / playbackState.durationMs).coerceIn(0f, 1f)
    else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.backgroundBlack)
            .padding(horizontal = 24.dp)
    ) {

        // Top bar — back arrow + "Now playing" title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
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
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Large artwork
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .aspectRatio(1f)
                .shadow(elevation = 32.dp, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
        ) {
            AsyncImage(
                model = song?.artworkUrl,
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
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = song?.artistName ?: "",
            color = colors.textTertiary,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Progress bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(50)),
            color = MaterialTheme.colorScheme.onBackground,
            trackColor = colors.progressTrack,
            strokeCap = StrokeCap.Round,
            drawStopIndicator = {}
        )

        // Time labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = playbackState.currentPositionMs.toTimeString(),
                color = colors.textTertiary,
                fontSize = 12.sp
            )
            Text(
                text = "-${(playbackState.durationMs - playbackState.currentPositionMs).coerceAtLeast(0).toTimeString()}",
                color = colors.textTertiary,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Playback controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play / Pause primary button
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.onBackground, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { if (playbackState.isPlaying) onPause() else onPlay() }
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                        tint = colors.backgroundBlack,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(24.dp))

            // Skip prev (decorative – no multi-track support per PRD)
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Skip next (decorative)
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun Long.toTimeString(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewSong = Song(
    trackId = 1L, collectionId = 10L, query = "daft punk",
    trackName = "Get Lucky", artistName = "Daft Punk feat. Pharrell Williams",
    previewUrl = "", artworkUrl = "", trackNumber = 1, lastFetched = 0L
)

@Preview(showBackground = true, showSystemUi = true, name = "PlayerScreen — playing")
@Composable
private fun PlayerScreenPlayingPreview() {
    PhoneChallengeTheme {
        PlayerScreen(
            song = previewSong,
            playbackState = PlaybackState(trackId = 1L, isPlaying = true, currentPositionMs = 86_000L, durationMs = 174_000L),
            onBack = {},
            onPlay = {},
            onPause = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "PlayerScreen — paused")
@Composable
private fun PlayerScreenPausedPreview() {
    PhoneChallengeTheme {
        PlayerScreen(
            song = previewSong,
            playbackState = PlaybackState(trackId = 1L, isPlaying = false, currentPositionMs = 20_000L, durationMs = 174_000L),
            onBack = {},
            onPlay = {},
            onPause = {}
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
            onBack = {},
            onPlay = {},
            onPause = {}
        )
    }
}
