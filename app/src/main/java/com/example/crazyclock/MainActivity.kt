package com.example.crazyclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrazyClock()
        }
    }
}

@Composable
fun CrazyClock() {
    var time by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            time = Calendar.getInstance()
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2.2f

        val hour = time.get(Calendar.HOUR)
        val minute = time.get(Calendar.MINUTE)
        val second = time.get(Calendar.SECOND)

        val hourAngle = (hour + minute / 60f) * 30f
        val minuteAngle = minute * 6f
        val secondAngle = second * 6f

        drawLine(
            color = Color.Yellow,
            start = center,
            end = Offset(
                center.x + radius * 0.5f * cos(Math.toRadians(hourAngle - 90.0)).toFloat(),
                center.y + radius * 0.5f * sin(Math.toRadians(hourAngle - 90.0)).toFloat()
            ),
            strokeWidth = 16f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color.Cyan,
            start = center,
            end = Offset(
                center.x + radius * 0.7f * cos(Math.toRadians(minuteAngle - 90.0)).toFloat(),
                center.y + radius * 0.7f * sin(Math.toRadians(minuteAngle - 90.0)).toFloat()
            ),
            strokeWidth = 12f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color.Red,
            start = center,
            end = Offset(
                center.x + radius * 0.9f * cos(Math.toRadians(secondAngle - 90.0)).toFloat(),
                center.y + radius * 0.9f * sin(Math.toRadians(secondAngle - 90.0)).toFloat()
            ),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
    }
}
