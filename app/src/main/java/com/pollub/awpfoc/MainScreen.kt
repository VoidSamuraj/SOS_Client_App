package com.pollub.awpfoc

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MainScreen(modifier: Modifier = Modifier, onLogout:()->Unit={}) {

    val isSystemConnected = remember{ mutableStateOf(false)}
    val isSmartWatchConnected = remember{ mutableStateOf(false)}
    val isSosActive = remember{ mutableStateOf(false)}

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFF4))
    ) {
        TopBar(onLogout = onLogout)

        Spacer(modifier = Modifier.height(16.dp))

        ConnectionStatus("Status połączenia z bazą", isActive = isSystemConnected.value)
        ConnectionStatus("Status połączenia ze smartwatch", isActive = isSmartWatchConnected.value)

        Spacer(modifier = Modifier.height(40.dp))
        Spacer(modifier = Modifier.weight(1f))

        SOSButton(
            isSosActive = isSosActive,
            onButtonClick = {
                isSosActive.value=true
            })

        Spacer(modifier = Modifier.height(40.dp))

        CallMenu(
            isCancelButtonVisible = isSosActive.value,
            onButtonClick = {
                isSosActive.value=false
            })

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun TopBar(onLogout:()->Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF232b54))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_account_circle_24),
            contentDescription = "Call",
            modifier = Modifier.size(32.dp)
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = "ID: Klient665 (Karol)",
            color = Color.White,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A5061)),
            shape = CircleShape,
        ) {
            Text(text = "Wyloguj", color = Color.White)
        }
    }
}

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

@Composable
fun SOSButton(isSosActive: MutableState<Boolean>, onButtonClick: ()->Unit) {

    val infiniteTransition = rememberInfiniteTransition()

    val buttonColor = infiniteTransition.animateColor(
        initialValue = Color.Red,
        targetValue = Color.Black,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = "buttonColor"
    )
    val shadowColor = infiniteTransition.animateColor(
        initialValue = Color.Red.copy(alpha = .8f),
        targetValue = Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = "shadowColor"
    )

    Button(
        onClick = onButtonClick,
        colors = ButtonDefaults.buttonColors(containerColor =  if(isSosActive.value) buttonColor.value else Color(0xFF4A5061)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(200.dp)
            .drawWithContent {
                if(isSosActive.value)
                    drawNeonStroke(20.dp,30.dp,shadowColor.value)
                drawContent()
            }
    ) {
        Text(text = if(isSosActive.value) "SOS AKTYWNY" else "SOS", color = Color.White , fontSize = 36.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun CallMenu(isCancelButtonVisible: Boolean, onButtonClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if(isCancelButtonVisible)
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A5061)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(80.dp)
            ) {
                Text(text = "ANULUJ SOS", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
            }
        else
            Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { /*TODO: Call action*/ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8C8C95)),
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_phone_24),
                contentDescription = "Call",
                modifier = Modifier.rotate(225f).fillMaxSize(),
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewClientAppUI() {
    MainScreen()
}
