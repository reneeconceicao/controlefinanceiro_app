package com.firebaseapp.controlefinanceiro.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import kotlin.contracts.Effect

@Composable
fun LinearProgressBar(currentProgress: Float, modifier: Modifier = Modifier) {

    var animatedProgress by remember {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(currentProgress) {
        animatedProgress = currentProgress.coerceIn(0f, 1f)
    }

    val progress by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(1000),
        label = "progress"
    )

    Box(
        modifier
            .height(paddingSmall())
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(100)
            )
            .background(Color.Gray)
    ) {
        val color =
            if (currentProgress <= 0.8) Color.Green else if (currentProgress > 0.8 && currentProgress < 1) Color.Yellow else Color.Red
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(
                    color
                )
        ) { }
    }
}