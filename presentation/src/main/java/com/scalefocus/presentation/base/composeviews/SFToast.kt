package com.scalefocus.presentation.base.composeviews

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ShowToast(
    @StringRes messageId: Int
) {
    val context = LocalContext.current
    Toast.makeText(context, messageId, Toast.LENGTH_LONG).show()
}

fun showToast(
    @StringRes messageId: Int,
    context: Context
) {
    Toast.makeText(context, messageId, Toast.LENGTH_LONG).show()
}
