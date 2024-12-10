package com.pollub.awpfowc.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.pollub.awpfowc.R

@Composable
fun MainScreen(
    isScreenRound: Boolean,
    isConnected: Boolean,
    onSOSClick: () -> Unit,
    onCheckConnectionClick: () -> Unit
) {
    val isDialogVisible = remember { mutableStateOf(false) }
    val connectionColor = if (isConnected) Color.Red else Color.Gray
    val screenShape = if (isScreenRound) CircleShape else RectangleShape

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(connectionColor, screenShape)
            .border(
                12.dp,
                connectionColor,
                if (isScreenRound) CircleShape else RoundedCornerShape(16.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .background(
                    color = Color.Black,
                    shape = if (isScreenRound) CircleShape else RoundedCornerShape(16.dp)
                )
                .padding(if (isScreenRound) 0.dp else 10.dp)

        ) {

            if (isScreenRound)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    ConnectionStatus(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(3f)
                            .padding(top = 5.dp),
                        isConnected = isConnected,
                        onCheckConnectionClick = onCheckConnectionClick
                    )

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f),
                        onClick = { isDialogVisible.value = true },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(143, 73, 83),
                            contentColor = Color.White
                        ),
                        shape = CutCornerShape(topStart = 50.dp, topEnd = 50.dp)
                    ) {
                        Text(
                            text = "SOS",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }
            else {
                val padding = 46.dp

                Icon(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    painter = if (isConnected) painterResource(R.drawable.sensors_24dp) else painterResource(
                        R.drawable.signal_disconnected_24
                    ),
                    contentDescription = "Stan Połączenia",
                    tint = Color.White
                )

                Button(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = onCheckConnectionClick,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(51, 153, 255),
                        contentColor = Color.White
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(R.drawable.baseline_refresh_24),
                        contentDescription = "Stan Połączenia",
                        tint = Color.White
                    )
                }

                Button(
                    modifier = Modifier
                        .padding(bottom = padding)
                        .fillMaxWidth(.9f)
                        .height(70.dp)
                        .align(Alignment.Center),
                    onClick = { isDialogVisible.value = true },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(143, 73, 83),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(35.dp)
                ) {
                    Text(
                        text = "SOS",
                        fontSize = 16.sp
                    )
                }
            }

            ConfirmationDialog(
                isVisible = isDialogVisible.value,
                isScreenRound = isScreenRound,
                text = "Czy na pewno wezwać patrol?",
                onConfirm = {
                    isDialogVisible.value = false
                    onSOSClick()
                },
                onDismiss = {
                    isDialogVisible.value = false
                })
        }
    }
}