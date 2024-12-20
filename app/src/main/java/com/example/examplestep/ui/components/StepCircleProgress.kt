package com.example.examplestep.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examplestep.ui.theme.Blue

@Composable
fun StepCircleProgress(currentSteps: Int, goalSteps: Int) {
    // 걸음 수 비율 계산
    val progress = (currentSteps.toFloat() / goalSteps.toFloat()).coerceIn(0f, 1f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 원형 배경 게이지
        Canvas(modifier = Modifier.size(300.dp)) {
            drawArc(
                color = Color.Gray.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 30.dp.toPx())
            )
        }

        // 원형 진행 게이지
        Canvas(modifier = Modifier.size(300.dp)) {
            drawArc(
                color = Blue,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 30.dp.toPx())
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 백분율 계산
            val progressPercentage = (currentSteps.toFloat() / goalSteps.toFloat()) * 100

            Text(
                text = "${currentSteps}걸음",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "${progressPercentage.toInt()} %",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = boldFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                ),
                color = Color.Gray
            )
        }
    }
}

