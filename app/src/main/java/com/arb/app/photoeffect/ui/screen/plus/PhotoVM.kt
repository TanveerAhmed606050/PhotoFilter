package com.arb.app.photoeffect.ui.screen.plus

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arb.app.photoeffect.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoVM @Inject constructor(
    private val photoRepository: PhotoRepository,
) : ViewModel() {
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf("")
    var bitmap by mutableStateOf<Bitmap?>(null)

    fun upload(context: Context, uri: Uri, effect: String) = viewModelScope.launch {
        isLoading = true
        try {
            bitmap = photoRepository.uploadImage(context, uri, effect)
        } finally {
            isLoading = false
        }
    }

    fun gradientBgApi(context: Context, uri: Uri, color1: String, color2: String) =
        viewModelScope.launch {
            isLoading = true
            try {
                bitmap = photoRepository.gradientBgApi(context, uri, color1, color2 = color2)
            } finally {
                isLoading = false
            }
        }

    fun solidBgApi(context: Context, uri: Uri, color1: String) = viewModelScope.launch {
        isLoading = true
        try {
            bitmap = photoRepository.solidBgApi(context, uri, color1)
        } finally {
            isLoading = false
        }
    }
}