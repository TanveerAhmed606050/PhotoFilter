package com.arb.app.photoeffect.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.arb.app.photoeffect.network.ApiInterface
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val apiInterface: ApiInterface,
) : BaseRepository() {
    suspend fun filterApi(context: Context, uri: Uri, effect: String): Bitmap? {
        val file = compressImage(context, uri) // compress first

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val response = when (effect) {
            "Enhance Photo" -> apiInterface.enhanceQuality(body)
            "Remove Scratch" -> apiInterface.enhanceQuality(body)
            "Colorize" -> apiInterface.colorizeImage(body)
            else -> apiInterface.cartoonize(body)
        }
        val imageBytes = response.bytes()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    suspend fun gradientBgApi(context: Context, uri: Uri, color1: String, color2: String): Bitmap? {
        val file = compressImage(context, uri) // compress first

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)
        val colorPart1 = color1.toRequestBody("text/plain".toMediaType())
        val colorPart2 = color2.toRequestBody("text/plain".toMediaType())

        val response = apiInterface.gradientBg(body, colorPart1, colorPart2)
        val imageBytes = response.bytes()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    suspend fun solidBgApi(context: Context, uri: Uri, color1: String): Bitmap? {
//        val inputStream = context.contentResolver.openInputStream(uri)
//        val bytes = inputStream?.readBytes() ?: return null
        val file = compressImage(context, uri) // compress first

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val colorPart1 = color1.toRequestBody("text/plain".toMediaType())

        val response = apiInterface.solidBg(body, colorPart1)

        val imageBytes = response.bytes()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}

fun compressImage(
    context: Context,
    uri: Uri,
    maxWidth: Int = 1080,
    maxHeight: Int = 1080,
    quality: Int = 80
): File {
    val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))

    val ratio = minOf(
        maxWidth.toFloat() / bitmap.width,
        maxHeight.toFloat() / bitmap.height
    )
    val width = (bitmap.width * ratio).toInt()
    val height = (bitmap.height * ratio).toInt()
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)

    val file = File(context.cacheDir, "compressed_image.jpg")
    FileOutputStream(file).use { out ->
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
    }
    return file
}
