package com.pollub.awpfoc.ui.main

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pollub.awpfoc.ui.components.CallMenu
import com.pollub.awpfoc.ui.components.ConnectionStatus
import com.pollub.awpfoc.ui.components.RotatingLoader
import com.pollub.awpfoc.ui.components.SOSButton
import com.pollub.awpfoc.ui.theme.AwpfocTheme
import com.pollub.awpfoc.viewmodel.AppViewModel

/**
 * Composable function for the main screen of the application. This screen displays the connection statuses,
 * an SOS button, and a call menu for handling phone calls.
 *
 * @param modifier Optional [Modifier] to be applied to the root element.
 * @param viewModel [AppViewModel] containing necessary functionality and data.
 * @param phoneNumber Optional phone number to be displayed in the call menu.
 * @param requestCallPermissionLauncher Optional launcher for requesting call permissions.
 * @param onCallSOS Lambda function to be executed when the SOS button is clicked.
 * @param onCancelSOS Lambda function to be executed when the SOS cancel button is clicked.
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel,
    phoneNumber: String? = null,
    requestCallPermissionLauncher: ActivityResultLauncher<String>? = null,
    onCallSOS: (onSuccess:() -> Unit) -> Unit,
    onCancelSOS: (onSuccess:() -> Unit) -> Unit,
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFF4))
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        ConnectionStatus("Status połączenia z bazą", isActive = viewModel.isSystemConnected.value)
        ConnectionStatus("Status połączenia ze smartwatch", isActive = viewModel.isSmartWatchConnected.value)

        if(viewModel.getIsSystemConnecting().value){
        Spacer(modifier = Modifier.weight(1f))
        RotatingLoader(Modifier.align(Alignment.CenterHorizontally),MaterialTheme.colorScheme.primary, circleRadius = 42.dp, strokeWidth = 10.dp)
        Spacer(modifier = Modifier.weight(1f))
        }else{
            Spacer(modifier = Modifier.weight(1f))
        }

        SOSButton(
            isSosActive = viewModel.isSosActive,
            onButtonClick = {
                onCallSOS(){
                    viewModel.isSosActive.value = true
                }
            })

        Spacer(modifier = Modifier.height(40.dp))

        CallMenu(
            isCancelButtonVisible = viewModel.isSosActive.value,
            onCancelClick = {
                onCancelSOS(){
                    viewModel.isSosActive.value = false
                }
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
        val vm = AppViewModel()
        MainScreen(viewModel = vm, onCallSOS = {}, onCancelSOS = {})
    }
}
