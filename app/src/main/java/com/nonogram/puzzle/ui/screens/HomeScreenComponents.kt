package com.nonogram.puzzle.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nonogram.puzzle.data.model.PuzzleCategory
import com.nonogram.puzzle.utils.SoundManager
import com.nonogram.puzzle.ui.theme.AccentGold
import com.nonogram.puzzle.ui.theme.AccentGreen
import com.nonogram.puzzle.ui.theme.AppBackground
import com.nonogram.puzzle.ui.theme.BorderLight
import com.nonogram.puzzle.ui.theme.BorderMedium
import com.nonogram.puzzle.ui.theme.PrimaryBlue
import com.nonogram.puzzle.ui.theme.PrimaryLight
import com.nonogram.puzzle.ui.theme.SurfaceWhite
import com.nonogram.puzzle.ui.theme.TextDark
import com.nonogram.puzzle.ui.theme.TextLight
import com.nonogram.puzzle.ui.theme.TextMedium
import com.nonogram.puzzle.ui.theme.TextWhite
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DailyFeatureCard(onClick: () -> Unit) {
    val today = java.time.LocalDate.now()
    val dateStr =
        "${today.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)} ${today.dayOfMonth}"

    Box(
        modifier = Modifier
            .width(170.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF4A90E2), Color(0xFF2F6BC4))
                )
            )
            .clickable { onClick() }
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            repeat(12) { i ->
                val angle = i * 30f
                val rad = Math.toRadians(angle.toDouble())
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = center,
                    end = Offset(
                        (center.x + size.width * cos(rad)).toFloat(),
                        (center.y + size.height * sin(rad)).toFloat()
                    ),
                    strokeWidth = 20.dp.toPx()
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            AccentGold.copy(alpha = 0.9f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(22.dp)) {
                        val p = Path().apply {
                            moveTo(size.width / 2, 0f)
                            lineTo(size.width, size.height * 0.35f)
                            lineTo(size.width * 0.85f, size.height)
                            lineTo(size.width * 0.15f, size.height)
                            lineTo(0f, size.height * 0.35f)
                            close()
                        }
                        drawPath(p, Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "DAILY CHALLENGE",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextWhite.copy(alpha = 0.8f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = dateStr,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(
                        TextWhite.copy(alpha = 0.25f),
                        RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Play",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
        }
    }
}

@Composable
fun LeaderboardFeatureCard(
    completedCount: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(170.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF6B4EFF), Color(0xFF4A35CC))
                )
            )
            .clickable { onClick() }
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            repeat(12) { i ->
                val angle = i * 30f
                val rad = Math.toRadians(angle.toDouble())
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = center,
                    end = Offset(
                        (center.x + size.width * cos(rad)).toFloat(),
                        (center.y + size.height * sin(rad)).toFloat()
                    ),
                    strokeWidth = 20.dp.toPx()
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            AccentGold.copy(alpha = 0.9f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "\u2605",
                        fontSize = 22.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LEADERBOARD",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextWhite.copy(alpha = 0.8f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$completedCount",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite
                )
                Text(
                    text = "Levels done",
                    fontSize = 12.sp,
                    color = TextWhite.copy(alpha = 0.8f)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(
                        TextWhite.copy(alpha = 0.25f),
                        RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "View",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
        }
    }
}

@Composable
fun StreakFeatureCard(streak: Int) {
    Box(
        modifier = Modifier
            .width(170.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFF8C42), Color(0xFFE65C1A))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            repeat(12) { i ->
                val angle = i * 30f
                val rad = Math.toRadians(angle.toDouble())
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = center,
                    end = Offset(
                        (center.x + size.width * cos(rad)).toFloat(),
                        (center.y + size.height * sin(rad)).toFloat()
                    ),
                    strokeWidth = 20.dp.toPx()
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            TextWhite.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "\u25B2",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "STREAK",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextWhite.copy(alpha = 0.8f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$streak",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite
                )
                Text(
                    text = "days",
                    fontSize = 13.sp,
                    color = TextWhite.copy(alpha = 0.8f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                repeat(7) { i ->
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(6.dp)
                            .background(
                                if (i < streak.coerceAtMost(7)) TextWhite
                                else TextWhite.copy(alpha = 0.3f),
                                RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: PuzzleCategory,
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val accentColor = Color(category.colorDark)
    val progress = if (totalCount > 0) {
        completedCount.toFloat() / totalCount
    } else {
        0f
    }
    val animProg by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "prog"
    )
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.85f),
                        accentColor
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(300f, 300f)
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    SoundManager.getInstance(context).playCategorySelect()
                    onClick()
                }
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.06f),
                radius = 70.dp.toPx(),
                center = Offset(size.width * 0.85f, size.height * 0.2f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = 50.dp.toPx(),
                center = Offset(size.width * 0.7f, size.height * 0.9f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.displayName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$completedCount / $totalCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            completedCount == 0 -> "Get started!"
                            progress >= 1f -> "Completed!"
                            else -> "${(progress * 100).toInt()}% done"
                        },
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    if (progress >= 1f) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(
                                    Color.White.copy(alpha = 0.3f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "\u2713",
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(3.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animProg)
                            .fillMaxHeight()
                            .background(
                                Color.White,
                                RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun TodaysGoalsCard(
    levelsSolvedToday: Int,
    hintsUsedToday: Int,
    modifier: Modifier = Modifier
) {
    val now = java.time.LocalDateTime.now()
    val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
    val hoursLeft = java.time.Duration.between(now, midnight).toHours()
    val minsLeft = java.time.Duration.between(now, midnight).toMinutes() % 60

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceWhite, RoundedCornerShape(20.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Today's Goals",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(AccentGold, CircleShape)
                    )
                    Text(
                        "${hoursLeft}h ${minsLeft}m left",
                        fontSize = 11.sp,
                        color = TextMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            PrimaryBlue.copy(alpha = 0.07f),
                            RoundedCornerShape(14.dp)
                        )
                        .border(
                            1.dp,
                            PrimaryBlue.copy(alpha = 0.15f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "$levelsSolvedToday",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = "Levels\nSolved",
                            fontSize = 11.sp,
                            color = TextLight,
                            lineHeight = 15.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            AccentGold.copy(alpha = 0.07f),
                            RoundedCornerShape(14.dp)
                        )
                        .border(
                            1.dp,
                            AccentGold.copy(alpha = 0.2f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "$hintsUsedToday",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentGold
                        )
                        Text(
                            text = "Hints\nUsed",
                            fontSize = 11.sp,
                            color = TextLight,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LightStatCard(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SurfaceWhite, RoundedCornerShape(16.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextLight
            )
        }
    }
}
