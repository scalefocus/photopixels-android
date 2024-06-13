package io.photopixels.presentation.base.composeviews

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.photopixels.presentation.theme.SFPrimaryDarkBlue

@Composable
fun SFButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier? = Modifier,
    color: Color? = SFPrimaryDarkBlue,
    showLoader: Boolean? = false,
    enabled: Boolean = true
) {
    Button(
        modifier = modifier ?: Modifier,
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = color ?: SFPrimaryDarkBlue)
    ) {
        if (showLoader == true) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White
            )
        } else {
            Text(text = text)
        }
    }
}
