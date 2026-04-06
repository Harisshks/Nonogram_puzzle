package com.nonogram.puzzle.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nonogram.puzzle.ui.components.BannerAdView
import com.nonogram.puzzle.ui.theme.AccentRed
import com.nonogram.puzzle.ui.theme.AppBackground
import com.nonogram.puzzle.ui.theme.BorderLight
import com.nonogram.puzzle.ui.theme.NonogramTheme
import com.nonogram.puzzle.ui.theme.PrimaryBlue
import com.nonogram.puzzle.ui.theme.SurfaceWhite
import com.nonogram.puzzle.ui.theme.TextDark
import com.nonogram.puzzle.ui.theme.TextLight
import com.nonogram.puzzle.utils.PuzzleData
import com.nonogram.puzzle.utils.SoundManager
import com.nonogram.puzzle.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onBack: () -> Unit,
    onReplayTutorial: () -> Unit = {}
) {
    val context = LocalContext.current
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    val errorHighlight by viewModel.errorHighlight.collectAsStateWithLifecycle()
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()
    val completedCount by viewModel.completedCount.collectAsStateWithLifecycle()
    val perfectCount by viewModel.perfectCount.collectAsStateWithLifecycle()

    val contentColor = if (darkMode) Color(0xFFE8E6FF) else TextDark
    val cardColor = if (darkMode) Color(0xFF1A1A26) else SurfaceWhite
    val borderColor = if (darkMode) Color(0xFF2A2A3F) else BorderLight

    BackHandler {
        SoundManager.getInstance(context).playBackNav()
        onBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (darkMode) Color(0xFF0A0A0F) else AppBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                color = PrimaryBlue,
                shadowElevation = 0.dp
            ) {
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
                    Text(
                        text = "Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(36.dp))
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "GAMEPLAY",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (darkMode) Color(0xFF6E6A8A) else TextLight,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                }
                item {
                    SettingsToggleRow(
                        title = "Sound Effects",
                        checked = soundEnabled,
                        cardColor = cardColor,
                        borderColor = borderColor,
                        textColor = contentColor,
                        onCheckedChange = { viewModel.setSoundEnabled(it) }
                    )
                }
                item {
                    SettingsToggleRow(
                        title = "Vibration",
                        checked = vibrationEnabled,
                        cardColor = cardColor,
                        borderColor = borderColor,
                        textColor = contentColor,
                        onCheckedChange = { viewModel.setVibrationEnabled(it) }
                    )
                }
                item {
                    SettingsToggleRow(
                        title = "Error Highlight",
                        checked = errorHighlight,
                        cardColor = cardColor,
                        borderColor = borderColor,
                        textColor = contentColor,
                        onCheckedChange = { viewModel.setErrorHighlight(it) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "APPEARANCE",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (darkMode) Color(0xFF6E6A8A) else TextLight,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                }
                item {
                    SettingsToggleRow(
                        title = "Dark Theme",
                        checked = darkMode,
                        cardColor = cardColor,
                        borderColor = borderColor,
                        textColor = contentColor,
                        onCheckedChange = { viewModel.setDarkMode(it) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "STATISTICS",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (darkMode) Color(0xFF6E6A8A) else TextLight,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardColor, RoundedCornerShape(14.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                    ) {
                        Column {
                            listOf(
                                Pair("Puzzles Solved", "$completedCount / ${PuzzleData.all.size}"),
                                Pair("Perfect Solves", "$perfectCount"),
                                Pair("Version", "1.0.0")
                            ).forEachIndexed { index, (label, value) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = contentColor
                                    )
                                    Text(
                                        value,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (darkMode) Color(0xFF9995B8) else TextLight
                                    )
                                }
                                if (index < 2) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .padding(horizontal = 16.dp)
                                            .background(borderColor)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
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
                            .clickable {
                                SoundManager.getInstance(context).playButtonTap()
                                onReplayTutorial()
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Replay Tutorial",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                AccentRed.copy(alpha = 0.06f),
                                RoundedCornerShape(14.dp)
                            )
                            .border(
                                1.dp,
                                AccentRed.copy(alpha = 0.2f),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.resetAllProgress() }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Reset All Progress",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentRed
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    BannerAdView(modifier = Modifier.fillMaxWidth())
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    checked: Boolean,
    cardColor: Color,
    borderColor: Color,
    textColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardColor, RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryBlue,
                    uncheckedThumbColor = Color(0xFFAAAAAA),
                    uncheckedTrackColor = borderColor
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    NonogramTheme {
        SettingsScreen(onBack = {})
    }
}
