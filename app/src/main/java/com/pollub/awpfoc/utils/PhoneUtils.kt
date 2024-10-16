package com.pollub.awpfoc.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat


/**
 * Initiates a phone call using the specified phone number.
 *
 * This function checks for the necessary permission to make phone calls.
 * If the permission is granted, it launches the dialer with the provided phone number.
 * If the permission is not granted, it requests the permission using the provided launcher.
 *
 * @param context The context from which the function is called.
 * @param requestPermissionLauncher The launcher used to request the CALL_PHONE permission.
 * @param phoneNumber The phone number to dial.
 */
fun makePhoneCall(
    context: Context,
    requestPermissionLauncher:ActivityResultLauncher<String>,
    phoneNumber: String
) {
    val permissionCheck = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.CALL_PHONE
    ) == PackageManager.PERMISSION_GRANTED

    if (permissionCheck) {
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(dialIntent)
    } else {
        requestPermissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
    }
}

/**
 * Formats a phone number by grouping it into chunks.
 *
 * This function splits the provided phone number into chunks of three digits
 * and joins them with a hyphen (-) for easier readability.
 *
 * @param phoneNumber The phone number to format, should contain only digits.
 * @return A formatted phone number string with hyphens separating every three digits.
 */
fun formatPhoneNumber(phoneNumber: String): String {
    return phoneNumber.chunked(3).joinToString("-")
}