package com.firebaseapp.controlefinanceiro.ui.screens.home

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingExtraLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.defaults.paddingTiny
import com.firebaseapp.controlefinanceiro.helpers.currencyFormat
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsDialog(
    context: Context,
    wordId: Int,
    onEdit: (Word) -> Unit,
    onDelete: (DeleteDetails) -> Unit, // word, index, deleteNext, deleteAll
    onDismissRequest: () -> Unit,
    viewModel: DetailsViewModel = viewModel(factory = ViewModelProviders.Factory)
) {

    val word by viewModel.currentWordStateFlow.collectAsState()

    var images by rememberSaveable { mutableStateOf<List<String>>(listOf()) }

    var imageIndex: Int by rememberSaveable { mutableIntStateOf(0) }

    var showFullScreenPhoto by rememberSaveable { mutableStateOf(false) }

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    viewModel.updateId(wordId)

    LaunchedEffect(word) {
        word?.let {
            images = it.images
        }
    }

    if (showFullScreenPhoto) {
        PhotoViewDialog(
            initialItem = imageIndex,
            images = images,
            onDismissRequest = {
                showFullScreenPhoto = false
            }
        )
    }

    if (showDeleteDialog) {
        word?.let {
            DeleteDialog(
                word = it,
                deleteThisOccurrenceWord = { word ->
                    onDelete(DeleteDetails(word = word))
                    onDismissRequest()
                },
                deleteWordAndNext = { word ->
                    onDelete(DeleteDetails(word = word, deleteNext = true))
                    onDismissRequest()
                },
                deleteAll = { word ->
                    onDelete(DeleteDetails(word = word, deleteAll = true))
                    onDismissRequest()
                },
                onDismissRequest = {
                    showDeleteDialog = false
                }
            )
        }
    }

    BasicAlertDialog(onDismissRequest = onDismissRequest) {


        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(paddingDefault())
                )
                .padding(vertical = paddingTiny(), horizontal = paddingDefault())
        ) {
            Box(Modifier.fillMaxWidth()) {

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

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(paddingSmall())
            ) {

                Text(currencyFormat(word?.value.toString()))

                LazyRow(
                    modifier = Modifier
                        .padding(paddingDefault())
                        .padding(bottom = paddingExtraLarge()),
                    horizontalArrangement = Arrangement.spacedBy(paddingSmall()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(images, key = { _, image -> image }) { index, image ->
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

                        bitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                Modifier
                                    .fillParentMaxWidth(0.32f)
                                    .aspectRatio(1f)
                                    .clickable {
                                        imageIndex = index
                                        showFullScreenPhoto = true
                                    },
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        showDeleteDialog = true
                    }) {
                        Text(stringResource(R.string.delete))
                    }
                    Spacer(Modifier.padding(horizontal = paddingDefault()))
                    Button(onClick = {
                        word?.let {
                            onEdit(it)
                        }
                        onDismissRequest()
                    }) {
                        Text(stringResource(R.string.edit))
                    }
                }


            }
        }
    }


}

@Composable
fun PhotoViewDialog(
    initialItem: Int,
    images: List<String>,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    var currentItem: Int by rememberSaveable { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        key(initialItem) {
            val pagerState = rememberPagerState(
                initialPage = initialItem,
                pageCount = { images.size }
            )
            Surface() {
                Column(Modifier.fillMaxSize()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(paddingSmall()),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        IconButton(
                            onClick = {
                                onDismissRequest()
                            },
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }

                        IconButton(
                            onClick = {
                                Log.d("TAG", "WordOptionsModal: $currentItem")
                            },
                        ) {
                            Icon(Icons.Default.Share, null)

                        }

                    }

                    HorizontalPager(
                        modifier = Modifier
                            .padding(paddingDefault())
                            .padding(bottom = paddingExtraLarge()),
                        state = pagerState,

                        verticalAlignment = Alignment.CenterVertically
                    ) { page ->

                        currentItem = page

                        val currentFolder = if (File(context.filesDir, images[page]).exists())
                            context.filesDir else context.cacheDir

                        val bitmap = remember(images[page]) {
                            BitmapFactory.decodeFile(
                                File(currentFolder, images[page]).absolutePath,
                            )?.asImageBitmap()
                        }

                        bitmap?.let {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Image(
                                    bitmap = it,
                                    contentDescription = null,
                                    Modifier.fillMaxSize()
                                )
                            }

                        }

                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDialog(
    word: Word,
    deleteThisOccurrenceWord: (Word) -> Unit,
    deleteWordAndNext: (Word) -> Unit,
    deleteAll: (Word) -> Unit,
    onDismissRequest: () -> Unit,
) {

    BasicAlertDialog(onDismissRequest = onDismissRequest) {


        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(paddingDefault())
                )
                .padding(vertical = paddingTiny(), horizontal = paddingDefault())
        ) {

            if (!word.isRecurring) {
                Text(stringResource(R.string.are_you_sure_you_want_to_delete), Modifier
                    .padding(paddingDefault())
                    .fillMaxWidth(), textAlign = TextAlign.Start)
                Row(Modifier
                    .fillMaxWidth()
                    .padding(bottom = paddingDefault()), horizontalArrangement = Arrangement.SpaceAround) {
                    Button(onClick = {
                        onDismissRequest()
                    }) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(onClick = {
                        deleteThisOccurrenceWord(word)
                        onDismissRequest()
                    }) {
                        Text(stringResource(R.string.delete))
                    }
                }
            } else {
                //Text(stringResource(R.string.are_you_sure_you_want_to_delete), Modifier.padding(paddingDefault()))
                Text(stringResource(R.string.this_record_repeats_every_month_what_would_you_like_to_delete),
                    Modifier.padding(paddingDefault()))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = paddingDefault()),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(paddingSmall())
                ) {
                    TextButton(onClick = {
                        deleteAll(word)
                        onDismissRequest()
                    }) {
                        Text(stringResource(R.string.delete_all_records))
                    }



                    Button(onClick = {
                        deleteWordAndNext(word)
                        onDismissRequest()
                    }) {
                        Text(stringResource(R.string.delete_this_and_next_ones))
                    }



                    Button(onClick = {
                        deleteThisOccurrenceWord(word)
                        onDismissRequest()
                    }) {
                        Text(stringResource(R.string.delete_only_this))
                    }






                }
            }
        }
    }

}

data class DeleteDetails(val word: Word, val deleteNext: Boolean = false, val deleteAll: Boolean = false)