package com.arb.app.photoeffect.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.arb.app.photoeffect.network.ApiInterface
import com.arb.app.photoeffect.network.ApiInterface8001
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val apiInterface8001: ApiInterface8001,

    ) : BaseRepository() {
    suspend fun uploadImage(context: Context, uri: Uri, effect: String): Bitmap? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return null

        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
        val body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)

        val response = when (effect) {
            "Enhance Photo" -> apiInterface.enhanceQuality(body)
            "Remove Scratch" -> apiInterface.enhanceQuality(body)
            "Colorize" -> apiInterface8001.colorizeImage(body)
            else -> apiInterface.cartoonize(body)
        }
        val imageBytes = response.bytes()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    suspend fun gradientBgApi(context: Context, uri: Uri, color1: String, color2: String): Bitmap? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return null

        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
        val body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)
        val colorPart1 = color1.toRequestBody("text/plain".toMediaType())
        val colorPart2 = color2.toRequestBody("text/plain".toMediaType())

        val response = apiInterface.gradientBg(body, colorPart1, colorPart2)
        val imageBytes = response.bytes()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    suspend fun solidBgApi(context: Context, uri: Uri, color1: String): Bitmap? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return null

        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
        val body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)
        val colorPart1 = color1.toRequestBody("text/plain".toMediaType())

        val response = apiInterface.solidBg(body, colorPart1)

        val imageBytes = response.bytes()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}
