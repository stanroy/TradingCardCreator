package com.stanroy.tradingcardcreator

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stanroy.tradingcardcreator.ui.theme.TradingCardCreatorTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TradingCardCreatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Greeting(
                            name = "Android",
                        )


                    }
                }
            }

        }
    }
}

@Composable
fun SmoothHapticAnimation() {
    val vibrator = getVibrator()
//    val anim = remember { Animatable(0f) }
    val coroutine = rememberCoroutineScope()

    Column {
        Button(onClick = {
            coroutine.launch {
                vibrate(
                    vibrator,
                    longArrayOf(0, 60, 40, 50, 30, 45, 25, 55, 35, 60)
                )
            }
        }) { Text("SmoothWave") }
        Button(onClick = {
            coroutine.launch {
                vibrate(
                    vibrator,
                    longArrayOf(0, 30, 100, 30, 200, 60)
                )
            }
        }) { Text("Pulse") }
        Button(onClick = {
            coroutine.launch {
                vibrate(
                    vibrator, longArrayOf(
                        0, 80, 1000,
                        130, 13, 700
                    )
                )
            }
        }) { Text("GentlePulse") }
        Button(onClick = {
            coroutine.launch {
                vibrate(
                    vibrator, longArrayOf(
                        0, 50, 30, 40, 25, 45, 20, 50
                    )
                )
            }
        }) { Text("Wave") }

        Button(onClick = {
            coroutine.launch {
                vibrate(
                    vibrator, longArrayOf(
                        0, 400,0,100,0,400,0,100
                    )
                )
            }
        }) { Text("Test") }
    }
}

suspend fun vibrate(vibrator: Vibrator?, timings: LongArray) {
    if (vibrator == null) return

    val totalDuration = timings.sum()
    val effect = VibrationEffect.createWaveform(timings, intArrayOf(),0)
    vibrator.vibrate(effect)
    delay(totalDuration)
    vibrator.cancel()


}

@Composable
fun VisualizedWaveWithHaptic() {
    val vibrator = getVibrator()
    var isWaving by remember { mutableStateOf(false) }
    val waveProgress by animateFloatAsState(
        targetValue = if (isWaving) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800, // Matches our 260ms vibration + smooth transition
            easing = LinearEasing
        ),
        label = "waveProgress"
    )

    // Trigger haptic feedback when waving starts
    LaunchedEffect(isWaving) {
        if (isWaving && vibrator != null && vibrator.hasVibrator()) {
            val wavePattern = longArrayOf(0, 100, 150, 120, 100, 150, 200, 80)
            vibrator.vibrate(VibrationEffect.createWaveform(wavePattern, -1))

            // Auto-stop waving after vibration completes
            delay(260) // Total duration of our pattern
            isWaving = false
        }
    }
    val rotation by animateFloatAsState(
        targetValue = if (isWaving) 10f else 0f,
        animationSpec = keyframes {
            durationMillis = 800
            0f at 0
            15f at 200
            5f at 400
            12f at 600
            0f at 800
        }
    )

    val waveOffset by animateFloatAsState(
        targetValue = if (isWaving) 20f else 0f,
        animationSpec = keyframes {
            durationMillis = 800
            0f at 0
            25f at 100
            5f at 300
            20f at 500
            0f at 800
        }
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        // Animated Character
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .border(1.dp, Color.LightGray, CircleShape)
        ) {


            Text(
                text = "👋", // Wave emoji as the hand
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier
                    .offset(x = waveOffset.dp)
                    .rotate(rotation)
            )

            Text(
                text = "😊", // Face
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.offset(x = (-20).dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vibration Visualization
        Text(
            text = getVibrationVisualization(waveProgress),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(8.dp)
        )

        // Timeline Visualization
        Text(
            text = getTimelineVisualization(waveProgress),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Blue,
            modifier = Modifier.padding(4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Wave Button
        Button(
            onClick = {
                isWaving = true
                // animateWave() is essentially this ^
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF58CC02) // Duolingo green
            ),
            modifier = Modifier.height(50.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_waving_hand_24),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Wave with Haptic!")
        }

        // Progress indicator
        LinearProgressIndicator(
            progress = waveProgress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            color = Color(0xFF58CC02)
        )
    }
}

// Simplified visualization functions
private fun getVibrationVisualization(progress: Float): String {
    return when {
        progress < 0.2f -> "🔈 Vibration: Starting... █████"
        progress < 0.4f -> "🔊 Vibration: Strong buzz! ████████"
        progress < 0.6f -> "🔈 Vibration: Medium ██████"
        progress < 0.8f -> "🔊 Vibration: Strong finish! ████████"
        else -> "🔇 Vibration: Complete ✅"
    }
}

private fun getTimelineVisualization(progress: Float): String {
    val markers = listOf("0ms", "50ms", "80ms", "120ms", "145ms", "190ms", "210ms", "260ms")
    val currentMarker = (progress * markers.size).toInt().coerceAtMost(markers.size - 1)

    return markers.mapIndexed { index, marker ->
        if (index <= currentMarker) "●$marker" else "○$marker"
    }.joinToString(" → ")
}


@Composable
fun getVibrator(): Vibrator? {
    val context = LocalContext.current
    return context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TradingCardCreatorTheme {
        Greeting("Android")
    }
}