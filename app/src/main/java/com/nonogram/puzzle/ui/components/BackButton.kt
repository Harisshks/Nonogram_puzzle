package com.nonogram.puzzle.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nonogram.puzzle.ui.theme.Border
import com.nonogram.puzzle.ui.theme.NonogramTheme
import com.nonogram.puzzle.ui.theme.Surface
import com.nonogram.puzzle.ui.theme.TextPrimary

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(38.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Surface),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Text("←", color = TextPrimary, fontSize = 18.sp)
    }
}

@Preview(showBackground = true)
@Composable
private fun BackButtonPreview() {
    NonogramTheme {
        Box(contentAlignment = Alignment.Center) {
            BackButton(onClick = {})
        }
    }
}
