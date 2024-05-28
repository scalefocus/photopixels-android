package com.scalefocus.presentation.base.composeviews

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.scalefocus.presentation.R

@Composable
fun ShowAlertDialog(
    title: String,
    description: String,
    positiveButtonText: String = stringResource(id = R.string.button_ok),
    onPositiveClick: () -> Unit = {},
    negativeButtonText: String? = stringResource(id = R.string.button_cancel),
    onNegativeClick: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = {
            // Handle dismissal (optional)
        },
        confirmButton = {
            TextButton(onClick = onPositiveClick) {
                Text(text = positiveButtonText)
            }
        },
        dismissButton = {
            negativeButtonText?.let { text ->
                TextButton(onClick = onNegativeClick) {
                    Text(text = text)
                }
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = description)
        }
    )
}
