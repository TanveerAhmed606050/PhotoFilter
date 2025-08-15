package com.arb.app.photoeffect.ui.screen.effect.models

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class ScratchDto(
    val file: MultipartBody.Part,
    val gpu: String,
    @SerializedName("with_scratch")
    val withScratch: Boolean,
    val hr: Boolean
)
