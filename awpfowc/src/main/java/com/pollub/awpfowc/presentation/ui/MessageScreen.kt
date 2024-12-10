package com.pollub.awpfowc.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.pollub.awpfowc.presentation.viewmodel.ViewModel

@Composable
fun MessageScreen(isScreenRound: Boolean, viewModel: ViewModel) {

    val text = buildAnnotatedString {
        if (viewModel.hasProtectExpired) {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Ochrona wygasła\n")
            }
            withStyle(style = SpanStyle(color = Color.Red)) {
                append("Proszę odnów ochronę\n")
            }
            append(" w aplikacji na smartfon.")
        } else {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Sesja wygasła\n")
            }
            withStyle(style = SpanStyle(color = Color.Red)) {
                append("Proszę się zalogować\n")
            }
            append(" w aplikacji na smartfon.")
        }
    }

    Box(
        modifier = Modifier
            .background(Color.Black, if (isScreenRound) CircleShape else RectangleShape)
            .padding(16.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Text(text, textAlign = TextAlign.Center)
    }
}