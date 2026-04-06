package com.nonogram.puzzle.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nonogram.puzzle.ui.theme.AccentGold
import com.nonogram.puzzle.ui.theme.AppBackground
import com.nonogram.puzzle.ui.theme.BorderLight
import com.nonogram.puzzle.ui.theme.PrimaryBlue
import com.nonogram.puzzle.ui.theme.SurfaceWhite
import com.nonogram.puzzle.ui.theme.TextLight
import com.nonogram.puzzle.ui.theme.TextWhite
import com.nonogram.puzzle.data.model.toDisplayLabel
import com.nonogram.puzzle.utils.PuzzleData
import com.nonogram.puzzle.utils.SoundManager
import com.nonogram.puzzle.viewmodel.DailyViewModel
import com.nonogram.puzzle.viewmodel.HomeViewModel
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

@Composable
fun WinScreen(
    puzzleId: Int,
    time: Long,
    errors: Int,
    isDailyPuzzle: Boolean = false,
    onNextPuzzle: () -> Unit,
    onHome: () -> Unit
) {
    val dailyViewModel: DailyViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val context = LocalContext.current
    val streak by homeViewModel.streak.collectAsStateWithLifecycle()

    BackHandler {
        SoundManager.getInstance(context).playBackNav()
        onHome()
    }

    LaunchedEffect(streak) {
        if (streak > 0 && streak % 7 == 0) {
            delay(1500L)
            SoundManager.getInstance(context).playStreakMilestone()
        }
    }

    LaunchedEffect(isDailyPuzzle) {
        if (isDailyPuzzle) {
            dailyViewModel.markDailyCompleted()
        }
    }

    LaunchedEffect(Unit) {
        homeViewModel.recordLevelSolvedToday()
    }

    LaunchedEffect(Unit) {
        homeViewModel.refreshCompletedIds()
    }

    WinScreenContent(
        puzzleId = puzzleId,
        time = time,
        errors = errors,
        isDailyPuzzle = isDailyPuzzle,
        onNextPuzzle = onNextPuzzle,
        onHome = onHome
    )
}

@Composable
fun WinScreenContent(
    puzzleId: Int,
    time: Long,
    errors: Int,
    isDailyPuzzle: Boolean,
    onNextPuzzle: () -> Unit,
    onHome: () -> Unit
) {
    val puzzle = PuzzleData.getById(puzzleId)
    if (puzzle == null) return
    val shapeName = puzzle.title
    val shapeCategory = puzzle.category.displayName
    val stars = when {
        errors == 0 -> 3
        errors <= 3 -> 2
        else -> 1
    }
    val formattedTime = "%02d:%02d".format(time / 60, time % 60)
    val levelNum = puzzle.levelNumber
    val currentDiffLabel = puzzle.difficulty.toDisplayLabel()
    val catPuzzles = PuzzleData.getByCategory(puzzle.category)
    val idx = catPuzzles.indexOfFirst { it.id == puzzleId }
    val nextPuzzle = if (idx >= 0) catPuzzles.getOrNull(idx + 1) else null
    val context = LocalContext.current
    var showTitle by remember { mutableStateOf(false) }
    var showGrid by remember { mutableStateOf(false) }
    var showName by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        showTitle = true
        delay(400)
        showGrid = true
        delay(800)
        showName = true
        delay(1200)
        showButtons = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF4A90E2), Color(0xFF2F6BC4))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            repeat(16) { i ->
                val angle = i * 22.5f
                val rad = Math.toRadians(angle.toDouble())
                drawLine(
                    color = Color.White.copy(alpha = 0.07f),
                    start = Offset(size.width / 2, size.height * 0.3f),
                    end = Offset(
                        (size.width / 2 + size.width * cos(rad)).toFloat(),
                        (size.height * 0.3f + size.height * sin(rad)).toFloat()
                    ),
                    strokeWidth = 30.dp.toPx()
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))

                AnimatedVisibility(
                    visible = showTitle,
                    enter = fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.85f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Level Completed!",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )
                        Text(
                            text = "Level $levelNum · $currentDiffLabel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                AnimatedVisibility(
                    visible = showGrid,
                    enter = fadeIn(tween(300))
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .shadow(16.dp, RoundedCornerShape(20.dp))
                            .background(SurfaceWhite, RoundedCornerShape(20.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val cellSize = 176.dp / puzzle.gridSize
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            puzzle.solution.forEachIndexed { rowIdx, row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    row.forEachIndexed { colIdx, cell ->
                                        val cellDelay = (rowIdx * puzzle.gridSize + colIdx) * 15
                                        WinRevealCell(
                                            filled = cell == 1,
                                            cellSize = cellSize,
                                            showGrid = showGrid,
                                            delayMs = cellDelay.toLong()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                AnimatedVisibility(
                    visible = showName,
                    enter = fadeIn(tween(500)) + scaleIn(
                        tween(500),
                        initialScale = 0.5f
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "It's a...",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color.White,
                                    RoundedCornerShape(50.dp)
                                )
                                .padding(horizontal = 28.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = shapeName,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryBlue
                            )
                        }
                        Text(
                            text = shapeCategory,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { i ->
                        Text(
                            text = "\u2605",
                            fontSize = 36.sp,
                            color = if (i < stars) AccentGold
                            else TextWhite.copy(alpha = 0.3f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            TextWhite.copy(alpha = 0.15f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        Pair(formattedTime, "Time"),
                        Pair("$errors", "Errors"),
                        Pair("$levelNum", "Level")
                    ).forEach { (value, label) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                value,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextWhite,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                label,
                                fontSize = 11.sp,
                                color = TextWhite.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                AnimatedVisibility(
                    visible = showButtons,
                    enter = fadeIn(tween(300))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(SurfaceWhite, RoundedCornerShape(50.dp))
                                .clickable {
                                    SoundManager.getInstance(context).playButtonTap()
                                    onNextPuzzle()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = when {
                                            isDailyPuzzle -> "Continue"
                                            nextPuzzle != null -> "Next Level"
                                            else -> "All Done!"
                                        },
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryBlue
                                    )
                                }
                                if (nextPuzzle != null && !isDailyPuzzle) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .border(
                                    1.5.dp,
                                    Color.White.copy(alpha = 0.5f),
                                    RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    SoundManager.getInstance(context).playBackNav()
                                    onHome()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Home",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WinRevealCell(
    filled: Boolean,
    cellSize: Dp,
    showGrid: Boolean,
    delayMs: Long
) {
    var cellVis by remember { mutableStateOf(false) }
    LaunchedEffect(showGrid) {
        if (showGrid) {
            delay(delayMs)
            cellVis = true
        }
    }
    val cellScale by animateFloatAsState(
        targetValue = if (cellVis) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cellScale"
    )
    Box(
        modifier = Modifier
            .size(cellSize)
            .scale(cellScale)
            .background(
                if (filled) PrimaryBlue else AppBackground,
                RoundedCornerShape(2.dp)
            )
    )
}
