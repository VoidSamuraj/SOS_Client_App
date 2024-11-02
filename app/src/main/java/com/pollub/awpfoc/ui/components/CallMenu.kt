package com.pollub.awpfoc.ui.components

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pollub.awpfoc.R
import com.pollub.awpfoc.ui.theme.AwpfocTheme
import com.pollub.awpfoc.utils.makePhoneCall


/**
 * Composable function that displays a call menu with an optional cancel button and a call button.
 *
 * @param isCancelButtonVisible Boolean indicating if the cancel button should be displayed.
 * @param isSystemConnecting MutableState<Boolean> that indicates whether the SOS feature is still connecting/reconnecting.
 * @param onCancelClick Lambda function to be executed when the cancel button is clicked.
 * @param phoneNumber Optional phone number to be used for making a call.
 * @param requestPermissionLauncher Optional launcher for requesting permissions needed for calling.
 */
@Composable
fun CallMenu(
    isCancelButtonVisible: Boolean,
    isSystemConnecting: MutableState<Boolean>,
    onCancelClick: () -> Unit,
    phoneNumber: String? = null,
    requestPermissionLauncher: ActivityResultLauncher<String>? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (isCancelButtonVisible)
            Button(
                onClick = onCancelClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(80.dp),
                enabled = !isSystemConnecting.value
            ) {
                Text(
                    text = "ANULUJ SOS",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        else
            Spacer(modifier = Modifier.weight(1f))
        CallButton(phoneNumber, requestPermissionLauncher)

    }
}

/**
 * Composable function that displays a call button to initiate a phone call.
 *
 * @param phoneNumber Optional phone number to be used for making a call.
 * @param requestPermissionLauncher Optional launcher for requesting permissions needed for calling.
 */
@Composable
fun CallButton(
    phoneNumber: String? = null,
    requestPermissionLauncher: ActivityResultLauncher<String>? = null
) {
    val context = LocalContext.current
    Button(
        onClick = {
            if (requestPermissionLauncher != null && phoneNumber != null)
                makePhoneCall(context, requestPermissionLauncher, phoneNumber)
        },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_phone_24),
            contentDescription = "Call",
            modifier = Modifier
                .rotate(225f)
                .fillMaxSize(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CallPreview() {
    AwpfocTheme(dynamicColor = false) {
        val isConnecting = remember{mutableStateOf(false)}
        CallMenu(true,isConnecting, {}, "2137")
    }
}