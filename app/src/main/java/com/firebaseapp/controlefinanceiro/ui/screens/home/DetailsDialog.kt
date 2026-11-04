package com.firebaseapp.controlefinanceiro.ui.screens.home

import android.content.Context
import android.util.Log
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
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.entities.CategoryType
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingExtraLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.defaults.paddingTiny
import com.firebaseapp.controlefinanceiro.helpers.ColorsHelper
import com.firebaseapp.controlefinanceiro.helpers.ShareHelper
import com.firebaseapp.controlefinanceiro.helpers.currencyFormat
import com.firebaseapp.controlefinanceiro.helpers.dateToString
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsDialog(
    context: Context,
    wordId: Int,
    index: Long?,
    onEdit: (Word) -> Unit,
    onDelete: (DeleteDetails) -> Unit, // word, index, deleteNext, deleteAll
    onDismissRequest: () -> Unit,
    viewModel: DetailsViewModel = viewModel(factory = ViewModelProviders.Factory),
) {

    val word by viewModel.currentWordStateFlow.collectAsState()

    var imageIndex: Int by rememberSaveable { mutableIntStateOf(0) }

    var showFullScreenPhoto by rememberSaveable { mutableStateOf(false) }

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    viewModel.updateWordIdIndex(wordId, index)

    if (showFullScreenPhoto) {
        PhotoViewDialog(
            initialItem = imageIndex,
            images = word?.images ?: emptyList(),
            onShareFile = { fileName ->
                ShareHelper(context).shareFile(fileName)
            },
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

    BasicAlertDialog(
        onDismissRequest = {
            viewModel.clearWordValue()
            onDismissRequest()
        }) {


        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(paddingDefault())
                )
                .fillMaxWidth()
                .padding(vertical = paddingTiny())
                .padding(start = paddingSmall())
        ) {
            Box(Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    "",
                    Modifier
                        .align(Alignment.TopEnd)
                        .clickable(interactionSource = null, indication = null) {
                            onDismissRequest()
                        }
                        .padding(paddingDefault())
                )
            }



            word?.let { it ->
                val icon = it.categoryIcon

                val color = ColorsHelper(context).colorByName(
                    it.categoryColor
                )

                val type =
                    if (it.type == WordType.Income) CategoryType.Income else CategoryType.Expense

                val typeLabel =
                    if (it.type == WordType.Income) stringResource(R.string.income) else stringResource(
                        R.string.expense
                    )

                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(paddingSmall())
                ) {

                    Text(typeLabel, fontWeight = FontWeight.SemiBold)

                    CategoryContainer(categoryIcon = icon, color = color, type = type)

                    Text(it.categoryName)

                    Spacer(Modifier.height(paddingSmall()))
                }





                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(paddingSmall()),
                    verticalArrangement = Arrangement.spacedBy(paddingSmall())

                ) {
                    Row() {
                        Text("${stringResource(R.string.date)}: ${dateToString(it.date)}")
                    }

                    Row() {
                        Text("${stringResource(R.string.value)}: ${currencyFormat(it.value.toString())}")
                    }

                    if (it.notes.isNotEmpty()) {
                        Text(it.notes)
                    }

                    Spacer(Modifier.height(paddingDefault()))

                    if (it.images.isNotEmpty()) {


                        Text(stringResource(R.string.photos))

                        LazyRow(
                            modifier = Modifier
                                //.padding(horizontal = paddingDefault())
                                .padding(bottom = paddingDefault()),
                            horizontalArrangement = Arrangement.spacedBy(paddingSmall()),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            itemsIndexed(it.images, key = { _, image -> image }) { index, image ->
                                val currentFolder = if (File(context.filesDir, image).exists())
                                    context.filesDir else context.cacheDir

                                val imageFile = File(currentFolder, image).absolutePath

                                AsyncImage(
                                    model = imageFile,
                                    contentDescription = null,
                                    modifier = Modifier
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = paddingSmall()),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                showDeleteDialog = true
                            },
                            colors = ButtonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White,
                                disabledContainerColor = Color.LightGray,
                                disabledContentColor = Color.Black
                            )
                        ) {
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


}

@Composable
fun PhotoViewDialog(
    initialItem: Int,
    images: List<String>,
    onShareFile: (String) -> Unit,
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
            Surface {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
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
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.LightGray)
                        }

                        IconButton(
                            onClick = {
                                onShareFile(images[currentItem])
                                Log.d("TAG", "WordOptionsModal: $currentItem")
                            },
                        ) {
                            Icon(Icons.Default.Share, null, tint = Color.LightGray)

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

                        val file = remember(images[page]) {
                            File(currentFolder, images[page]).absolutePath
                        }

                        Column(
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            AsyncImage(
                                file,
                                contentDescription = null,
                                Modifier
                                    .fillMaxSize()
                                    .zoomable(rememberZoomState())
                            )
                        }

//
//                        val bitmap = remember(images[page]) {
//                            BitmapFactory.decodeFile(
//                                File(currentFolder, images[page]).absolutePath,
//                            )?.asImageBitmap()
//                        }
//
//                        bitmap?.let {
//                            Column(
//                                Modifier
//                                    .fillMaxSize()
//                                    .verticalScroll(rememberScrollState()),
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                                verticalArrangement = Arrangement.Center,
//                            ) {
//                                Image(
//                                    bitmap = it,
//                                    contentDescription = null,
//                                    Modifier
//                                        .fillMaxSize()
//                                        .zoomable(rememberZoomState())
//                                )
//                            }
//
//                        }

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
                Text(
                    stringResource(R.string.are_you_sure_you_want_to_delete), Modifier
                        .padding(paddingDefault())
                        .fillMaxWidth(), textAlign = TextAlign.Start
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = paddingDefault()),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
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
                Text(
                    stringResource(R.string.this_record_repeats_every_month_what_would_you_like_to_delete),
                    Modifier.padding(paddingDefault())
                )
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

data class DeleteDetails(
    val word: Word,
    val deleteNext: Boolean = false,
    val deleteAll: Boolean = false
)