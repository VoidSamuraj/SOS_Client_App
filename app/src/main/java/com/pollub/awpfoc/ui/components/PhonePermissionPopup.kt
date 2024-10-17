package com.pollub.awpfoc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pollub.awpfoc.supportPhoneNumber
import com.pollub.awpfoc.ui.theme.AwpfocTheme
import com.pollub.awpfoc.utils.formatPhoneNumber

/**
 * Composable function that displays a popup dialog requesting phone call permissions.
 *
 * This dialog informs the user that permission to make phone calls is required.
 * It also provides an option to copy a support phone number to the clipboard.
 *
 * @param showDialog MutableState<Boolean> that controls the visibility of the dialog.
 */
@Composable
fun PhonePermissionPopup(showDialog: MutableState<Boolean>) {
    val clipboardManager =  LocalClipboardManager.current
    if (showDialog.value) {

        Dialog(onDismissRequest = { showDialog.value = false }) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Aby zadzwonić, musisz udzielić aplikacji zezwolenia na wykonywanie połączeń.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Lub zadzwonić pod poniższy numer.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary)

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(modifier = Modifier.height(80.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        onClick = {
                            clipboardManager.setText(AnnotatedString(supportPhoneNumber))
                        }) {
                        Box(modifier = Modifier.fillMaxHeight()){
                            Text(
                                text= formatPhoneNumber(supportPhoneNumber),
                                fontSize = 24.sp,
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Text("Kopiuj numer",
                                modifier = Modifier.align(Alignment.BottomCenter),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhonePermissionPopupPreview() {
    AwpfocTheme(dynamicColor = false) {
        val showDialog = remember{ mutableStateOf(true) }
        PhonePermissionPopup(showDialog)
    }
}