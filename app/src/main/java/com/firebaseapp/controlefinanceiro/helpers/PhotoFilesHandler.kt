package com.firebaseapp.controlefinanceiro.helpers

import android.content.Context
import android.graphics.Bitmap
import com.firebaseapp.controlefinanceiro.data.entities.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PhotoFilesHandler (
    private val context: Context,
) {
    fun saveBitmapToCache(bitmap: Bitmap): String {
        val fileName = "photo_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)

        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, output)
        }

        return fileName//Uri.fromFile(file)
    }

    suspend fun saveFiles(word: Word) {
        withContext(Dispatchers.IO) {
            if (word.images.isNotEmpty()) {

                word.images.forEach { image ->

                    val previousFile = File(context.filesDir, image)
                    if (previousFile.exists()) { // if the file is previously is saved...
                        return@forEach // do nothing
                    }

                    val cacheFile = File(context.cacheDir, image)
                    if (cacheFile.exists()) {
                        val newFile = File(context.filesDir, image)
                        cacheFile.copyTo(newFile, overwrite = true)
                        cacheFile.delete()
                    }
                }

            }
        }
    }

    suspend fun saveFilesOnUpdate(word: Word, originalImages: List<String>) {

        withContext(Dispatchers.IO) {
            var needDelete = mutableListOf<String>()
            originalImages.forEach {
                if (it !in word.images) {
                    needDelete.add(it)
                }
            }
            needDelete.forEach { image ->
                val fileToDelete = File(context.filesDir, image)
                if (fileToDelete.exists()) {
                    fileToDelete.delete()
                }
            }
            if (word.images.isNotEmpty()) {
                word.images.forEach { image ->

                    val previousFile = File(context.filesDir, image)
                    if (previousFile.exists()) { // if the file is previously is saved...
                        return@forEach // do nothing
                    }

                    val cacheFile = File(context.cacheDir, image)
                    if (cacheFile.exists()) {
                        val newFile = File(context.filesDir, image)
                        cacheFile.copyTo(newFile, overwrite = true)
                        cacheFile.delete()
                    }
                }
            }
        }
    }
}
