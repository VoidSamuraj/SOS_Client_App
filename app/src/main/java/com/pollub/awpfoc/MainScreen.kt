package com.pollub.awpfoc

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val isActive=true
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFF4))
    ) {
        TopBar()

        Spacer(modifier = Modifier.height(16.dp))

        ConnectionStatus("Status połączenia z bazą", isActive = isActive)
        ConnectionStatus("Status połączenia ze smartwatch", isActive = isActive)

        Spacer(modifier = Modifier.height(40.dp))
        Spacer(modifier = Modifier.weight(1f))

        SOSButton(isActive = isActive)

        Spacer(modifier = Modifier.height(40.dp))

        CallMenu(isVisible = isActive)

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun TopBar() {
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
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = "ID: Klient665 (Karol)",
                color = Color.White,
                fontSize = 18.sp
            )
            Text(
                text = "Zalogowany",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { /*TODO: Implement logout*/ },
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
        Column(modifier = Modifier.wrapContentHeight().weight(1f),) {
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
fun SOSButton(isActive: Boolean) {
    Button(
        onClick = { /*TODO: SOS action*/ },
        colors = ButtonDefaults.buttonColors(containerColor =  if(isActive) Color(0xFFFF6754) else Color(0xFF4A5061)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(200.dp)
    ) {
        Text(text = if(isActive) "SOS AKTYWNY" else "SOS", color = if(isActive) Color(0xFF4A5061) else Color.White , fontSize = 36.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun CallMenu(isVisible: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {

        if(isVisible)
        Button(
            onClick = { /*TODO: SOS action*/ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A5061)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .height(80.dp)
        ) {
            Text(text = "ANULUJ SOS", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
        }else {
            Spacer(modifier = Modifier.weight(1f))
        }

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
