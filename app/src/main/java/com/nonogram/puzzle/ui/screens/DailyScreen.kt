package com.nonogram.puzzle.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import java.time.format.DateTimeFormatter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nonogram.puzzle.ui.components.BannerAdView
import com.nonogram.puzzle.ui.components.MiniPuzzlePreview
import com.nonogram.puzzle.ui.components.PrimaryButton
import com.nonogram.puzzle.ui.components.SectionLabel
import com.nonogram.puzzle.ui.components.DifficultyBadge
import com.nonogram.puzzle.ui.theme.AppBackground
import com.nonogram.puzzle.ui.theme.BorderLight
import com.nonogram.puzzle.ui.theme.PrimaryBlue
import com.nonogram.puzzle.ui.theme.SurfaceWhite
import com.nonogram.puzzle.ui.theme.TextLight
import com.nonogram.puzzle.ui.theme.Accent
import com.nonogram.puzzle.ui.theme.Background
import com.nonogram.puzzle.ui.theme.Border
import com.nonogram.puzzle.ui.theme.Card as CardColor
import com.nonogram.puzzle.ui.theme.Gold
import com.nonogram.puzzle.ui.theme.NonogramTheme
import com.nonogram.puzzle.ui.theme.Surface
import com.nonogram.puzzle.ui.theme.TextMuted
import com.nonogram.puzzle.ui.theme.TextPrimary
import com.nonogram.puzzle.utils.PuzzleData
import com.nonogram.puzzle.utils.SoundManager
import com.nonogram.puzzle.viewmodel.DailyViewModel
import androidx.compose.foundation.BorderStroke
import com.nonogram.puzzle.ui.theme.AppSpacing
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

fun formatRelativeDate(timestamp: Long): String {
    val date = Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault()).toLocalDate()
    val today = java.time.LocalDate.now()
    return when {
        date == today -> "Today"
        date == today.minusDays(1) -> "Yesterday"
        else -> "${ChronoUnit.DAYS.between(date, today)} days ago"
    }
}

@Composable
fun DailyScreen(
    viewModel: DailyViewModel = viewModel(),
    onBack: () -> Unit,
    onPlayDaily: () -> Unit
) {
    val context = LocalContext.current
    val timeUntilNext by viewModel.timeUntilNext.collectAsStateWithLifecycle()
    val streak by viewModel.streak.collectAsStateWithLifecycle()
    val isDailyCompleted by viewModel.isDailyCompleted.collectAsStateWithLifecycle()
    val last7Days by viewModel.last7Days.collectAsStateWithLifecycle()
    val pastDailies by viewModel.pastDailies.collectAsStateWithLifecycle()
    val puzzle = viewModel.dailyPuzzle
    val (h, m, s) = timeUntilNext

    BackHandler {
        SoundManager.getInstance(context).playBackNav()
        onBack()
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            Surface(color = PrimaryBlue, shadowElevation = 0.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp)
                        .padding(top = 12.dp, bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
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
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Daily Challenge",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        val today = java.time.LocalDate.now()
                        val fmt = DateTimeFormatter.ofPattern("MMMM d, yyyy")
                        Text(
                            text = today.format(fmt),
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.size(36.dp))
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                horizontal = AppSpacing.screenHorizontal,
                vertical = AppSpacing.screenVertical
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.blockGap)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Accent.copy(alpha = 0.15f), CardColor),
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(200f, 200f)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(1.5.dp, Accent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SectionLabel("TODAY'S PUZZLE")
                        Text(
                            text = puzzle.title,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        DifficultyBadge(puzzle.difficulty)
                        Box(
                            modifier = Modifier.blur(6.dp)
                        ) {
                            MiniPuzzlePreview(
                                solution = puzzle.solution,
                                cellSize = 28.dp
                            )
                        }
                        if (isDailyCompleted) {
                            Text(
                                text = "COMPLETED TODAY",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        } else {
                            PrimaryButton(
                                text = "PLAY NOW",
                                onClick = { onPlayDaily() }
                            )
                        }
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceWhite, RoundedCornerShape(16.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "NEXT PUZZLE IN",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextLight,
                            letterSpacing = 2.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Pair(h.toString().padStart(2, '0'), "HRS"),
                                Pair(m.toString().padStart(2, '0'), "MIN"),
                                Pair(s.toString().padStart(2, '0'), "SEC")
                            ).forEach { (value, unit) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            AppBackground,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            1.dp,
                                            BorderLight,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(vertical = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = value,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontFamily = FontFamily.Monospace,
                                            color = PrimaryBlue
                                        )
                                        Text(
                                            text = unit,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = TextLight,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardColor),
                    border = BorderStroke(1.dp, Border),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Your streak", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("$streak days", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Gold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            last7Days.forEach { (label, wasPlayed) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(label, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = TextMuted)
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                if (wasPlayed) Gold else Border.copy(alpha = 0.3f),
                                                RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (wasPlayed) {
                                            Text("·", fontSize = 12.sp, color = Background)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                SectionLabel("PAST PUZZLES")
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.tightGap)) {
                    pastDailies.forEach { progress ->
                        val p = PuzzleData.getById(progress.puzzleId)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            border = BorderStroke(1.dp, Border),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = AppSpacing.blockGap,
                                        vertical = AppSpacing.itemGap
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        p?.title ?: "Puzzle ${progress.puzzleId}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        formatRelativeDate(progress.lastUpdated),
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    p?.let { DifficultyBadge(it.difficulty) }
                                    Text("${progress.stars}★", fontSize = 12.sp, color = Gold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                BannerAdView(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DailyScreenPreview() {
    NonogramTheme {
        Text("Daily — use preview with ViewModel")
    }
}
