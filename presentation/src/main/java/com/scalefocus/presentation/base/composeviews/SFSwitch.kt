package com.scalefocus.presentation.base.composeviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scalefocus.presentation.theme.AppTypography
import com.scalefocus.presentation.theme.PhotoPixelsTheme
import com.scalefocus.presentation.theme.SFPrimaryDarkBlue
import com.scalefocus.presentation.theme.SFSecondaryLightBlue

@Composable
fun SFSwitch(
    checked: Boolean,
    text: String,
    onValueChange: (Boolean) -> Unit,
    textStyle: TextStyle = TextStyle.Default
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, modifier = Modifier.weight(0.5f), style = textStyle)

        Switch(
            checked = checked,
            enabled = true,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SFPrimaryDarkBlue,
                checkedTrackColor = SFSecondaryLightBlue
            ),
            onCheckedChange = { changedValue ->
                onValueChange(changedValue)
            }
        )
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun PreviewSFSwitch() {
    PhotoPixelsTheme {
        SFSwitch(
            text = "Require power for sync",
            checked = true,
            textStyle = AppTypography.BigBlue,
            onValueChange = {}
        )
    }
}
