package com.arb.app.photoeffect.util

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.core.view.WindowCompat
import com.arb.app.photoeffect.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

object Utils {
    // Function to load cities from JSON file in assets
    fun isValidPhone(phoneNumber: String): String {
        val phoneNumberPattern = Regex("^[+]?[0-9]{10,11}$")
        return if (phoneNumberPattern.matches(phoneNumber)) "" else "Enter valid phone number"
    }

    fun isValidPassword(password: String): String {
        return if (password.isEmpty())
            "Password fields must not be empty."
        else if (password.trim().length < 8)
            "Password must be at least 8 characters long."
        else ""
    }

    fun isValidText(text: String): String {
        return if (text.trim().replace("\n", "").matches(Regex("^[^0-9]+$"))
            && text.length >= 3
        )
            ""
        else "Enter valid text"
    }

    fun uriToFile(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        val tempFile = File(
            context.cacheDir,
            "${System.currentTimeMillis()}.jpg"
        ) // Change extension based on your API requirement

        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    fun createMultipartBodyPart(file: File, name: String): MultipartBody.Part {
        val requestBody =
            file.asRequestBody("image/*".toMediaTypeOrNull()) // Change MIME type as needed
        return MultipartBody.Part.createFormData(name, file.name, requestBody)
    }

    fun isRtlLocale(locale: Locale): Boolean {
        val rtlLanguages = listOf("ur") // List of RTL languages
        return rtlLanguages.contains(locale.language)
    }

    fun actionBar(context: Activity) {
        val window = context.window
        // Set the status bar background color to white
        window.statusBarColor = context.resources.getColor(R.color.black, context.theme)

        // Change the status bar icons to black for better visibility
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

        val outputDate = inputFormat.parse(inputDate)
        return outputFormat.format(outputDate ?: "")
    }
}