package com.scalefocus.presentation.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.scalefocus.presentation.R
import com.scalefocus.presentation.theme.PhotoPixelsTheme

@Composable
fun SplashScreenContent(screenState: SplashScreenState) {
    if (isSystemInDarkTheme()) {
        DarkContent()
    } else {
        LightContent()
    }
}

@Composable
private fun LightContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logophotopixels2_light),
            contentDescription = "logo"
        )
    }
}

@Composable
private fun DarkContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logophotopixels2_dark),
            contentDescription = "logo"
        )
    }
}

// Issue with preview https://issuetracker.google.com/issues/324732800?pli=1
// use apiLevel = 33 for workaround
@Preview(showBackground = true, apiLevel = 33)
@Composable
private fun PreviewSplashScreenContent() {
    PhotoPixelsTheme {
        SplashScreenContent(SplashScreenState())
    }
}
