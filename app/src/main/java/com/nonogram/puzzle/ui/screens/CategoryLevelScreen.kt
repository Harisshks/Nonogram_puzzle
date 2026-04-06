package com.nonogram.puzzle.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nonogram.puzzle.data.model.PuzzleCategory
import com.nonogram.puzzle.ui.theme.AppBackground
import com.nonogram.puzzle.utils.PuzzleData
import com.nonogram.puzzle.utils.SoundManager
import com.nonogram.puzzle.viewmodel.HomeViewModel

@Composable
fun CategoryLevelScreen(
    category: PuzzleCategory,
    homeViewModel: HomeViewModel = viewModel(),
    onLevelSelected: (puzzleId: Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val completedIds by homeViewModel.completedPuzzleIds.collectAsStateWithLifecycle()
    val unlockedMax by homeViewModel.unlockedMaxLevel(category).collectAsStateWithLifecycle()
    val puzzles = PuzzleData.getByCategory(category)
    val accentColor = Color(category.colorDark)
    val completedInCat = puzzles.count { it.id in completedIds }
    val totalInCat = puzzles.size
    val progress = if (totalInCat > 0) {
        completedInCat.toFloat() / totalInCat
    } else {
        0f
    }

    LaunchedEffect(Unit) { homeViewModel.refreshCompletedIds() }

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
                color = accentColor,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 14.dp, bottom = 0.dp),
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
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = category.displayName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.size(36.dp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(
                            Pair("$completedInCat", "Solved"),
                            Pair("${totalInCat - completedInCat}", "Remaining"),
                            Pair("${(progress * 100).toInt()}%", "Progress")
                        ).forEach { (value, label) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = value,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        val animProg by animateFloatAsState(
                            targetValue = progress,
                            animationSpec = tween(1000),
                            label = "topProg"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animProg)
                                .fillMaxHeight()
                                .background(Color.White)
                        )
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
                itemsIndexed(puzzles, key = { _, p -> p.id }) { index, puzzle ->
                    val isCompleted = puzzle.id in completedIds
                    val isUnlocked = PuzzleData.isLevelUnlocked(
                        puzzle.levelNumber,
                        unlockedMax
                    )
                    val nextIndex = puzzles.indexOfFirst { p ->
                        p.id !in completedIds &&
                            PuzzleData.isLevelUnlocked(p.levelNumber, unlockedMax)
                    }
                    val isNext = nextIndex >= 0 && index == nextIndex

                    LevelButton(
                        level = puzzle.levelNumber,
                        isCompleted = isCompleted,
                        isUnlocked = isUnlocked,
                        isNext = isNext,
                        color = accentColor,
                        onClick = {
                            if (isUnlocked) onLevelSelected(puzzle.id)
                        }
                    )
                }
            }
        }
    }
}
