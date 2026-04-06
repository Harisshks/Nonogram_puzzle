package com.nonogram.puzzle.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nonogram.puzzle.ui.components.BannerAdView
import com.nonogram.puzzle.ui.theme.AccentGold
import com.nonogram.puzzle.ui.theme.AccentGreen
import com.nonogram.puzzle.ui.theme.AppBackground
import com.nonogram.puzzle.ui.theme.BorderLight
import com.nonogram.puzzle.ui.theme.TextDark
import com.nonogram.puzzle.ui.theme.PrimaryBlue
import com.nonogram.puzzle.ui.theme.PrimaryDark
import com.nonogram.puzzle.utils.SoundManager
import com.nonogram.puzzle.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onPlayClick: () -> Unit,
    onDailyClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLeaderboardClick: () -> Unit
) {
    val completedCount by viewModel.completedCount.collectAsStateWithLifecycle()
    val streak by viewModel.streak.collectAsStateWithLifecycle()
    val perfectCount by viewModel.perfectCount.collectAsStateWithLifecycle()
    val totalTimeSolved by viewModel.totalTimeSolved.collectAsStateWithLifecycle()
    val levelsSolvedToday by viewModel.levelsSolvedToday.collectAsStateWithLifecycle()
    val hintsUsedToday by viewModel.hintsUsedToday.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.refreshCompletedIds() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Surface(color = PrimaryBlue, shadowElevation = 4.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "NONOGRAM",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f),
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "Puzzle Game",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (streak > 0) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            AccentGold.copy(alpha = 0.25f),
                                            RoundedCornerShape(50.dp)
                                        )
                                        .border(
                                            1.dp,
                                            Color.White.copy(alpha = 0.35f),
                                            RoundedCornerShape(50.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("\u25B2", fontSize = 10.sp, color = AccentGold)
                                        Text(
                                            "$streak",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSettingsClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Featured",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(start = 20.dp, bottom = 10.dp, top = 16.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        DailyFeatureCard(
                            onClick = {
                                SoundManager.getInstance(context).playButtonTap()
                                onDailyClick()
                            }
                        )
                    }
                    item {
                        LeaderboardFeatureCard(
                            completedCount = completedCount,
                            onClick = onLeaderboardClick
                        )
                    }
                    item { StreakFeatureCard(streak = streak) }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                TodaysGoalsCard(
                    levelsSolvedToday = levelsSolvedToday,
                    hintsUsedToday = hintsUsedToday,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(72.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = PrimaryBlue.copy(alpha = 0.3f)
                        )
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(PrimaryBlue, PrimaryDark)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            SoundManager.getInstance(context).playButtonTap()
                            onPlayClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Resume",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = "Play",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LightStatCard(
                        value = "$completedCount",
                        label = "Solved",
                        color = PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    LightStatCard(
                        value = "$perfectCount",
                        label = "Perfect",
                        color = AccentGold,
                        modifier = Modifier.weight(1f)
                    )
                    LightStatCard(
                        value = viewModel.formatTotalTime(totalTimeSolved),
                        label = "Played",
                        color = AccentGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                BannerAdView(modifier = Modifier.padding(horizontal = 20.dp))
            }
        }
    }
}
