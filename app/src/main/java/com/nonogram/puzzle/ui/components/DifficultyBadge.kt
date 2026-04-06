package com.nonogram.puzzle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nonogram.puzzle.data.model.Difficulty
import com.nonogram.puzzle.ui.theme.Accent
import com.nonogram.puzzle.ui.theme.AccentSoft
import com.nonogram.puzzle.ui.theme.Gold
import com.nonogram.puzzle.ui.theme.Green
import com.nonogram.puzzle.ui.theme.NonogramTheme
import com.nonogram.puzzle.ui.theme.Red

@Composable
fun DifficultyBadge(difficulty: Difficulty) {
    val (color, bgColor, borderColor) = when (difficulty) {
        Difficulty.EASY -> Triple(Green, Green.copy(alpha = 0.12f), Green.copy(alpha = 0.26f))
        Difficulty.MEDIUM -> Triple(Gold, Gold.copy(alpha = 0.12f), Gold.copy(alpha = 0.26f))
        Difficulty.HARD -> Triple(Red, Red.copy(alpha = 0.12f), Red.copy(alpha = 0.26f))
        Difficulty.EXPERT -> Triple(
            AccentSoft,
            Accent.copy(alpha = 0.12f),
            Accent.copy(alpha = 0.26f)
        )
    }
    Text(
        text = difficulty.name,
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.5.sp
    )
}

@Preview(showBackground = true)
@Composable
private fun DifficultyBadgePreview() {
    NonogramTheme {
        DifficultyBadge(Difficulty.MEDIUM)
    }
}
