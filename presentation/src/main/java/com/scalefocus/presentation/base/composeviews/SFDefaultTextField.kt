package com.scalefocus.presentation.base.composeviews

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scalefocus.presentation.theme.PhotoPixelsTheme

@Composable
fun SFDefaultTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    @StringRes errorText: Int? = null,
) {
    TextField(
        modifier = modifier
            .padding(bottom = 0.dp),
        value = value,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        supportingText = {
            if (isError) {
                errorText?.let {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = it),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSfDefaultTextField() {
    PhotoPixelsTheme {
        SFDefaultTextField(value = "John Smith", modifier = Modifier, label = "Name", onValueChange = {})
    }
}
