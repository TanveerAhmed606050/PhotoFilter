package com.arb.app.photoeffect.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.arb.app.photoeffect.network.ApiInterface
import com.arb.app.photoeffect.ui.screen.effect.models.EnhanceColorizeDto
import com.arb.app.photoeffect.ui.screen.effect.models.IngredientDto
import com.arb.app.photoeffect.ui.screen.effect.models.ScratchDto
import com.arb.app.photoeffect.ui.screen.effect.models.SolidDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val apiInterface: ApiInterface,
) : BaseRepository() {
    suspend fun filterApi(enhanceColorizeDto: EnhanceColorizeDto, effect: String): Bitmap? {

        val response = when (effect) {
            "Enhance Photo" -> apiInterface.enhanceQuality(enhanceColorizeDto.file)
            "Colorize" -> apiInterface.colorizeImage(enhanceColorizeDto.file)
            "Portrait Cutout" -> apiInterface.portrait(enhanceColorizeDto.file)
            else -> apiInterface.cartoonize(enhanceColorizeDto.file)
        }
        val imageBytes = response.bytes()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    suspend fun gradientBgApi(ingredientDto: IngredientDto): Bitmap? {
        val response = apiInterface.gradientBg(
            ingredientDto.file,
            ingredientDto.color1,
            ingredientDto.color2
        )
        val imageBytes = response.bytes()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    suspend fun removeScratchApi(scratchDto: ScratchDto): Bitmap? {
        val response = apiInterface.removeScratch(
            scratchDto.file,
            scratchDto.gpu.toRequestBody("text/plain".toMediaTypeOrNull()),
            scratchDto.withScratch.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            scratchDto.hr.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
        )
        val imageBytes = response.bytes()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    suspend fun solidBgApi(solidDto: SolidDto): Bitmap? {
        val response = apiInterface.solidBg(
            solidDto.image,
            solidDto.color
        )

        val imageBytes = response.bytes()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}

fun compressImage(
    file: File,
    maxWidth: Int = 1080,
    maxHeight: Int = 1080,
    quality: Int = 90
): File {
    val bitmap = BitmapFactory.decodeFile(file.path)

    // Calculate proportional scaling
    val ratio = minOf(
        maxWidth.toFloat() / bitmap.width,
        maxHeight.toFloat() / bitmap.height,
        1f // don't upscale small images
    )
    val newWidth = (bitmap.width * ratio).toInt()
    val newHeight = (bitmap.height * ratio).toInt()
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

    // Detect original format
    val extension = file.extension.lowercase()
    val format = when (extension) {
        "png" -> Bitmap.CompressFormat.PNG // lossless
        "webp" -> Bitmap.CompressFormat.WEBP
        else -> Bitmap.CompressFormat.JPEG
    }

    // Output file
    val compressedFile = File(file.parent, "compressed_${file.name}")
    FileOutputStream(compressedFile).use { out ->
        scaledBitmap.compress(format, quality, out)
    }

    return compressedFile
}
