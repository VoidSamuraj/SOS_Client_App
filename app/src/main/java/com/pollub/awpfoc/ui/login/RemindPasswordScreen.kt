package com.pollub.awpfoc.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pollub.awpfoc.ui.theme.AwpfocTheme

/**
 * Composable function for the password reminder screen where users can enter their email to reset their password.
 *
 * The screen includes a field for email input and a button for sending the password reminder.
 *
 * @param modifier Optional [Modifier] to be applied to the root element.
 * @param navBack Lambda function to be executed when the nav back button is pressed.
 * @param onSendPress Lambda function to be executed when the send button is pressed.
 */
@Composable
fun RemindPasswordScreen(
    modifier: Modifier = Modifier,
    navBack:()->Unit={},
    onSendPress: (email: String) -> Unit = {}
) {
    val emailState = remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Przypomnij hasło",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = "Wprowadź swój adres e-mail, aby otrzymać instrukcje dotyczące resetowania hasła.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )

        Button(
            onClick = { onSendPress(emailState.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Wyślij przypomnienie", color = MaterialTheme.colorScheme.onSecondary)
        }
        OutlinedButton(
            onClick = {
                navBack()
            },
            modifier = Modifier.padding(bottom = 100.dp)
        ) {
            Text(text = "Wróć do logowania", color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRemindPasswordScreen() {
    AwpfocTheme(dynamicColor = false) {
        RemindPasswordScreen()
    }
}