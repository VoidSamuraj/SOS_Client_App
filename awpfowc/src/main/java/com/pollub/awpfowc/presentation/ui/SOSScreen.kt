package com.pollub.awpfowc.presentation.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.pollub.awpfowc.presentation.utils.innerShadow


@Composable
fun SOSScreen(isScreenRound: Boolean, onDismiss: () -> Unit) {
    val size = remember { Animatable(30f) }
    val isDialogVisible = remember { mutableStateOf(false) }
    val screenShape = if (isScreenRound) CircleShape else RectangleShape

    LaunchedEffect(Unit) {
        while (true) {
            size.animateTo(
                targetValue = 30f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                )
            )
            size.animateTo(
                targetValue = 10f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red, screenShape)
            .border(12.dp, Color.Red, if (isScreenRound) CircleShape else RoundedCornerShape(16.dp))
    ) {

        Column(
            modifier = Modifier
                .padding(12.dp)
                .background(
                    color = Color.Black,
                    shape = if (isScreenRound) CircleShape else RoundedCornerShape(16.dp)
                )
                .innerShadow(
                    blur = size.value.dp,
                    color = Color.Red,
                    cornersRadius = if (isScreenRound) 200.dp else 16.dp,
                    offsetX = 0.dp,
                    offsetY = 0.dp
                )
                .padding(if (isScreenRound) 0.dp else 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(if (isScreenRound) 1f else 0.5f))
            Box(
                modifier = Modifier
                    .weight(1.8f)
                    .padding(horizontal = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Patrol jest w drodze",
                    color = Color(51, 204, 51),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth(if (isScreenRound) 1f else 0.9f)
                    .weight(2f),
                onClick = { isDialogVisible.value = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(143, 73, 83),
                    contentColor = Color.White
                ),
                shape = if (isScreenRound) CutCornerShape(50.dp) else RoundedCornerShape(80.dp)
            ) {
                Text(text = "Odwołaj patrol", fontSize = 16.sp, modifier = Modifier)
            }
            if (!isScreenRound)
                Spacer(modifier = Modifier.weight(0.2f))

            ConfirmationDialog(
                isVisible = isDialogVisible.value,
                isScreenRound = isScreenRound,
                text = "Czy na pewno odwołać patrol?",
                onConfirm = {
                    isDialogVisible.value = false
                    onDismiss()
                },
                onDismiss = {
                    isDialogVisible.value = false
                })
        }
    }
}

@Preview(device = WearDevices.SQUARE, showSystemUi = true)
@Composable
fun SOSPreview() {
    val isScreenRound = LocalContext.current.resources.configuration.isScreenRound
    SOSScreen(isScreenRound, {})
}