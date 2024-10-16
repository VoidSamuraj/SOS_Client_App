package com.pollub.awpfoc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Aby zadzwonić, musisz udzielić aplikacji zezwolenia na wykonywanie połączeń.",
                        textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Lub zadzwonić pod poniższy numer.",
                        textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(modifier = Modifier,
                        onClick = {
                            clipboardManager.setText(AnnotatedString(supportPhoneNumber))
                        }) {
                        Box(modifier = Modifier.height(60.dp)){
                            Text(
                                text= formatPhoneNumber(supportPhoneNumber),
                                fontSize = 24.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            Text("Kopiuj numer",
                                color = Color.LightGray,
                                modifier = Modifier.align(Alignment.BottomCenter))
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
    AwpfocTheme {
        val showDialog = remember{ mutableStateOf(true) }
        PhonePermissionPopup(showDialog)
    }
}