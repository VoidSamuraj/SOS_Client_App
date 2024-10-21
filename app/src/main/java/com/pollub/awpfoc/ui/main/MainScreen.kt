package com.pollub.awpfoc.ui.main

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pollub.awpfoc.ui.components.CallMenu
import com.pollub.awpfoc.ui.components.ConnectionStatus
import com.pollub.awpfoc.ui.components.SOSButton
import com.pollub.awpfoc.ui.theme.AwpfocTheme

/**
 * Composable function for the main screen of the application. This screen displays the connection statuses,
 * an SOS button, and a call menu for handling phone calls.
 *
 * @param modifier Optional [Modifier] to be applied to the root element.
 * @param phoneNumber Optional phone number to be displayed in the call menu.
 * @param requestCallPermissionLauncher Optional launcher for requesting call permissions.
 * @param onCallSOS Lambda function to be executed when the SOS button is clicked.
 * @param onCancelSOS Lambda function to be executed when the SOS cancel button is clicked.
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    phoneNumber: String? = null,
    requestCallPermissionLauncher: ActivityResultLauncher<String>? = null,
    onCallSOS: () -> Unit,
    onCancelSOS: () -> Unit,
) {

    val isSystemConnected = remember { mutableStateOf(false) }
    val isSmartWatchConnected = remember { mutableStateOf(false) }
    val isSosActive = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFF4))
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        ConnectionStatus("Status połączenia z bazą", isActive = isSystemConnected.value)
        ConnectionStatus("Status połączenia ze smartwatch", isActive = isSmartWatchConnected.value)

        Spacer(modifier = Modifier.height(40.dp))
        Spacer(modifier = Modifier.weight(1f))

        SOSButton(
            isSosActive = isSosActive,
            onButtonClick = {
                isSosActive.value = true
                onCallSOS()
            })

        Spacer(modifier = Modifier.height(40.dp))

        CallMenu(
            isCancelButtonVisible = isSosActive.value,
            onCancelClick = {
                isSosActive.value = false
                onCancelSOS()
            },
            phoneNumber = phoneNumber,
            requestPermissionLauncher = requestCallPermissionLauncher
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewClientAppUI() {
    AwpfocTheme(dynamicColor = false) {
        MainScreen(onCallSOS = {}, onCancelSOS = {})
    }
}
