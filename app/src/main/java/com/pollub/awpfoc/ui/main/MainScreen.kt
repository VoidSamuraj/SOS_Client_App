package com.pollub.awpfoc.ui.main

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.pollub.awpfoc.ui.components.CallMenu
import com.pollub.awpfoc.ui.components.ConnectionStatus
import com.pollub.awpfoc.ui.components.PatrolStatus
import com.pollub.awpfoc.ui.components.RotatingLoader
import com.pollub.awpfoc.ui.components.SOSButton
import com.pollub.awpfoc.ui.theme.AwpfocTheme
import com.pollub.awpfoc.viewmodel.AppViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Composable function for the main screen of the application. This screen displays the connection statuses,
 * an SOS button, and a call menu for handling phone calls.
 *
 * @param modifier Optional [Modifier] to be applied to the root element.
 * @param viewModel [AppViewModel] containing necessary functionality and data.
 * @param phoneNumber Optional phone number to be displayed in the call menu.
 * @param protectionExpirationDate Optional expiration date of account to display and block sos button.
 * @param requestCallPermissionLauncher Optional launcher for requesting call permissions.
 * @param onCallSOS Lambda function to be executed when the SOS button is clicked.
 * @param onCancelSOS Lambda function to be executed when the SOS cancel button is clicked.
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel,
    phoneNumber: String? = null,
    protectionExpirationDate: String? = null,
    requestCallPermissionLauncher: ActivityResultLauncher<String>? = null,
    onCallSOS: (onSuccess: () -> Unit) -> Unit,
    onCancelSOS: (onSuccess: () -> Unit) -> Unit,
) {

    var expirationDate: MutableState<LocalDateTime?> = remember { mutableStateOf(null) }
    var areButtonsActive: MutableState<Boolean> = remember { mutableStateOf(true) }
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm")

    LaunchedEffect(protectionExpirationDate) {
        if (protectionExpirationDate != null) {
            expirationDate.value =
                LocalDateTime.parse(protectionExpirationDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            areButtonsActive.value = LocalDateTime.now().isBefore(expirationDate.value)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFF4))
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        ConnectionStatus("Status połączenia z bazą", isActive = viewModel.isSystemConnected.value)
        ConnectionStatus(
            "Status połączenia ze smartwatch",
            isActive = viewModel.isSmartWatchConnected.value
        )


        Spacer(modifier = Modifier.weight(1f))
        Box(Modifier.align(Alignment.CenterHorizontally)) {
            PatrolStatus(Modifier.zIndex(1f), 200f, viewModel.reportState.value)
            if (viewModel.getIsSystemConnecting().value)
                RotatingLoader(
                    Modifier
                        .zIndex(2f)
                        .align(Alignment.Center),
                    MaterialTheme.colorScheme.primary,
                    circleRadius = 42.dp,
                    strokeWidth = 10.dp
                )
        }
        Spacer(modifier = Modifier.weight(1f))


        if (expirationDate.value != null) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .background(
                        if (areButtonsActive.value) Color(0x99089D01) else Color(0x999D0801),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    text = if (areButtonsActive.value)
                        "Ochrona ważna do"
                    else
                        "Ochrona wygasła"
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    text = "${expirationDate.value!!.format(formatter)}"
                )
            }
        }

        SOSButton(
            isButtonActive = areButtonsActive,
            isSosActive = viewModel.isSosActive,
            isSystemConnecting = viewModel.getIsSystemConnecting(),
            onButtonClick = {
                onCallSOS() {
                    viewModel.isSosActive.value = true
                }
            })

        Spacer(modifier = Modifier.height(40.dp))

        CallMenu(
            isCancelButtonVisible = viewModel.isSosActive.value,
            isSystemConnecting = viewModel.getIsSystemConnecting(),
            onCancelClick = {
                onCancelSOS() {
                    viewModel.isSosActive.value = false
                    viewModel.reportState.value = AppViewModel.Companion.ReportState.NONE
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
