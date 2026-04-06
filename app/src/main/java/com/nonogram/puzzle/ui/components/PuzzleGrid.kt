package com.nonogram.puzzle.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nonogram.puzzle.data.model.Cell
import com.nonogram.puzzle.data.model.CellState
import com.nonogram.puzzle.data.model.Difficulty
import com.nonogram.puzzle.data.model.Puzzle
import com.nonogram.puzzle.data.model.PuzzleCategory
import com.nonogram.puzzle.ui.theme.NonogramTheme
import kotlin.math.hypot

private fun categoryFillColor(category: PuzzleCategory): Color = when (category) {
    PuzzleCategory.ANIMALS -> Color(0xFF2E7D32)
    PuzzleCategory.FOOD -> Color(0xFFE65100)
    PuzzleCategory.SYMBOLS -> Color(0xFF283593)
    PuzzleCategory.OBJECTS -> Color(0xFFC62828)
    PuzzleCategory.NATURE -> Color(0xFF00695C)
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PuzzleGrid(
    grid: List<List<Cell>>,
    puzzle: Puzzle,
    onCellTap: (Int, Int) -> Unit,
    onCellLongPress: (Int, Int) -> Unit,
    onDragStart: (Int, Int) -> Unit,
    onDragEnter: (Int, Int) -> Unit,
    onDragEnd: () -> Unit,
    isRowSatisfied: (Int) -> Boolean,
    isColSatisfied: (Int) -> Boolean,
    markMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (grid.isEmpty() || puzzle.gridSize == 0) return

    val categoryColor = remember(puzzle.category) { categoryFillColor(puzzle.category) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        val n = puzzle.gridSize
        val maxColClueCount = puzzle.colClues.maxOf { it.size }.coerceAtLeast(1)
        val totalWidth = maxWidth

        // Clue strip width from typography + actual clue strings — not a fraction of cell size,
        // so rows with fewer clues than the max row don't leave a huge empty band beside "1".
        val estCellForFont = (totalWidth.value / n).coerceIn(28f, 56f)
        val clueFontSp = (estCellForFont * 0.30f).coerceIn(9f, 14f)
        val perCharW = clueFontSp * 0.58f
        val gapBetweenClues = 2f
        val endPad = 3f
        val minClueStrip = 12f
        val neededClueW = puzzle.rowClues.maxOf { clues ->
            if (clues.isEmpty()) perCharW + endPad
            else {
                val chars = clues.sumOf { it.toString().length }
                val gaps = (clues.size - 1).coerceAtLeast(0) * gapBetweenClues
                chars * perCharW + gaps + endPad
            }
        }.coerceAtLeast(minClueStrip)

        val minGridW = 28.dp * n
        val maxClueW = (totalWidth - minGridW).coerceAtLeast(minClueStrip.dp)
        val clueAreaWidth = neededClueW.dp.coerceIn(minClueStrip.dp, maxClueW)

        val cellSize = ((totalWidth - clueAreaWidth) / n).coerceIn(28.dp, 56.dp)
        val clueAreaHeight = cellSize * maxColClueCount * 0.85f

        var activeRow by remember { mutableStateOf(-1) }
        var activeCol by remember { mutableStateOf(-1) }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .width(clueAreaWidth)
                            .height(clueAreaHeight)
                    )
                    repeat(n) { col ->
                        TopClueCell(
                            clues = puzzle.colClues[col],
                            cellSize = cellSize,
                            clueHeight = clueAreaHeight,
                            isActive = activeCol == col,
                            isSatisfied = isColSatisfied(col),
                            categoryColor = categoryColor
                        )
                    }
                }

                repeat(n) { row ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        LeftClueCell(
                            clues = puzzle.rowClues[row],
                            clueWidth = clueAreaWidth,
                            cellSize = cellSize,
                            isActive = activeRow == row,
                            isSatisfied = isRowSatisfied(row),
                            categoryColor = categoryColor
                        )
                        repeat(n) { col ->
                            val cell = grid[row][col]
                            GameCell(
                                cell = cell,
                                cellSize = cellSize,
                                isFilled = cell.state == CellState.FILLED,
                                isMarked = cell.state == CellState.MARKED,
                                isActiveRow = activeRow == row,
                                isActiveCol = activeCol == col,
                                isRowSatisfied = isRowSatisfied(row),
                                isColSatisfied = isColSatisfied(col),
                                categoryColor = categoryColor,
                                markMode = markMode,
                                onTap = {
                                    activeRow = row
                                    activeCol = col
                                    onCellTap(row, col)
                                },
                                onLongPress = {
                                    activeRow = row
                                    activeCol = col
                                    onCellLongPress(row, col)
                                },
                                onDragStart = {
                                    activeRow = row
                                    activeCol = col
                                    onDragStart(row, col)
                                },
                                onDragEnter = {
                                    activeRow = row
                                    activeCol = col
                                    onDragEnter(row, col)
                                },
                                onDragEnd = onDragEnd
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameCell(
    cell: Cell,
    cellSize: Dp,
    isFilled: Boolean,
    isMarked: Boolean,
    isActiveRow: Boolean,
    isActiveCol: Boolean,
    isRowSatisfied: Boolean,
    isColSatisfied: Boolean,
    categoryColor: Color,
    markMode: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnter: () -> Unit,
    onDragEnd: () -> Unit
) {
    val isActive = isActiveRow || isActiveCol
    val bothSatisfied = isRowSatisfied && isColSatisfied

    val emptyTint = Color(0xFFEEF3FB)
    val targetBg = when {
        isMarked -> emptyTint
        isFilled && bothSatisfied -> categoryColor.copy(alpha = 0.85f)
        isFilled -> categoryColor
        isActive -> categoryColor.copy(alpha = 0.08f)
        else -> emptyTint
    }

    val bgColor by animateColorAsState(
        targetValue = targetBg,
        animationSpec = tween(120),
        label = "cellBg"
    )

    var prevFilled by remember { mutableStateOf(isFilled) }
    val scale = remember { Animatable(1f) }
    LaunchedEffect(isFilled) {
        if (isFilled && !prevFilled) {
            scale.snapTo(0.6f)
            scale.animateTo(
                1.1f,
                spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh)
            )
            scale.animateTo(1f, tween(80))
        } else if (!isFilled) {
            scale.snapTo(1f)
        }
        prevFilled = isFilled
    }

    val gap = (cellSize.value * 0.06f).dp.coerceIn(1.dp, 2.5.dp)

    val innerModifier = Modifier
        .fillMaxSize()
        .padding(gap)
        .scale(scale.value)
        .clip(RoundedCornerShape(5.dp))
        .background(bgColor)
        .then(
            if (isFilled) {
                Modifier.drawWithContent {
                    drawContent()
                    drawRect(
                        color = Color.White.copy(alpha = 0.25f),
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height * 0.3f)
                    )
                }
            } else {
                Modifier
            }
        )
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = { onLongPress() }
            )
        }
        .pointerInput(markMode) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                var isDragging = false
                var movedAny = false
                var dragLoop = true
                while (dragLoop) {
                    val event = awaitPointerEvent()
                    val anyPressed = event.changes.any { it.pressed }
                    if (!anyPressed) {
                        if (!isDragging && !movedAny) {
                            if (markMode) onLongPress() else onTap()
                        }
                        onDragEnd()
                        dragLoop = false
                    } else {
                        val moved = event.changes.any { change ->
                            val d = change.positionChange()
                            hypot(d.x, d.y) > 8f
                        }
                        if (moved) {
                            movedAny = true
                            if (!markMode) {
                                if (!isDragging) {
                                    isDragging = true
                                    onDragStart()
                                }
                                event.changes.forEach { it.consume() }
                                onDragEnter()
                            }
                        }
                    }
                }
            }
        }

    Box(
        modifier = Modifier.size(cellSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = innerModifier,
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = when {
                        isFilled -> categoryColor.copy(alpha = 0.5f)
                        isActive -> categoryColor.copy(alpha = 0.25f)
                        else -> Color(0xFFCCD8EF)
                    },
                    cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            if (isMarked) {
                Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                    drawLine(
                        color = categoryColor,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = categoryColor,
                        start = Offset(size.width, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
private fun TopClueCell(
    clues: List<Int>,
    cellSize: Dp,
    clueHeight: Dp,
    isActive: Boolean,
    isSatisfied: Boolean,
    categoryColor: Color
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isSatisfied -> categoryColor.copy(alpha = 0.12f)
            isActive -> categoryColor.copy(alpha = 0.06f)
            else -> Color.Transparent
        },
        animationSpec = tween(200),
        label = "clueBgTop"
    )
    val textColor by animateColorAsState(
        targetValue = when {
            isSatisfied -> categoryColor
            isActive -> categoryColor.copy(alpha = 0.8f)
            else -> Color(0xFF5A6B8A)
        },
        animationSpec = tween(200),
        label = "clueTextTop"
    )
    val fontSize = (cellSize.value * 0.30f).coerceIn(9f, 14f).sp

    Box(
        modifier = Modifier
            .width(cellSize)
            .height(clueHeight)
            .background(bgColor),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(bottom = 3.dp)
        ) {
            clues.forEach { n ->
                Text(
                    text = "$n",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = textColor,
                    lineHeight = (fontSize.value * 1.15f).sp
                )
            }
        }
    }
}

@Composable
private fun LeftClueCell(
    clues: List<Int>,
    clueWidth: Dp,
    cellSize: Dp,
    isActive: Boolean,
    isSatisfied: Boolean,
    categoryColor: Color
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isSatisfied -> categoryColor.copy(alpha = 0.12f)
            isActive -> categoryColor.copy(alpha = 0.06f)
            else -> Color.Transparent
        },
        animationSpec = tween(200),
        label = "clueBgLeft"
    )
    val textColor by animateColorAsState(
        targetValue = when {
            isSatisfied -> categoryColor
            isActive -> categoryColor.copy(alpha = 0.8f)
            else -> Color(0xFF5A6B8A)
        },
        animationSpec = tween(200),
        label = "clueTextLeft"
    )
    val fontSize = (cellSize.value * 0.30f).coerceIn(9f, 14f).sp

    Box(
        modifier = Modifier
            .width(clueWidth)
            .height(cellSize)
            .background(bgColor),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier.padding(end = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            clues.forEach { n ->
                Text(
                    text = "$n",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = textColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PuzzleGridPreview() {
    val puzzle = Puzzle(
        id = 1,
        levelNumber = 1,
        title = "Preview",
        category = PuzzleCategory.SYMBOLS,
        difficulty = Difficulty.EASY,
        gridSize = 5,
        solution = listOf(
            listOf(0, 1, 0, 1, 0),
            listOf(1, 1, 1, 1, 1),
            listOf(1, 1, 1, 1, 1),
            listOf(0, 1, 1, 1, 0),
            listOf(0, 0, 1, 0, 0)
        ),
        rowClues = listOf(listOf(1, 1), listOf(5), listOf(5), listOf(3), listOf(1)),
        colClues = listOf(listOf(1, 1), listOf(4), listOf(5), listOf(4), listOf(1, 1))
    )
    val grid = List(5) { r ->
        List(5) { c ->
            Cell(r, c, if ((r + c) % 3 == 0) CellState.FILLED else CellState.EMPTY)
        }
    }
    NonogramTheme {
        Column(
            modifier = Modifier.background(Color.Transparent),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PuzzleGrid(
                grid = grid,
                puzzle = puzzle,
                onCellTap = { _, _ -> },
                onCellLongPress = { _, _ -> },
                onDragStart = { _, _ -> },
                onDragEnter = { _, _ -> },
                onDragEnd = {},
                isRowSatisfied = { false },
                isColSatisfied = { false },
                markMode = false
            )
        }
    }
}
