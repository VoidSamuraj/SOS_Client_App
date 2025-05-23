package com.pollub.awpfoc.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pollub.awpfoc.ui.theme.AwpfocTheme
import com.pollub.awpfoc.utils.drawShadow


/**
 * Composable function that displays a SOS button that changes color and shadow based on its active state.
 *
 * The button animates its background color and shadow color when the SOS is active.
 *
 * @param isButtonActive Boolean indicating if the button are clickable.
 * @param isSosActive MutableState<Boolean> that indicates whether the SOS feature is active.
 * @param isSystemConnecting MutableState<Boolean> that indicates whether the SOS feature is still connecting/reconnecting.
 * @param onButtonClick Lambda function to be executed when the button is clicked.
 */
@Composable
fun SOSButton(
    isButtonActive: MutableState<Boolean>,
    isSosActive: MutableState<Boolean>,
    isSystemConnecting: MutableState<Boolean>,
    onButtonClick: () -> Unit
) {

    val infiniteTransition = rememberInfiniteTransition()
    val initialColor = if (isSystemConnecting.value) {
        Color.Red.copy(alpha = 0.5f).compositeOver(Color.Gray)
    } else {
        Color.Red
    }
    val targetColor = if (isSystemConnecting.value) {
        Color.Black.copy(alpha = 0.5f).compositeOver(Color.Gray)
    } else {
        Color.Black
    }
    val shadowAlpha = if (isSystemConnecting.value) {
        0.4f
    } else {
        0.8f
    }
    val buttonColor = infiniteTransition.animateColor(
        initialValue = initialColor,
        targetValue = targetColor,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = "buttonColor"
    )
    val shadowColor = infiniteTransition.animateColor(
        initialValue = Color.Red.copy(alpha = shadowAlpha),
        targetValue = Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = "shadowColor"
    )

    Button(
        onClick = {
            if (!(isSosActive.value || isSystemConnecting.value))
                onButtonClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSosActive.value) buttonColor.value else Color(
                0xFF4A5061
            )
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(200.dp)
            .drawWithContent {
                if (isSosActive.value)
                    drawShadow(20.dp, 30.dp, shadowColor.value)
                drawContent()
            },
        enabled=isButtonActive.value
    ) {
        Text(
            text = if (isSosActive.value) "SOS AKTYWNY" else "SOS",
            color = Color.White,
            fontSize = 36.sp,
            textAlign = TextAlign.Center
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SOSButtonPreview() {
    AwpfocTheme(dynamicColor = false) {
        val isButtonActive = remember { mutableStateOf(true) }
        val isSosActive = remember { mutableStateOf(true) }
        val isReconnecting = remember { mutableStateOf(true) }
        SOSButton(isButtonActive,isSosActive, isReconnecting, {})
    }
}