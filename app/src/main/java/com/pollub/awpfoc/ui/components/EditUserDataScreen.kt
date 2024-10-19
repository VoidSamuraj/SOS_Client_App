package com.pollub.awpfoc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pollub.awpfoc.R
import com.pollub.awpfoc.ui.theme.AwpfocTheme

/**
 * Composable function for editing user data including fields for login, password, first name, last name,
 * email, phone number, and PESEL.
 *
 * The screen includes fields for user data input and a button to save the changes.
 *
 * @param modifier Optional [Modifier] to be applied to the root element.
 * @param onSavePress Lambda function to be executed when the save button is pressed.
 */
@Composable
fun EditUserDataScreen(
    modifier: Modifier = Modifier,
    onSavePress: (login: String, password: String, firstName: String, lastName: String, email: String, phone: String, pesel: String) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    val loginState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val newPasswordState = remember { mutableStateOf("") }
    val firstNameState = remember { mutableStateOf("") }
    val lastNameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val phoneState = remember { mutableStateOf("") }
    val peselState = remember { mutableStateOf("") }

    val passwordVisible = remember { mutableStateOf(false) }
    val newPasswordVisible = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Edytuj dane użytkownika",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        OutlinedTextField(
            value = loginState.value,
            onValueChange = { loginState.value = it },
            label = { Text("Login*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        )

        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Hasło*") },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible.value) R.drawable.outline_visibility_24 else R.drawable.outline_visibility_off_24
                Icon(
                    painter = painterResource(image),
                    contentDescription = if (passwordVisible.value) "Ukryj hasło" else "Pokaż hasło",
                    modifier = Modifier.clickable{ passwordVisible.value = !passwordVisible.value }
                )
            }
        )
        OutlinedTextField(
            value = newPasswordState.value,
            onValueChange = { newPasswordState.value = it },
            label = { Text("Nowe Hasło") },
            visualTransformation = if (newPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (newPasswordVisible.value) R.drawable.outline_visibility_24 else R.drawable.outline_visibility_off_24
                Icon(
                    painter = painterResource(image),
                    contentDescription = if (newPasswordVisible.value) "Ukryj hasło" else "Pokaż hasło",
                    modifier = Modifier.clickable{ newPasswordVisible.value = !newPasswordVisible.value }
                )
            }
        )

        OutlinedTextField(
            value = firstNameState.value,
            onValueChange = { firstNameState.value = it },
            label = { Text("Imię*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        )

        OutlinedTextField(
            value = lastNameState.value,
            onValueChange = { lastNameState.value = it },
            label = { Text("Nazwisko*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        )

        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = phoneState.value,
            onValueChange = { phoneState.value = it },
            label = { Text("Numer telefonu*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
        )

        OutlinedTextField(
            value = peselState.value,
            onValueChange = { peselState.value = it },
            label = { Text("PESEL*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = {
                onSavePress(
                    loginState.value,
                    passwordState.value,
                    firstNameState.value,
                    lastNameState.value,
                    emailState.value,
                    phoneState.value,
                    peselState.value
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Zapisz zmiany", color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditUserDataScreen() {
    AwpfocTheme(dynamicColor = false) {
        EditUserDataScreen()
    }
}
