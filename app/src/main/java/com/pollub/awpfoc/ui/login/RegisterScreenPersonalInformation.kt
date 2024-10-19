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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pollub.awpfoc.ui.theme.AwpfocTheme
import com.pollub.awpfoc.utils.isPeselValid
import com.pollub.awpfoc.utils.isPhoneValid
import com.pollub.awpfoc.utils.isUsernameValid
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import com.pollub.awpfoc.utils.isEmailValid

/**
 * Personal information registration screen that collects user details for account creation.
 *
 * @param modifier Modifier for this composable, allowing customization of style and layout.
 * @param navBack Function called to navigate back to the previous screen.
 * @param onSignUp Function called when the user completes the registration, with user details as parameters.
 */
@Composable
fun RegistrationScreenPersonalInformation(
    modifier: Modifier = Modifier,
    navBack: () -> Unit = {},
    onSignUp: (login:String, password:String, name:String, surname:String, email:String, phone:String, pesel:String) -> Unit = { _, _, _, _, _, _, _ -> }
) {

    var loginState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }

    var nameState by remember { mutableStateOf("") }
    var surnameState by remember { mutableStateOf("") }
    var emailState by remember { mutableStateOf("") }
    var phoneState by remember { mutableStateOf("") }
    var peselState by remember { mutableStateOf("") }

    var isNameValid by remember { mutableStateOf(true) }
    var isSurnameValid by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPhoneValid by remember { mutableStateOf(true) }
    var isPeselValid by remember { mutableStateOf(true) }

    var nameError by remember { mutableStateOf("") }
    var surnameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var peselError by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = nameState,
            onValueChange = {
                nameState = it
                isNameValid = isUsernameValid(it)
                nameError = if(isNameValid) "" else "Imie powinno zawierać od 3 do 40 znaków."
            },
            label = { Text("Imię*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = if(isNameValid) 16.dp else 4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            isError = !isNameValid
        )
        if (!isNameValid) {
            Text(
                text = nameError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = surnameState,
            onValueChange = {
                surnameState = it
                isSurnameValid = isUsernameValid(it)
                surnameError = if(isSurnameValid) "" else "Nazwisko powinno zawierać od 3 do 40 znaków."
            },
            label = { Text("Nazwisko*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = if(isSurnameValid) 16.dp else 4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            isError = !isSurnameValid
        )
        if (!isSurnameValid) {
            Text(
                text = surnameError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = emailState,
            onValueChange = {
                emailState = it
                isEmailValid = isEmailValid(it)
                emailError = if(isEmailValid) "" else "Proszę podać poprawny email"
            },
            label = { Text("Email*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = if(isEmailValid) 16.dp else 4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            isError = !isEmailValid
        )
        if (!isEmailValid) {
            Text(
                text = emailError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = phoneState,
            onValueChange = {
                phoneState = it
                isPhoneValid = isPhoneValid(it)
                phoneError = if(isPhoneValid) "" else "Telefon powinien zawierać od 10 do 13 cyfr z opcjonalnym znakiem +"
            },
            label = { Text("Nr telefonu*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = if(isPhoneValid) 16.dp else 4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            isError = !isPhoneValid
        )
        if (!isPhoneValid) {
            Text(
                text = phoneError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = peselState,
            onValueChange = {
                peselState = it
                isPeselValid = isPeselValid(it)
                peselError = if(isPeselValid) "" else "Proszę podać poprawny pesel"
            },
            label = { Text("Nr PESEL*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = if(isPeselValid) 32.dp else 4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            isError = !isPeselValid
        )
        if (!isPeselValid) {
            Text(
                text = peselError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
        Button(
            onClick = {
                var proceed = true

                isNameValid = isUsernameValid(nameState)
                isSurnameValid = isUsernameValid(surnameState)
                isEmailValid = isEmailValid(emailState)
                isPhoneValid = isPhoneValid(phoneState)
                isPeselValid = isPeselValid(peselState)

                if(!isNameValid){
                    proceed = false
                    nameError = "Imie powinno zawierać od 3 do 40 znaków."
                }
                if(!isSurnameValid){
                    proceed = false
                    surnameError = "Nazwisko powinno zawierać od 3 do 40 znaków."
                }
                if(!isEmailValid){
                    proceed = false
                    emailError = "Proszę podać poprawny email"
                }
                if(!isPhoneValid){
                    proceed = false
                    phoneError = "Telefon powinien zawierać od 10 do 13 cyfr z opcjonalnym znakiem +"
                }
                if(!isPeselValid){
                    proceed = false
                    peselError = "Proszę podać poprawny pesel"
                }

                if(proceed)
                    onSignUp(
                        loginState,
                        passwordState,
                        nameState,
                        surnameState,
                        emailState,
                        phoneState,
                        peselState
                    )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Zarejestruj się", color = MaterialTheme.colorScheme.onSecondary)
        }

        TextButton(
            onClick = {
                navBack()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Cofnij", color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegistrationScreenPersonalInformation() {
    AwpfocTheme(dynamicColor = false) {
        RegistrationScreenPersonalInformation()
    }
}
