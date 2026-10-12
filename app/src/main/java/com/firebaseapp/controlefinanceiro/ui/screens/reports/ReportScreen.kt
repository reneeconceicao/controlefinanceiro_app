package com.firebaseapp.controlefinanceiro.ui.screens.reports

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.firebaseapp.compose_lucide_icons.IconItemPicker


@Composable
fun ReportScreen(modifier: Modifier = Modifier) {

    Scaffold(Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {

            IconItemPicker(onIconSelected = {
                Log.d("TAG", "ReportScreen: $it")
            })

        }
    }

}

fun getAssetFiles(
    context: Context,
    folder: String = ""
): List<String> {
    val result = mutableListOf<String>()

    context.assets.list(folder)?.forEach { name ->
        val path = if (folder.isEmpty()) name else "$folder/$name"

        if (context.assets.list(path)?.isNotEmpty() == true) {
            // É folder → entra nele automaticamente
            result += getAssetFiles(context, path)
        } else {
            // É arquivo
            result += path
        }
    }

    return result
}