package com.pollub.awpfowc.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices

@Composable
fun ConfirmationDialog(
    isVisible: Boolean,
    isScreenRound: Boolean,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {

        Column(
            modifier = Modifier
                .background(Color.Black, if (isScreenRound) CircleShape else RectangleShape)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(if (isScreenRound) 24.dp else 32.dp))
            Row {
                Spacer(modifier = Modifier.width(10.dp))
                Button(modifier = Modifier.weight(1f).border(6.dp, Color.Gray, shape = CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    ),
                    onClick = { onDismiss() }) {
                    Text("Nie")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(modifier = Modifier.weight(1f).border(6.dp, Color.Red, shape = CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    ),
                    onClick = {
                        onConfirm()
                    }) {
                    Text("Tak")
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@Preview(device = WearDevices.RECT, showSystemUi = true)
@Composable
fun ConfirmationDialogPreview() {
    // WearApp("Preview Android")
    val isScreenRound = LocalContext.current.resources.configuration.isScreenRound
    ConfirmationDialog(true,isScreenRound,"Czy na pewno odwołać patrol?",{},{})
}