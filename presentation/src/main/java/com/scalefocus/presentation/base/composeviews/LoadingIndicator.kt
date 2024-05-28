package com.scalefocus.presentation.base.composeviews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.scalefocus.presentation.theme.SFPrimaryDarkBlue

@Composable
fun CircularIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CircularProgressIndicator(
            color = SFPrimaryDarkBlue,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
