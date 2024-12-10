package com.pollub.awpfowc.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.pollub.awpfowc.R


@Composable
fun ConnectionStatus(modifier: Modifier,isConnected: Boolean, onCheckConnectionClick: () -> Unit) {
    val connectionText = if (isConnected) "Połączono" else "Brak połączenia"

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = if (isConnected) painterResource(R.drawable.sensors_24dp) else painterResource(R.drawable.signal_disconnected_24),
            contentDescription = "Stan Połączenia",
            tint = Color.White
        )
        Text(text = connectionText, color = Color.White, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            modifier = Modifier.width(100.dp).height(40.dp),
            onClick = onCheckConnectionClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Gray,
                contentColor = Color.White
            )
        ) {
            Text(text = "Sprawdź stan połączenia", fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}