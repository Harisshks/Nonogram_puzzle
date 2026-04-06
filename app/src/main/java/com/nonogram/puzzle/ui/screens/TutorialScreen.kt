package com.nonogram.puzzle.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nonogram.puzzle.data.model.Cell
import com.nonogram.puzzle.data.model.CellState
import com.nonogram.puzzle.ui.theme.AccentGold
import com.nonogram.puzzle.ui.theme.AccentGreen
import com.nonogram.puzzle.ui.theme.AccentRed
import com.nonogram.puzzle.ui.theme.AppBackground
import com.nonogram.puzzle.ui.theme.BorderLight
import com.nonogram.puzzle.ui.theme.PrimaryBlue
import com.nonogram.puzzle.ui.theme.SurfaceWhite
import com.nonogram.puzzle.ui.theme.TextDark
import com.nonogram.puzzle.ui.theme.TextLight
import com.nonogram.puzzle.ui.theme.TextMedium
import com.nonogram.puzzle.viewmodel.TutorialViewModel
import kotlinx.coroutines.delay

@Composable
fun TutorialScreen(
    onFinished: () -> Unit
) {
    val activity = LocalContext.current as ComponentActivity
    val viewModel: TutorialViewModel = viewModel(viewModelStoreOwner = activity)
    val step by viewModel.currentStep.collectAsStateWithLifecycle()
    val grid by viewModel.tutorialGrid.collectAsStateWithLifecycle()
    val highlighted by viewModel.highlightedCells.collectAsStateWithLifecycle()
    val stepDone by viewModel.stepTaskDone.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = PrimaryBlue.copy(alpha = 0.06f),
                radius = 200.dp.toPx(),
                center = Offset(size.width * 0.8f, size.height * 0.15f)
            )
            drawCircle(
                color = PrimaryBlue.copy(alpha = 0.04f),
                radius = 150.dp.toPx(),
                center = Offset(size.width * 0.1f, size.height * 0.7f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(viewModel.totalSteps) { i ->
                        val isActive = i == step
                        val isDone = i < step
                        val dotWidth by animateDpAsState(
                            targetValue = if (isActive) 20.dp else 8.dp,
                            animationSpec = tween(300),
                            label = "dotWidth"
                        )
                        Box(
                            modifier = Modifier
                                .width(dotWidth)
                                .height(8.dp)
                                .background(
                                    color = when {
                                        isActive -> PrimaryBlue
                                        isDone -> PrimaryBlue.copy(alpha = 0.4f)
                                        else -> BorderLight
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }

                if (step < viewModel.totalSteps - 1) {
                    Text(
                        text = "Skip",
                        fontSize = 14.sp,
                        color = TextLight,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable {
                            viewModel.skipTutorial()
                            onFinished()
                        }
                    )
                }
            }

            AnimatedContent(
                modifier = Modifier.weight(1f),
                targetState = step,
                transitionSpec = {
                    val dir = if (targetState > initialState) 1 else -1
                    (
                        slideInHorizontally(
                            animationSpec = tween(350),
                            initialOffsetX = { full -> full * dir }
                        ) + fadeIn(tween(350))
                        ) togetherWith (
                        slideOutHorizontally(
                            animationSpec = tween(350),
                            targetOffsetX = { full -> -full * dir }
                        ) + fadeOut(tween(350))
                        )
                },
                label = "stepContent"
            ) { currentStep ->
                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentStep) {
                        0 -> TutorialStep0Welcome()
                        1 -> TutorialStep1WhatIs()
                        2 -> TutorialStep2Clues()
                        3 -> TutorialStep3ReadClues()
                        4 -> TutorialStep4Fill(
                            grid = grid,
                            highlighted = highlighted,
                            onCellTap = { r, c ->
                                viewModel.onTutorialCellTap(r, c)
                            }
                        )
                        5 -> TutorialStep5Mark(
                            grid = grid,
                            highlighted = highlighted,
                            onCellTap = { r, c ->
                                viewModel.onTutorialCellTap(r, c)
                            },
                            onCellLongPress = { r, c ->
                                viewModel.onTutorialCellLongPress(r, c)
                            }
                        )
                        6 -> TutorialStep6Solve(
                            grid = grid,
                            puzzle = viewModel.tutorialPuzzle,
                            stepDone = stepDone,
                            onCellTap = { r, c ->
                                viewModel.onTutorialCellTap(r, c)
                            },
                            onCellLongPress = { r, c ->
                                viewModel.onTutorialCellLongPress(r, c)
                            }
                        )
                        else -> TutorialStep7Ready(
                            onFinished = {
                                viewModel.completeTutorial()
                                onFinished()
                            }
                        )
                    }
                }
            }

            if (step < viewModel.totalSteps - 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    if (step > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(48.dp)
                                .background(
                                    SurfaceWhite,
                                    RoundedCornerShape(14.dp)
                                )
                                .border(
                                    1.dp,
                                    BorderLight,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { viewModel.prevStep() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = TextMedium,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    val canProceed = when (step) {
                        4, 5 -> stepDone
                        6 -> stepDone
                        else -> true
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .height(48.dp)
                            .widthIn(min = 120.dp)
                            .background(
                                if (canProceed) PrimaryBlue else BorderLight,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable(enabled = canProceed) {
                                viewModel.nextStep()
                            }
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = when {
                                    step in 4..6 && !stepDone -> "Try it!"
                                    else -> "Next"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TutorialStep0Welcome() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { visible = true }

        val scale by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "logoScale"
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(scale)
                .background(
                    brush = Brush.linearGradient(
                        listOf(PrimaryBlue, Color(0xFF1A5FBB))
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            val pattern = listOf(
                listOf(1, 0, 1),
                listOf(0, 1, 0),
                listOf(1, 1, 1)
            )
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                pattern.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        row.forEach { cell ->
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(
                                        if (cell == 1) Color.White
                                        else Color.White.copy(alpha = 0.2f),
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 300)) +
                slideInVertically(tween(500, delayMillis = 300)) { 30 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Welcome to\nNonogram!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 38.sp
                )
                Text(
                    text = "The logic puzzle where you\nreveal hidden pixel art!",
                    fontSize = 16.sp,
                    color = TextMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            PrimaryBlue.copy(alpha = 0.1f),
                            RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Let's learn how to play →",
                        fontSize = 13.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun TutorialStep1WhatIs() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "What is Nonogram?",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Fill in the correct cells to reveal\na hidden pixel art picture!",
            fontSize = 15.sp,
            color = TextMedium,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Empty Grid",
                    fontSize = 12.sp,
                    color = TextLight,
                    fontFamily = FontFamily.Monospace
                )
                MiniGridDisplay(
                    solution = List(4) { List(4) { 0 } },
                    showEmpty = true
                )
            }

            Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text("→", fontSize = 24.sp, color = PrimaryBlue)
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Solved!",
                    fontSize = 12.sp,
                    color = AccentGreen,
                    fontFamily = FontFamily.Monospace
                )
                MiniGridDisplay(
                    solution = listOf(
                        listOf(0, 1, 1, 0),
                        listOf(1, 1, 1, 1),
                        listOf(1, 1, 1, 1),
                        listOf(0, 1, 1, 0)
                    ),
                    showEmpty = false
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        listOf(
            Pair("🔢", "Numbers tell you how many cells to fill"),
            Pair("🧩", "Use logic - no guessing needed!"),
            Pair("🎨", "Reveal a hidden picture when done")
        ).forEach { (icon, text) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(icon, fontSize = 24.sp)
                Text(
                    text,
                    fontSize = 14.sp,
                    color = TextMedium,
                    lineHeight = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TutorialStep2Clues() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Reading the Clues",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Numbers show groups of filled cells\nin that row or column",
            fontSize = 15.sp,
            color = TextMedium,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        listOf(
            Triple("3", listOf(1, 1, 1, 0, 0), "Fill 3 cells in a row"),
            Triple("1 2", listOf(1, 0, 1, 1, 0), "Fill 1, gap, then fill 2"),
            Triple("2 1", listOf(0, 1, 1, 0, 1), "Fill 2, gap, then fill 1")
        ).forEach { (clue, pattern, desc) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .background(SurfaceWhite, RoundedCornerShape(14.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                PrimaryBlue.copy(alpha = 0.1f),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            clue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryBlue,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        pattern.forEach { cell ->
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(
                                        if (cell == 1) PrimaryBlue
                                        else Color(0xFFEEF3FB),
                                        RoundedCornerShape(5.dp)
                                    )
                                    .border(1.dp, BorderLight, RoundedCornerShape(5.dp))
                            )
                        }
                    }

                    Text(
                        desc,
                        fontSize = 12.sp,
                        color = TextMedium,
                        modifier = Modifier.weight(1f),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    AccentGold.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    AccentGold.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                )
                .padding(14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text("💡", fontSize = 18.sp)
                Text(
                    "There must be at least ONE empty cell\nbetween each group of filled cells!",
                    fontSize = 13.sp,
                    color = TextMedium,
                    lineHeight = 19.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TutorialStep3ReadClues() {
    var selectedRow by remember { mutableStateOf(-1) }

    val rows = listOf(
        Pair(listOf(2), listOf(0, 1, 1, 0, 0)),
        Pair(listOf(1, 1), listOf(1, 0, 1, 0, 0)),
        Pair(listOf(3), listOf(0, 1, 1, 1, 0)),
        Pair(listOf(1, 2), listOf(1, 0, 0, 1, 1)),
        Pair(listOf(0), listOf(0, 0, 0, 0, 0))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Tap a row to see\nhow the clue works",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextDark,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            rows.forEachIndexed { rowIdx, (clue, pattern) ->
                val isSelected = selectedRow == rowIdx
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) PrimaryBlue.copy(alpha = 0.06f)
                            else Color.Transparent
                        )
                        .clickable { selectedRow = rowIdx }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.width(50.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            clue.joinToString(" "),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) PrimaryBlue else TextMedium,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        pattern.forEachIndexed { i, cell ->
                            val delay = i * 50
                            var cellVis by remember(rowIdx, isSelected) {
                                mutableStateOf(!isSelected)
                            }
                            LaunchedEffect(isSelected, rowIdx) {
                                if (isSelected) {
                                    cellVis = false
                                    delay(delay.toLong())
                                    cellVis = true
                                }
                            }
                            val cellScale by animateFloatAsState(
                                targetValue = if (cellVis) 1f else 0.5f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                ),
                                label = "cellScale$i"
                            )
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .scale(cellScale)
                                    .background(
                                        if (cell == 1) PrimaryBlue
                                        else Color(0xFFEEF3FB),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.5.dp,
                                        if (isSelected && cell == 1) PrimaryBlue.copy(alpha = 0.6f)
                                        else BorderLight,
                                        RoundedCornerShape(8.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }

        if (selectedRow >= 0) {
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically { 20 }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            PrimaryBlue.copy(alpha = 0.08f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(14.dp)
                ) {
                    val (clue, _) = rows[selectedRow]
                    val explanation = when {
                        clue == listOf(0) ->
                            "Clue 0 means this entire row is empty!"
                        clue.size == 1 ->
                            "Fill exactly ${clue[0]} cells together"
                        else ->
                            "Fill ${clue[0]} cells, leave a gap, " +
                                "then fill ${clue[1]} cells"
                    }
                    Text(
                        explanation,
                        fontSize = 14.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "← Tap any row above",
                fontSize = 14.sp,
                color = TextLight,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TutorialStep4Fill(
    grid: List<List<Cell>>,
    highlighted: Set<Pair<Int, Int>>,
    onCellTap: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Fill the cells!",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Tap a cell to fill it.\nTap again to unfill it.",
            fontSize = 15.sp,
            color = TextMedium,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    PrimaryBlue.copy(alpha = 0.08f),
                    RoundedCornerShape(12.dp)
                )
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val infiniteTransition =
                    rememberInfiniteTransition(label = "hand")
                val handY by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 8f,
                    animationSpec = infiniteRepeatable(
                        tween(600),
                        RepeatMode.Reverse
                    ),
                    label = "handY"
                )
                Text(
                    "👆",
                    fontSize = 24.sp,
                    modifier = Modifier.offset(y = handY.dp)
                )
                Text(
                    "Fill the highlighted cells\nin the first row",
                    fontSize = 13.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TutorialGrid(
            grid = grid,
            highlightedCells = highlighted,
            activeRows = setOf(0),
            onCellTap = onCellTap,
            showRowClues = true,
            rowClues = listOf(
                listOf(2), listOf(2), listOf(0),
                listOf(2), listOf(3)
            ),
            colClues = listOf(
                listOf(1), listOf(2), listOf(0),
                listOf(2), listOf(1)
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TutorialStep5Mark(
    grid: List<List<Cell>>,
    highlighted: Set<Pair<Int, Int>>,
    onCellTap: (Int, Int) -> Unit,
    onCellLongPress: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Mark Empty Cells",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "When you KNOW a cell is empty,\nmark it with X to remember!",
            fontSize = 15.sp,
            color = TextMedium,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        PrimaryBlue.copy(alpha = 0.08f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PrimaryBlue, RoundedCornerShape(8.dp))
                    )
                    Text(
                        "TAP\nto fill",
                        fontSize = 11.sp,
                        color = PrimaryBlue,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        AccentRed.copy(alpha = 0.06f),
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        1.dp,
                        AccentRed.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFEEF3FB), RoundedCornerShape(8.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "✕",
                            fontSize = 18.sp,
                            color = AccentRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "TAP / LONG\nfor ✕",
                        fontSize = 11.sp,
                        color = AccentRed,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    AccentRed.copy(alpha = 0.06f),
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⚠️", fontSize = 16.sp)
                Text(
                    "X marks NEVER cost you a life!\nThey're just a helper tool.",
                    fontSize = 13.sp,
                    color = AccentRed,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Tap or long-press empty cells to mark ✕",
            fontSize = 14.sp,
            color = TextMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TutorialGrid(
            grid = grid,
            highlightedCells = highlighted,
            activeRows = (0..4).toSet(),
            onCellTap = onCellTap,
            onCellLongPress = onCellLongPress,
            showRowClues = true,
            rowClues = listOf(
                listOf(2), listOf(2), listOf(0),
                listOf(2), listOf(3)
            ),
            colClues = listOf(
                listOf(1), listOf(2), listOf(0),
                listOf(2), listOf(1)
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TutorialStep6Solve(
    grid: List<List<Cell>>,
    puzzle: List<List<Int>>,
    stepDone: Boolean,
    onCellTap: (Int, Int) -> Unit,
    onCellLongPress: (Int, Int) -> Unit
) {
    val rowClues = puzzle.map { row ->
        val clues = mutableListOf<Int>()
        var count = 0
        row.forEach {
            if (it == 1) count++
            else if (count > 0) {
                clues.add(count)
                count = 0
            }
        }
        if (count > 0) clues.add(count)
        if (clues.isEmpty()) listOf(0) else clues
    }
    val colClues = List(5) { c ->
        val col = List(5) { r -> puzzle[r][c] }
        val clues = mutableListOf<Int>()
        var count = 0
        col.forEach {
            if (it == 1) count++
            else if (count > 0) {
                clues.add(count)
                count = 0
            }
        }
        if (count > 0) clues.add(count)
        if (clues.isEmpty()) listOf(0) else clues
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            if (stepDone) "You solved it! 🎉" else "Solve this puzzle!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (stepDone) AccentGreen else TextDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            if (stepDone) "Amazing! You know how to play!"
            else "Use what you've learned.\nTap to fill, long press to mark.",
            fontSize = 14.sp,
            color = TextMedium,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        TutorialGrid(
            grid = grid,
            highlightedCells = emptySet(),
            activeRows = (0..4).toSet(),
            onCellTap = onCellTap,
            onCellLongPress = onCellLongPress,
            showRowClues = true,
            rowClues = rowClues,
            colClues = colClues
        )

        if (stepDone) {
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = stepDone,
                enter = scaleIn(
                    spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            AccentGreen.copy(alpha = 0.1f),
                            RoundedCornerShape(14.dp)
                        )
                        .border(
                            1.dp,
                            AccentGreen.copy(alpha = 0.3f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "You revealed a Smiley Face! 😊",
                        fontSize = 15.sp,
                        color = AccentGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TutorialStep7Ready(onFinished: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val infiniteTransition =
                rememberInfiniteTransition(label = "ready")
            val bounce by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -10f,
                animationSpec = infiniteRepeatable(
                    tween(800, easing = FastOutSlowInEasing),
                    RepeatMode.Reverse
                ),
                label = "bounce"
            )

            Text(
                "🎉",
                fontSize = 72.sp,
                modifier = Modifier.offset(y = bounce.dp)
            )

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 200)) +
                    slideInVertically(tween(600, delayMillis = 200)) { 40 }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "You're Ready!",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )
                    Text(
                        "You know everything needed\nto play Nonogram!",
                        fontSize = 16.sp,
                        color = TextMedium,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 500)) +
                    scaleIn(tween(600, delayMillis = 500))
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(
                        Pair("💡", "Use hints when you're stuck"),
                        Pair("♥", "You have 3 lives per puzzle"),
                        Pair("✕", "Mark empty cells to help solve"),
                        Pair("🔥", "Play daily to build your streak!")
                    ).forEach { (icon, tip) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceWhite, RoundedCornerShape(12.dp))
                                .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(icon, fontSize = 20.sp)
                            Text(
                                tip,
                                fontSize = 13.sp,
                                color = TextMedium
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 800)) +
                    slideInVertically(tween(400, delayMillis = 800)) { 30 }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(PrimaryBlue, Color(0xFF1A5FBB))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onFinished() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Start Playing! →",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TutorialGrid(
    grid: List<List<Cell>>,
    highlightedCells: Set<Pair<Int, Int>>,
    activeRows: Set<Int>,
    onCellTap: (Int, Int) -> Unit,
    onCellLongPress: (Int, Int) -> Unit = { _, _ -> },
    showRowClues: Boolean = false,
    rowClues: List<List<Int>> = emptyList(),
    colClues: List<List<Int>> = emptyList()
) {
    val cellSize = 44.dp
    val clueWidth = 40.dp
    val clueHeight = 36.dp

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (colClues.isNotEmpty()) {
            Row {
                Spacer(modifier = Modifier.width(clueWidth))
                colClues.forEach { clue ->
                    Box(
                        modifier = Modifier
                            .width(cellSize)
                            .height(clueHeight),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            clue.joinToString("\n"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = TextMedium,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        grid.forEachIndexed { r, row ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showRowClues && rowClues.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .width(clueWidth)
                            .height(cellSize),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            rowClues[r].joinToString(" "),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = TextMedium
                        )
                    }
                }

                row.forEachIndexed { c, cell ->
                    val isHighlighted = highlightedCells.contains(r to c)
                    val isActive = r in activeRows
                    val isFilled = cell.state == CellState.FILLED
                    val isMarked = cell.state == CellState.MARKED

                    val infiniteTransition =
                        rememberInfiniteTransition(label = "pulse$r$c")
                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 0.8f,
                        animationSpec = infiniteRepeatable(
                            tween(700),
                            RepeatMode.Reverse
                        ),
                        label = "pAlpha"
                    )

                    val bgColor by animateColorAsState(
                        targetValue = when {
                            isFilled -> PrimaryBlue
                            isHighlighted -> PrimaryBlue.copy(alpha = pulseAlpha)
                            !isActive -> Color(0xFFF5F5F5)
                            else -> Color(0xFFEEF3FB)
                        },
                        label = "cellBg"
                    )

                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .padding(3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgColor)
                            .border(
                                if (isHighlighted) 2.dp else 1.dp,
                                if (isHighlighted) PrimaryBlue else BorderLight,
                                RoundedCornerShape(8.dp)
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        if (isActive) onCellTap(r, c)
                                    },
                                    onLongPress = {
                                        if (isActive) onCellLongPress(r, c)
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isMarked) {
                            Text(
                                "✕",
                                fontSize = 16.sp,
                                color = AccentRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniGridDisplay(
    solution: List<List<Int>>,
    showEmpty: Boolean
) {
    val cellSize = 28.dp
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        solution.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                row.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .background(
                                when {
                                    cell == 1 -> PrimaryBlue
                                    showEmpty -> Color(0xFFEEF3FB)
                                    else -> Color(0xFFEEF3FB)
                                },
                                RoundedCornerShape(5.dp)
                            )
                            .border(1.dp, BorderLight, RoundedCornerShape(5.dp))
                    )
                }
            }
        }
    }
}
