package com.arb.app.photoeffect.ui.screen.plus

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arb.app.photoeffect.repository.PhotoRepository
import com.arb.app.photoeffect.ui.screen.effect.models.EnhanceColorizeDto
import com.arb.app.photoeffect.ui.screen.effect.models.IngredientDto
import com.arb.app.photoeffect.ui.screen.effect.models.ScratchDto
import com.arb.app.photoeffect.ui.screen.effect.models.SolidDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class PhotoVM @Inject constructor(
    private val photoRepository: PhotoRepository,
) : ViewModel() {
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf("")
    var bitmap by mutableStateOf<Bitmap?>(null)

    fun filterApi(enhanceColorizeDto: EnhanceColorizeDto, effect: String) =
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                bitmap = photoRepository.filterApi(enhanceColorizeDto, effect)
            } catch (e: HttpException) {
                error = if (e.code() == 413) {
                    "Image too large. Please use a smaller file."
                } else {
                    "Server error: ${e.code()}"
                }
            } catch (e: Exception) {
                error = "Something went wrong: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }

    fun gradientBgApi(ingredientDto: IngredientDto) =
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                bitmap = photoRepository.gradientBgApi(ingredientDto)
            } finally {
                isLoading = false
            }
        }

    fun removeScratchApi(scratchDto: ScratchDto) =
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                bitmap = photoRepository.removeScratchApi(scratchDto)
            } finally {
                isLoading = false
            }
        }

    fun solidBgApi(solidDto: SolidDto) =
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                Log.d("lsdjg", "solidBgApi: $solidDto")
                bitmap = photoRepository.solidBgApi(solidDto)
            } finally {
                isLoading = false
            }
        }

    fun resetBitmap() {
        bitmap = null
    }
}