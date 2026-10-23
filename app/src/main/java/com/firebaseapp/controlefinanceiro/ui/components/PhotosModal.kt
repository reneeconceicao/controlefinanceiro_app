package com.firebaseapp.controlefinanceiro.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.firebaseapp.compose_lucide_icons.helpers.IconsHelper
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.entities.Category
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingExtraLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.defaults.paddingTiny
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosModal(
    context: Context,
    images: List<String>,
    onAddImage: () -> Unit,
    onItemDeleted: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {

        Box(Modifier.fillMaxWidth()) {

            Text(
                stringResource(R.string.select_a_category),
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    onDismissRequest()
                },
                Modifier
                    .padding(horizontal = paddingSmall())
                    .align(Alignment.CenterEnd),
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = Color.Transparent
                )
            ) {
                Icon(imageVector = Icons.Default.Close, "")
            }
        }




        LazyRow(
            modifier = Modifier
                .padding(paddingDefault())
                .padding(bottom = paddingExtraLarge()),
            horizontalArrangement = Arrangement.spacedBy(paddingSmall()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(images, key = { image -> image }) { image ->
                val currentFolder = if (File(context.filesDir, image).exists())
                    context.filesDir else context.cacheDir

                val options = BitmapFactory.Options().apply {
                    inSampleSize = 4
                }

                val bitmap = remember(image) {
                    BitmapFactory.decodeFile(
                        File(currentFolder, image).absolutePath,
                        options
                    )?.asImageBitmap()
                }

                bitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        Modifier
                            .fillParentMaxWidth(0.32f)
                            .aspectRatio(1f)
                            .clickable {
                                onItemDeleted(image)
                            },
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            if (images.size < 3) {
                item {
                    Box(
                        Modifier
                            .fillParentMaxWidth(0.32f)
                            .aspectRatio(1f)
                            .clickable {
                                onAddImage()
                            }
                            .border(
                                width = 2.dp,
                                Color.LightGray,
                                shape = RoundedCornerShape(size = paddingTiny())
                            ),

                        ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            "",
                            modifier = Modifier
                                .size(paddingLarge())
                                .align(Alignment.Center)
                        )
                    }
                }
            }

        }
    }
}