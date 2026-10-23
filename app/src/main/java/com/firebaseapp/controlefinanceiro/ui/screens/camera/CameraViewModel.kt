package com.firebaseapp.controlefinanceiro.ui.screens.camera

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import de.yanneckreiss.cameraxtutorial.data.usecases.SavePhotoToGalleryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

//import org.koin.android.annotation.KoinViewModel

//@KoinViewModel
class CameraViewModel(
    //private val savePhotoToGalleryUseCase: SavePhotoToGalleryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    fun storePhotoInGallery(bitmap: Bitmap) {
        viewModelScope.launch {
            //savePhotoToGalleryUseCase.call(bitmap)
            updateCapturedPhotoState(bitmap)
        }
    }

    fun saveBitmapToCache(context: Context, bitmap: Bitmap): String {
        val fileName = "photo_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)

        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
        }

        return fileName//Uri.fromFile(file)
    }

    private fun updateCapturedPhotoState(updatedPhoto: Bitmap?) {
        _state.value.capturedImage?.recycle()
        _state.value = _state.value.copy(capturedImage = updatedPhoto)
    }

    override fun onCleared() {
        _state.value.capturedImage?.recycle()
        super.onCleared()
    }
}