package com.arb.app.photoeffect.ui.screen.effect.models

import okhttp3.MultipartBody

data class IngredientDto(
    val color1: String,
    val color2: String,
    val file: MultipartBody.Part,
)
