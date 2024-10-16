package com.pollub.awpfoc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pollub.awpfoc.ui.theme.AwpfocTheme

/**
 * Composable function that displays the connection status with a label and an active/inactive indicator.
 *
 * @param label The label to display for the connection status.
 * @param isActive Boolean indicating whether the connection is active or not.
 */
@Composable
fun ConnectionStatus(label: String, isActive: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.wrapContentHeight().weight(1f)) {
            Text(
                text = label,
                fontSize = 16.sp
            )
            Text(
                text = if(isActive) "Aktywny" else "Nie aktywny",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Gray)

        ){
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (isActive) Color.Green else Color.Red)
                    .align(Alignment.Center)
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ConnectionStatusPreview() {
    AwpfocTheme {
        ConnectionStatus("Stan połączenia",true)
    }
}