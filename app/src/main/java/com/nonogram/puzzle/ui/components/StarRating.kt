package com.nonogram.puzzle.ui.components

import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nonogram.puzzle.ui.theme.Border
import com.nonogram.puzzle.ui.theme.Gold
import kotlinx.coroutines.delay
import androidx.compose.animation.core.Animatable
import com.nonogram.puzzle.ui.theme.NonogramTheme

@Composable
fun StarRating(stars: Int, size: Dp = 24.dp, animated: Boolean = false) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(3) { index ->
            val isFilled = index < stars
            val scale = remember(index, stars, animated) { Animatable(if (animated) 0f else 1f) }
            LaunchedEffect(stars, animated) {
                if (animated && isFilled) {
                    val delayMs = when (index) {
                        0 -> 100L
                        1 -> 250L
                        else -> 400L
                    }
                    scale.snapTo(0f)
                    delay(delayMs)
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(dampingRatio = 0.45f, stiffness = 350f)
                    )
                } else {
                    scale.snapTo(1f)
                }
            }
            Text(
                text = "★",
                color = if (isFilled) Gold else Border,
                fontSize = (size.value * 0.85f).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .size(size)
                    .scale(scale.value)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StarRatingPreview() {
    NonogramTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StarRating(stars = 1)
            StarRating(stars = 3, animated = true)
        }
    }
}
