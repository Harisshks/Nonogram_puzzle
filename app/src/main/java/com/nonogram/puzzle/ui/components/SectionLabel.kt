package com.nonogram.puzzle.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nonogram.puzzle.ui.theme.NonogramTheme
import com.nonogram.puzzle.ui.theme.TextMuted

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        color = TextMuted,
        letterSpacing = 2.sp
    )
}

@Preview(showBackground = true)
@Composable
private fun SectionLabelPreview() {
    NonogramTheme {
        SectionLabel(text = "Section")
    }
}
