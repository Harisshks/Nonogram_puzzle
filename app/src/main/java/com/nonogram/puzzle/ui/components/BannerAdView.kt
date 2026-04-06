package com.nonogram.puzzle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.nonogram.puzzle.ui.theme.NonogramTheme
import com.nonogram.puzzle.ui.theme.Surface
import com.nonogram.puzzle.ui.theme.TextMuted
import com.nonogram.puzzle.utils.Constants

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Surface),
        contentAlignment = Alignment.Center
    ) {
        if (isPreview) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Banner Ad", color = TextMuted)
            }
        } else {
            AndroidView(
                factory = { context ->
                    AdView(context).apply {
                        setAdSize(AdSize.BANNER)
                        adUnitId = Constants.BANNER_AD_UNIT_ID
                        loadAd(AdRequest.Builder().build())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BannerAdPreview() {
    NonogramTheme {
        BannerAdView()
    }
}
