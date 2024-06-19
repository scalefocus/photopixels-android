package io.photopixels.presentation.screens.settings

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.photopixels.presentation.R
import io.photopixels.presentation.utils.Utils
import timber.log.Timber

@Suppress("LambdaParameterInRestartableEffect")
@Composable
fun SettingsScreen(onNavigateToConnectServerScreen: () -> Unit, viewModel: SettingsScreenViewModel = hiltViewModel()) {
    val screenState = viewModel.state.collectAsStateWithLifecycle().value
    val appVersion = Utils.getVersionName(LocalContext.current)
    val context = LocalContext.current

    val googleAuthorizationLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle successful result from the activity
                val data = result.data ?: return@rememberLauncherForActivityResult
                // Extract data from the Intent
                Timber.tag("TAG").e("Submit OnGoogleOauthIntentReceived ACTION!!!!")
                viewModel.submitAction(SettingsScreenActions.OnGoogleOauthIntentReceived(data))
            } else {
                Timber.tag("TAG").e("OnActivity Result FAILED result code:${result.resultCode}")
                // Handle failed result (e.g., canceled)
                // TODO: Handle error, and show msg to the user
            }
        }

    LaunchedEffect(Unit) {
        viewModel.submitAction(SettingsScreenActions.LoadSettingsData(appVersion))
        viewModel.events.collect { settingsEvent ->
            when (settingsEvent) {
                SettingsScreenEvents.NavigateToConnectServerScreen -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.settings_screen_logout_success),
                        Toast.LENGTH_LONG
                    ).show()
                    onNavigateToConnectServerScreen()
                }

                is SettingsScreenEvents.StartAuthorizationIntent -> {
                    googleAuthorizationLauncher.launch(settingsEvent.authorizationIntent)
                }
            }
        }
    }

    SettingsScreenContent(screenState = screenState, onSubmitAction = { viewModel.submitAction(it) })
}
