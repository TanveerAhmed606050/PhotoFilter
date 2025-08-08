package com.arb.app.photoeffect.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.arb.app.photoeffect.network.ApiInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val apiInterface: ApiInterface
) : BaseRepository() {
    suspend fun uploadImage(context: Context, uri: Uri, effect: String): Bitmap? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return null

        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
        val body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)

        val response = when (effect) {
            "Enhance Photo" -> apiInterface.enhanceQuality(body)
            "Remove Scratch" -> apiInterface.enhanceQuality(body)
            "Colorize" -> apiInterface.colorizeImage(body)
            else -> apiInterface.cartoonize(body)
        }
        val imageBytes = response.bytes()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}