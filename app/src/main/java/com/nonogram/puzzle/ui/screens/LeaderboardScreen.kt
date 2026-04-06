package com.nonogram.puzzle.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nonogram.puzzle.data.model.PuzzleCategory
import com.nonogram.puzzle.ui.theme.AppBackground
import com.nonogram.puzzle.ui.theme.BorderLight
import com.nonogram.puzzle.ui.theme.PrimaryBlue
import com.nonogram.puzzle.ui.theme.PrimaryDark
import com.nonogram.puzzle.ui.theme.SurfaceWhite
import com.nonogram.puzzle.ui.theme.TextDark
import com.nonogram.puzzle.ui.theme.TextLight
import com.nonogram.puzzle.utils.PuzzleData
import com.nonogram.puzzle.utils.SoundManager
import com.nonogram.puzzle.viewmodel.HomeViewModel

@Composable
fun LeaderboardScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val completedCount by homeViewModel.completedCount.collectAsStateWithLifecycle()
    val perfectCount by homeViewModel.perfectCount.collectAsStateWithLifecycle()
    val totalTime by homeViewModel.totalTimeSolved.collectAsStateWithLifecycle()
    val streak by homeViewModel.streak.collectAsStateWithLifecycle()
    val completedIds by homeViewModel.completedPuzzleIds.collectAsStateWithLifecycle()

    BackHandler {
        SoundManager.getInstance(context).playBackNav()
        onBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Surface(
                color = PrimaryBlue,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
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
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Leaderboard",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(PrimaryBlue, PrimaryDark)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Your Stats",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                listOf(
                                    Triple("$completedCount", "Solved", "◈"),
                                    Triple("$perfectCount", "Perfect", "★"),
                                    Triple("$streak", "Streak", "▲"),
                                    Triple(
                                        homeViewModel.formatTotalTime(totalTime),
                                        "Time",
                                        "◎"
                                    )
                                ).forEach { (value, label, icon) ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            icon,
                                            fontSize = 16.sp,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                        Text(
                                            value,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            label,
                                            fontSize = 10.sp,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Category Progress",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                items(
                    items = PuzzleCategory.entries.toList(),
                    key = { it.name }
                ) { cat ->
                    val catPuzzles = PuzzleData.getByCategory(cat)
                    val completed = completedIds.count { id ->
                        catPuzzles.any { it.id == id }
                    }
                    val total = catPuzzles.size
                    val pct = if (total > 0) completed * 100 / total else 0
                    val accentColor = Color(cat.colorDark)
                    val lightColor = Color(cat.colorLight)
                    val animProg by animateFloatAsState(
                        targetValue = pct / 100f,
                        animationSpec = tween(1000),
                        label = "prog"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        accentColor.copy(alpha = 0.12f),
                                        lightColor
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                accentColor.copy(alpha = 0.25f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(48.dp)
                                    .background(accentColor, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        cat.displayName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextDark
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                accentColor.copy(alpha = 0.12f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "$completed / $total",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accentColor,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(7.dp)
                                        .background(
                                            accentColor.copy(alpha = 0.12f),
                                            RoundedCornerShape(4.dp)
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(animProg)
                                            .fillMaxHeight()
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    listOf(
                                                        accentColor.copy(alpha = 0.7f),
                                                        accentColor
                                                    )
                                                ),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when {
                                        pct == 0 -> "Not started yet"
                                        pct == 100 -> "Fully completed!"
                                        else -> "$pct% complete"
                                    },
                                    fontSize = 10.sp,
                                    color = accentColor.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                PrimaryBlue.copy(alpha = 0.06f),
                                RoundedCornerShape(14.dp)
                            )
                            .border(
                                1.dp,
                                PrimaryBlue.copy(alpha = 0.2f),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Global Leaderboard",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                            Text(
                                "Coming soon! Compete with players worldwide.",
                                fontSize = 12.sp,
                                color = TextLight,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
