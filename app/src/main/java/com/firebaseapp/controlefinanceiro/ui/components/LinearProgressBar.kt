package com.firebaseapp.controlefinanceiro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall

@Composable
fun LinearProgressBar(currentProgress: Float, modifier: Modifier = Modifier) {
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
                .fillMaxWidth(currentProgress)
                .background(
                    color
                )
        ) { }
    }
}