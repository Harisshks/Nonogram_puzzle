package com.nonogram.puzzle.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import com.nonogram.puzzle.data.preferences.UserPreferences
import kotlinx.coroutines.flow.first
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nonogram.puzzle.ui.components.BannerAdView
import com.nonogram.puzzle.ui.components.PuzzleGrid
import com.nonogram.puzzle.data.model.Cell
import com.nonogram.puzzle.data.model.Puzzle
import com.nonogram.puzzle.utils.AdManager
import com.nonogram.puzzle.viewmodel.GameViewModel
import com.nonogram.puzzle.ui.theme.AccentGold
import com.nonogram.puzzle.ui.theme.AccentRed
import com.nonogram.puzzle.ui.theme.AppBackground
import com.nonogram.puzzle.ui.theme.BorderLight
import com.nonogram.puzzle.ui.theme.PrimaryBlue
import com.nonogram.puzzle.data.model.Difficulty
import com.nonogram.puzzle.data.model.PuzzleCategory
import com.nonogram.puzzle.ui.theme.TextDark
import com.nonogram.puzzle.ui.theme.TextLight
import com.nonogram.puzzle.utils.SoundManager

@Composable
fun GameScreen(
    puzzleId: Int,
    isDaily: Boolean = false,
    viewModel: GameViewModel = viewModel(),
    onBack: () -> Unit,
    onWin: (Long, Int) -> Unit
) {
    LaunchedEffect(puzzleId, isDaily) {
        viewModel.loadPuzzle(puzzleId, isDaily)
    }

    val puzzle by viewModel.puzzle.collectAsStateWithLifecycle()
    val grid by viewModel.grid.collectAsStateWithLifecycle()
    val isCompleted by viewModel.isCompleted.collectAsStateWithLifecycle()
    val errors by viewModel.errors.collectAsStateWithLifecycle()
    val hintsRemaining by viewModel.hintsRemaining.collectAsStateWithLifecycle()
    val timer by viewModel.timer.collectAsStateWithLifecycle()
    val showErrorFlash by viewModel.showErrorFlash.collectAsStateWithLifecycle()
    val errorHighlight by viewModel.errorHighlight.collectAsStateWithLifecycle()
    val outOfLives by viewModel.outOfLives.collectAsStateWithLifecycle()
    val heartsLeft by viewModel.heartsLeft.collectAsStateWithLifecycle()

    val context = LocalContext.current

    BackHandler {
        SoundManager.getInstance(context).playBackNav()
        onBack()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopTimer() }
    }

    LaunchedEffect(Unit) {
        AdManager.loadRewarded(context.applicationContext)
    }

    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            onWin(timer, errors)
        }
    }

    val topColor = puzzle?.let { categoryTopBarColor(it.category) } ?: PrimaryBlue

    if (outOfLives) {
        GameOverDialog(
            categoryColor = topColor,
            onRestart = { viewModel.resetPuzzle() },
            onWatchAd = {
                val activity = context.findActivity()
                if (activity == null) {
                    Toast.makeText(context, "Ad not available.", Toast.LENGTH_SHORT).show()
                    return@GameOverDialog
                }
                AdManager.showRewarded(
                    activity = activity,
                    onUserEarnedReward = { viewModel.addHeart() },
                    onDismissed = {
                        AdManager.loadRewarded(context.applicationContext)
                    },
                    onUnavailable = {
                        Toast.makeText(
                            context,
                            "Ad not available. Try again or restart the level.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            },
            onDismiss = {}
        )
    }

    GameScreenContent(
        puzzle = puzzle,
        grid = grid,
        isCompleted = isCompleted,
        errors = errors,
        hintsRemaining = hintsRemaining,
        timer = timer,
        showErrorFlash = showErrorFlash,
        errorHighlight = errorHighlight,
        isDaily = isDaily,
        gameViewModel = viewModel,
        onBack = onBack,
        onCellTap = viewModel::onCellTap,
        onCellLongPress = viewModel::onCellLongPress,
        onDragStart = viewModel::onDragStart,
        onDragEnter = viewModel::onDragEnter,
        onDragEnd = viewModel::onDragEnd,
        isRowSatisfied = viewModel::isRowSatisfied,
        isColSatisfied = viewModel::isColSatisfied,
        onUseHint = viewModel::useHint,
        outOfLives = outOfLives,
        heartsLeft = heartsLeft
    )
}

private fun Context.findActivity(): Activity? {
    var current: Context = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

private fun categoryTopBarColor(category: PuzzleCategory): Color = when (category) {
    PuzzleCategory.ANIMALS -> Color(0xFF388E3C)
    PuzzleCategory.FOOD -> Color(0xFFE64A19)
    PuzzleCategory.SYMBOLS -> Color(0xFF303F9F)
    PuzzleCategory.OBJECTS -> Color(0xFFD32F2F)
    PuzzleCategory.NATURE -> Color(0xFF00796B)
}

private fun categoryMarkFillColor(category: PuzzleCategory): Color = when (category) {
    PuzzleCategory.ANIMALS -> Color(0xFF2E7D32)
    PuzzleCategory.FOOD -> Color(0xFFE65100)
    PuzzleCategory.SYMBOLS -> Color(0xFF283593)
    PuzzleCategory.OBJECTS -> Color(0xFFC62828)
    PuzzleCategory.NATURE -> Color(0xFF00695C)
}

@Composable
fun GameScreenContent(
    puzzle: Puzzle?,
    grid: List<List<Cell>>,
    isCompleted: Boolean,
    errors: Int,
    hintsRemaining: Int,
    timer: Long,
    showErrorFlash: Boolean,
    errorHighlight: Boolean,
    isDaily: Boolean,
    gameViewModel: GameViewModel,
    onBack: () -> Unit,
    onCellTap: (Int, Int) -> Unit,
    onCellLongPress: (Int, Int) -> Unit,
    onDragStart: (Int, Int) -> Unit,
    onDragEnter: (Int, Int) -> Unit,
    onDragEnd: () -> Unit,
    isRowSatisfied: (Int) -> Boolean,
    isColSatisfied: (Int) -> Boolean,
    onUseHint: () -> Unit,
    outOfLives: Boolean = false,
    heartsLeft: Int = 3
) {
    val currentPuzzle = puzzle ?: return
    var markMode by remember { mutableStateOf(false) }
    var showPauseMenu by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context.applicationContext) }

    LaunchedEffect(showPauseMenu) {
        if (showPauseMenu) gameViewModel.stopTimer()
        else gameViewModel.startTimerIfNotComplete()
    }

    LaunchedEffect(currentPuzzle.id) {
        if (!userPrefs.markTipShown.first()) {
            snackbarHostState.showSnackbar(
                message = "Tip: Use X to mark cells you know are empty",
                duration = SnackbarDuration.Short
            )
            userPrefs.setMarkTipShown()
        }
    }

    LaunchedEffect(timer, isCompleted, currentPuzzle.difficulty) {
        if (timer > 0 && !isCompleted) {
            val timeLimit = when (currentPuzzle.difficulty) {
                Difficulty.EASY -> 120L
                Difficulty.MEDIUM -> 300L
                Difficulty.HARD -> 600L
                Difficulty.EXPERT -> 600L
            }
            val remaining = timeLimit - timer
            if (remaining in 1..10) {
                SoundManager.getInstance(context).playTimerWarning()
            }
        }
    }

    val mins = timer / 60
    val secs = timer % 60
    val formattedTime = "%02d:%02d".format(mins, secs)
    val blockPlay = outOfLives || isCompleted

    val topBarColor = categoryTopBarColor(currentPuzzle.category)
    val markFillColor = categoryMarkFillColor(currentPuzzle.category)
    val titleText = if (isDaily) {
        "Daily Challenge"
    } else {
        "Level ${currentPuzzle.levelNumber}"
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppBackground,
        topBar = {
            Surface(
                color = topBarColor,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.05f),
                            radius = 80.dp.toPx(),
                            center = Offset(size.width * 0.85f, 0f)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.04f),
                            radius = 60.dp.toPx(),
                            center = Offset(size.width * 0.1f, size.height)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 10.dp, bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.18f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        SoundManager.getInstance(context).playBackNav()
                                        onBack()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = titleText,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.18f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable(enabled = !outOfLives) {
                                        SoundManager.getInstance(context).playDialogOpen()
                                        showPauseMenu = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Pause",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(3) { i ->
                                    key(i) {
                                        val isFull = i < heartsLeft
                                        val heartScale = remember { Animatable(1f) }
                                        LaunchedEffect(isFull) {
                                            if (!isFull) {
                                                heartScale.animateTo(1.4f, tween(80))
                                                heartScale.animateTo(
                                                    1f,
                                                    spring(Spring.DampingRatioMediumBouncy)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "\u2665",
                                            fontSize = 20.sp,
                                            color = if (isFull) Color.White
                                            else Color.White.copy(alpha = 0.25f),
                                            modifier = Modifier.scale(heartScale.value)
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        Color.White.copy(alpha = 0.18f),
                                        RoundedCornerShape(50.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                val diffName = currentPuzzle.difficulty.name
                                    .lowercase()
                                    .replaceFirstChar { it.uppercase() }
                                Text(
                                    text = "${currentPuzzle.category.displayName} · $diffName",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        Color.White.copy(alpha = 0.18f),
                                        RoundedCornerShape(50.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = formattedTime,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = AppBackground,
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(50.dp))
                                .padding(6.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            if (!markMode) AppBackground else Color.Transparent,
                                            CircleShape
                                        )
                                        .clickable(enabled = !outOfLives) {
                                            if (markMode) {
                                                SoundManager.getInstance(context).playMarkToggle()
                                            }
                                            markMode = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "\u2715",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!markMode) TextDark else TextLight
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            if (markMode) AppBackground else Color.Transparent,
                                            CircleShape
                                        )
                                        .clickable(enabled = !outOfLives) {
                                            if (!markMode) {
                                                SoundManager.getInstance(context).playMarkToggle()
                                            }
                                            markMode = true
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .background(
                                                if (markMode) markFillColor else TextLight,
                                                RoundedCornerShape(5.dp)
                                            )
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color.White, CircleShape)
                                    .border(1.dp, BorderLight, CircleShape)
                                    .clickable(enabled = !outOfLives) {
                                        if (hintsRemaining > 0) onUseHint()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = "Hint",
                                    tint = if (hintsRemaining > 0) AccentGold else TextLight,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            if (hintsRemaining > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .background(PrimaryBlue, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$hintsRemaining",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                    BannerAdView(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    ) { paddingValues ->
        val categoryTint = Color(currentPuzzle.category.colorDark)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            categoryTint.copy(alpha = 0.04f),
                            AppBackground
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = showErrorFlash && errorHighlight,
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(AccentRed)
                )
            }

            if (grid.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PuzzleGrid(
                        grid = grid,
                        puzzle = currentPuzzle,
                        onCellTap = { r, c ->
                            if (markMode) onCellLongPress(r, c) else onCellTap(r, c)
                        },
                        onCellLongPress = onCellLongPress,
                        onDragStart = onDragStart,
                        onDragEnter = onDragEnter,
                        onDragEnd = onDragEnd,
                        isRowSatisfied = isRowSatisfied,
                        isColSatisfied = isColSatisfied,
                        markMode = markMode,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (blockPlay) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = if (isCompleted) 0.15f else 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Text(
                            text = "SOLVED!",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            if (showPauseMenu) {
                GamePauseDialog(
                    categoryColor = topBarColor,
                    puzzleTitle = currentPuzzle.title,
                    onResume = { showPauseMenu = false },
                    onRestart = {
                        showPauseMenu = false
                        gameViewModel.resetPuzzle()
                    },
                    onExitToHome = {
                        showPauseMenu = false
                        onBack()
                    }
                )
            }
        }
    }
}
