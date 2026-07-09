package com.keypadds.phonechallenge.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.drawBehind
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.keypadds.phonechallenge.R
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        delay(1500.milliseconds)
        onTimeout()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.3357f to Color(0xFF000000),
                            1.0f    to Color(0xFF0086A0)
                        ),
                        start = Offset(0f, size.height),
                        end   = Offset(size.width, 0f)
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_splash_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(160.dp)
        )
    }
}
