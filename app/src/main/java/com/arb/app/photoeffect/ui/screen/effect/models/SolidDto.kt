package com.arb.app.photoeffect.ui.screen.effect.models

import okhttp3.MultipartBody

data class SolidDto(
    val color: String,
    val image: MultipartBody.Part,
)