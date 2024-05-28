package com.scalefocus.presentation.base.composeviews

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.scalefocus.presentation.theme.SFPrimaryDarkBlue

@Composable
fun SFButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier? = Modifier,
    color: Color? = SFPrimaryDarkBlue,
    enabled: Boolean = true
) {
    Button(
        modifier = modifier ?: Modifier,
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = color ?: SFPrimaryDarkBlue)
    ) {
        Text(text = text)
    }
}
