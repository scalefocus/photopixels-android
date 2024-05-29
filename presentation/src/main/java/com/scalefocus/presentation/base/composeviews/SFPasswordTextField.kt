package com.scalefocus.presentation.base.composeviews

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scalefocus.presentation.R
import com.scalefocus.presentation.theme.PhotoPixelsTheme

@Composable
fun SFPasswordTextField(
    password: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    label: String = stringResource(id = R.string.login_password),
    @StringRes errorText: Int? = null,
) {
    var showPassword by remember { mutableStateOf(value = false) }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp),
        value = password,
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
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            if (showPassword) {
                IconButton(onClick = { showPassword = false }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_visibility_24),
                        contentDescription = "hide_password"
                    )
                }
            } else {
                IconButton(onClick = { showPassword = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_visibility_off_24),
                        contentDescription = "hide_password"
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSfPasswordTextField() {
    PhotoPixelsTheme {
        SFPasswordTextField(password = "password", onValueChange = {})
    }
}
