package com.nonogram.puzzle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nonogram.puzzle.ui.theme.AccentSoft
import com.nonogram.puzzle.ui.theme.Green
import com.nonogram.puzzle.ui.theme.NonogramTheme
import com.nonogram.puzzle.ui.theme.TextMuted

@Composable
fun ColClueCell(
    clues: List<Int>,
    isSatisfied: Boolean,
    isActive: Boolean,
    cellSize: Dp,
    clueHeight: Dp
) {
    val textColor = when {
        isSatisfied -> Green
        isActive -> AccentSoft
        else -> TextMuted
    }
    val fontSp = (cellSize.value * 0.28f).coerceIn(8f, 13f)
    val fontSize = fontSp.sp
    Box(
        modifier = Modifier
            .width(cellSize)
            .height(clueHeight)
            .background(
                color = if (isActive) AccentSoft.copy(alpha = 0.08f) else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            clues.forEach { clue ->
                Text(
                    text = clue.toString(),
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = textColor,
                    lineHeight = (fontSp * 1.2f).sp
                )
            }
        }
    }
}

@Composable
fun RowClueCell(
    clues: List<Int>,
    isSatisfied: Boolean,
    isActive: Boolean,
    cellSize: Dp,
    clueWidth: Dp
) {
    val textColor = when {
        isSatisfied -> Green
        isActive -> AccentSoft
        else -> TextMuted
    }
    val fontSp = (cellSize.value * 0.28f).coerceIn(8f, 13f)
    val fontSize = fontSp.sp
    Box(
        modifier = Modifier
            .width(clueWidth)
            .height(cellSize)
            .background(
                color = if (isActive) AccentSoft.copy(alpha = 0.08f) else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 4.dp)
        ) {
            clues.forEachIndexed { index, clue ->
                Text(
                    text = clue.toString(),
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = textColor
                )
                if (index < clues.size - 1) {
                    Spacer(modifier = Modifier.width(3.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClueCellPreview() {
    NonogramTheme {
        Column {
            ColClueCell(
                clues = listOf(1, 2),
                isSatisfied = false,
                isActive = true,
                cellSize = 40.dp,
                clueHeight = 80.dp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                RowClueCell(
                    clues = listOf(2, 1),
                    isSatisfied = true,
                    isActive = false,
                    cellSize = 40.dp,
                    clueWidth = 80.dp
                )
            }
        }
    }
}
