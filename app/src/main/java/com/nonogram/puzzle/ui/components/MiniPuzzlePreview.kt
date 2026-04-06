package com.nonogram.puzzle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nonogram.puzzle.ui.theme.Accent
import com.nonogram.puzzle.ui.theme.Background
import com.nonogram.puzzle.ui.theme.Border
import com.nonogram.puzzle.ui.theme.NonogramTheme
/**
 * Either set [cellSize] (fixed per cell) or [totalSize] (fits grid into a square ~60dp by default).
 */
@Composable
fun MiniPuzzlePreview(
    solution: List<List<Int>>,
    modifier: Modifier = Modifier,
    totalSize: Dp = 60.dp,
    cellSize: Dp? = null,
    gap: Dp = 1.5.dp
) {
    val n = solution.size.coerceAtLeast(1)
    val cell = cellSize ?: (totalSize - gap * (n - 1)) / n
    Column(
        modifier = modifier
            .background(Background, RoundedCornerShape(8.dp))
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(gap)
    ) {
        solution.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                row.forEach { v ->
                    Box(
                        modifier = Modifier
                            .size(cell)
                            .background(
                                if (v == 1) Accent.copy(alpha = 0.85f) else Border.copy(alpha = 0.4f),
                                RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MiniPuzzlePreviewPreview() {
    NonogramTheme {
        MiniPuzzlePreview(
            solution = listOf(
                listOf(1, 0, 1),
                listOf(0, 1, 0),
                listOf(1, 1, 1)
            )
        )
    }
}
