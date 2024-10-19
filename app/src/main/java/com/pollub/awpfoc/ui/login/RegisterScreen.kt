package com.pollub.awpfoc.ui.login

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pollub.awpfoc.ui.theme.AwpfocTheme
import com.pollub.awpfoc.utils.isLoginValid
import com.pollub.awpfoc.utils.isPasswordValid
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.pollub.awpfoc.R

/**
 * Registration screen that allows the user to enter their username and password.
 *
 * @param modifier Modifier for this composable, allowing customization of style and layout.
 * @param navToLogin Function called when navigating to the login screen.
 * @param navToNextScreen Function called when navigating to the next registration screen,
 * with username and password as parameters.
 */
@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    navToLogin: () -> Unit = {},
    navToNextScreen: (login: String, password: String) -> Unit = {_,_->},
) {
    var loginState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }
    var confirmPasswordState by remember { mutableStateOf("") }

    var isLoginValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var arePasswordsSame by remember { mutableStateOf(true) }

    var loginError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var passwordConfirmError by remember { mutableStateOf("") }

    val passwordVisible = remember { mutableStateOf(false) }
    val repeatPasswordVisible = remember { mutableStateOf(false) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Rejestracja",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = "Aby założyć konto wprowadź login i hasło",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        OutlinedTextField(
            value = loginState,
            onValueChange = {
                loginState = it
                isLoginValid = isLoginValid(it)
                loginError = if (isLoginValid) "" else "Login jest wymagany i powinien zawierać od 3 do 20 znaków"
            },
            label = { Text("Login*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = if(isLoginValid) 16.dp else 4.dp),
            isError = !isLoginValid
        )
        if (!isLoginValid) {
            Text(
                text = loginError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        OutlinedTextField(
            value = passwordState,
            onValueChange = {
                passwordState = it
                isPasswordValid = isPasswordValid(it)
                arePasswordsSame = it == confirmPasswordState
                passwordConfirmError =  if (arePasswordsSame) "" else "Hasła powinny być takie same."
                passwordError = if (isPasswordValid) "" else "Hasło powinno mieć minimum 8 znaków, zawierać małą i dużą literę, cyfrę i znak specjalny."
            },
            label = { Text("Wprowadź hasło*") },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(bottom = if(isPasswordValid) 16.dp else 4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            isError = !isPasswordValid,
            trailingIcon = {
                val image = if (passwordVisible.value) R.drawable.outline_visibility_24 else R.drawable.outline_visibility_off_24
                Icon(
                    painter = painterResource(image),
                    contentDescription = if (passwordVisible.value) "Ukryj hasło" else "Pokaż hasło",
                    modifier = Modifier.clickable{ passwordVisible.value = !passwordVisible.value }
                )
            }
        )
        if (!isPasswordValid) {
            Text(
                text = passwordError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom=8.dp)
            )
        }
        OutlinedTextField(
            value = confirmPasswordState,
            onValueChange = {
                confirmPasswordState = it
                arePasswordsSame = it == passwordState
                passwordConfirmError =  if (arePasswordsSame) "" else "Hasła powinny być takie same."
            },
            label = { Text("Powtórz hasło*") },
            visualTransformation = if (repeatPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(bottom = if(arePasswordsSame) 32.dp else 4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            isError = !arePasswordsSame,
            trailingIcon = {
                val image = if (repeatPasswordVisible.value) R.drawable.outline_visibility_24 else R.drawable.outline_visibility_off_24
                Icon(
                    painter = painterResource(image),
                    contentDescription = if (repeatPasswordVisible.value) "Ukryj hasło" else "Pokaż hasło",
                    modifier = Modifier.clickable{ repeatPasswordVisible.value = !repeatPasswordVisible.value }
                )
            }
        )
        if (!arePasswordsSame) {
            Text(
                text = passwordConfirmError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
        Button(
            onClick = {
                var proceed = true

                isLoginValid = isLoginValid(loginState)
                isPasswordValid = isPasswordValid(passwordState)
                arePasswordsSame = passwordState == confirmPasswordState
                if(!isLoginValid){
                    proceed = false
                    loginError = "Login jest wymagany i powinien zawierać od 3 do 20 znaków"
                }
                if(!isPasswordValid){
                    proceed = false
                    passwordError = "Hasło powinno mieć minimum 8 znaków, zawierać małą i dużą literę, cyfrę i znak specjalny."
                }
                if(!arePasswordsSame){
                    proceed = false
                    passwordConfirmError = "Hasła powinny być takie same."
                }

                if(proceed)
                    navToNextScreen(loginState, passwordState)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Załóż konto", color = MaterialTheme.colorScheme.onSecondary)
        }
        Text(
            text = "Posiadasz konto? Zaloguj się",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        OutlinedButton(
            onClick = {
                navToLogin()
            },
            modifier = Modifier.padding(bottom = 100.dp)
        ) {
            Text(text = "Zaloguj się", color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegistrationScreen() {
    AwpfocTheme(dynamicColor = false) {
        RegistrationScreen()
    }
}
