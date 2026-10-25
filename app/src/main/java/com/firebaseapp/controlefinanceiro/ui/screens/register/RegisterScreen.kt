package com.firebaseapp.controlefinanceiro.ui.screens.register

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebaseapp.compose_lucide_icons.helpers.IconsHelper
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.datastore.DataStoreViewModel
import com.firebaseapp.controlefinanceiro.data.entities.Category
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingExtraLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.defaults.paddingTiny
import com.firebaseapp.controlefinanceiro.helpers.PhotoFilesHandler
import com.firebaseapp.controlefinanceiro.helpers.dateToString
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.calculator.Calculator
import com.firebaseapp.controlefinanceiro.ui.components.DatePickerModal
import com.firebaseapp.controlefinanceiro.ui.components.ImageOptionsDialog
import com.firebaseapp.controlefinanceiro.ui.components.PhotosModal
import com.firebaseapp.controlefinanceiro.ui.components.hapticClick
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.time.LocalDateTime


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateCategories: (Int) -> Unit,
    viewModel: RegisterViewModel = viewModel(factory = ViewModelProviders.Factory),
    navigateCamera: () -> Unit,
    resultForCamera: String?,
) {

    val registerUiState by viewModel.uiState.collectAsState()

    Log.d("TAG", "Back: $resultForCamera")

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                title = { Text(stringResource(R.string.register)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.back)
                        )
                    }
                },

                )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding(),
        ) {


            RegisterScreenBody(
                registerUiState = registerUiState,
                onUpdate = viewModel::updateUiState,
                onSave = {
                    viewModel.insertWord(it)
                    navigateBack()
                },
                categoriesExpense = registerUiState.details.categoriesExpense,
                categoriesIncome = registerUiState.details.categoriesIncome,
                images = registerUiState.details.images,
                navigateCategories = navigateCategories,
                navigateCamera = navigateCamera,
                resultForCamera = resultForCamera
            )

        }
    }

}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenBody(
    registerUiState: RegisterUiState,
    onUpdate: (RegisterDetails) -> Unit,
    onSave: (RegisterDetails) -> Unit,
    categoriesExpense: List<Category> = listOf(),
    categoriesIncome: List<Category> = listOf(),
    navigateCategories: (Int) -> Unit,
    navigateCamera: () -> Unit,
    resultForCamera: String?,
    images: List<String> = mutableListOf(),
) {

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showPhotoOptionsDialog by rememberSaveable { mutableStateOf(false) }
    var showPhotosDialog by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(contract =
        ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val stream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(stream)
            val savedFileName = PhotoFilesHandler(context).saveBitmapToCache(bitmap)
            val newImages = images + savedFileName
            onUpdate(registerUiState.details.copy(images = newImages))
        }
    }

    // flag to control the resultForCamera consumption
    var cameraResultFlag by remember { mutableStateOf(resultForCamera) }

    cameraResultFlag?.let { result ->
        // if flag is not null update the view model
        val newImages = images + result
        onUpdate(registerUiState.details.copy(images = newImages))
        cameraResultFlag = null // turn this flag null to avoid recurrency results
    }

    if (showPhotosDialog) {
        val context = LocalContext.current
        PhotosModal(
            context = context,
            images = images,
            onAddImage = {
                showPhotoOptionsDialog = true
            },
            onItemDeleted = {
                val newImages = images - it
                onUpdate(registerUiState.details.copy(images = newImages))
            },
            onDismissRequest = { showPhotosDialog = false })
    }

    if (showDatePicker) {
        DatePickerModal(
            date = registerUiState.details.date,
            onDateSelected = { selectedDate ->
                if (selectedDate != null) {
                    onUpdate(registerUiState.details.copy(date = selectedDate))
                }
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }

    if (showPhotoOptionsDialog) {
        ImageOptionsDialog(
            onCameraSelected = navigateCamera,
            onGallerySelected = {
                launcher.launch("image/*")
            },
            onDismissRequest = { showPhotoOptionsDialog = false })
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(
                paddingSmall()
            )
    ) {


        Box(
            Modifier
                .fillMaxSize()
        ) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(bottom = 120.dp)
            ) {

                ButtonsOptions(
                    registerUiState.details.selectedOption,
                    onOptionSelected = { option ->
                        if (registerUiState.details.selectedOption != option) {
                            onUpdate(
                                registerUiState.details.copy(
                                    selectedOption = option,
                                    currentCategoryId = 0,
                                    categoryName = "",
                                    categoryIcon = ""
                                )
                            )
                        } else {
                            onUpdate(registerUiState.details.copy(selectedOption = option))
                        }
                    })

                CategoryGrid(
                    registerUiState = registerUiState,
                    categoriesExpense = categoriesExpense,
                    categoriesIncome = categoriesIncome,
                    onUpdate = onUpdate,
                    navigateCategories = navigateCategories
                )

            }


            AnimatedVisibility(
                visible = registerUiState.details.currentCategoryId != 0,
                modifier = Modifier.align(
                    Alignment.BottomCenter,
                ),
                enter = slideInVertically(
                    initialOffsetY = { it }
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it }
                )
            ) {
                Surface(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = paddingLarge())
                        .fillMaxWidth()
                        .imePadding(),

                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomEnd = 20.dp,
                        bottomStart = 20.dp
                    ),
                    shadowElevation = 8.dp,
                    color = colorResource(R.color.dark_gray)//MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(Modifier.padding(vertical = paddingDefault())) {


                        FieldsCalculator(
                            details = registerUiState.details,
                            onEntryValue = {
                                onSave(it)
                                haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                            },
                            onUpdateNotes = {
                                onUpdate(registerUiState.details.copy(notes = it))
                            },
                            onUpdatePrice = {
                                onUpdate(registerUiState.details.copy(price = it))
                            },
                            onUpdateRecurrency = {
                                onUpdate(registerUiState.details.copy(isRecurring = it))
                            },
                            showDateDialog = { showDatePicker = true },
                            showTakePhotoDialog = {
                                if (images.isEmpty()) {
                                    showPhotoOptionsDialog = true
                                } else {
                                    showPhotosDialog = true
                                }
                            }
                        )


                    }
                }
            }
        }
    }
}

@Composable
fun ButtonsOptions(selectedOption: Int, onOptionSelected: (Int) -> Unit) {
    Row(
        Modifier
            .padding(paddingSmall())
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {


        Card(
            Modifier
                .padding(paddingSmall())
                .weight(1f)
                .hapticClick {
                    onOptionSelected(0)
                },
            colors = CardDefaults.cardColors(
                containerColor = if (selectedOption == 0) colorResource(
                    R.color.dark_red
                ) else Color.Gray,
                contentColor = Color.White//if (selectedOption == 1) MaterialTheme.colorScheme.onPrimary else Color.White
            )
        ) {
            Text(
                stringResource(R.string.add_expense),
                Modifier
                    .padding(paddingDefault())
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        Card(
            Modifier
                .padding(paddingSmall())
                .weight(1f)
                .hapticClick {
                    onOptionSelected(1)
                },
            colors = CardDefaults.cardColors(
                containerColor = if (selectedOption == 1) colorResource(
                    R.color.dark_green
                ) else Color.Gray,
                contentColor = Color.White//if (selectedOption == 0) Color.White else Color.LightGray
            )
        ) {
            Text(
                stringResource(R.string.add_income),
                Modifier
                    .padding(paddingDefault())
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun CategoryGrid(
    registerUiState: RegisterUiState,
    categoriesExpense: List<Category> = listOf(),
    categoriesIncome: List<Category> = listOf(),
    onUpdate: (RegisterDetails) -> Unit,
    navigateCategories: (Int) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 96.dp)
    ) {
        items(if (registerUiState.details.selectedOption == 0) categoriesExpense else categoriesIncome) { category ->

            Column(
                modifier = Modifier
                    .padding(paddingDefault())
                    .hapticClick(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {

                        onUpdate(
                            registerUiState.details.copy(
                                currentCategoryId = category.id,
                                categoryName = category.categoryName,
                                categoryIcon = category.categoryIcon
                            )
                        )

                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (registerUiState.details.currentCategoryId == category.id) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            paddingTiny()
                        )
                    ) {
                        Box(
                            Modifier

                                .background(
                                    color = colorResource(R.color.yellow),
                                    shape = CircleShape
                                )
                        ) {
                            Column(
                                Modifier
                                    .padding(paddingDefault())
                                    .size(24.dp)
                            ) {
                                Icon(
                                    painter = IconsHelper.iconByName(category.categoryIcon),
                                    contentDescription = "",
                                )
                            }
                        }
                        Text(category.categoryName)
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            paddingTiny()
                        )
                    ) {
                        Box(
                            Modifier

                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                )
                        ) {
                            Column(
                                Modifier
                                    .padding(paddingDefault())
                                    .size(24.dp)
                            ) {
                                Icon(
                                    painter = IconsHelper.iconByName(category.categoryIcon),
                                    contentDescription = "",


                                    //colorFilter = ColorFilter.tint(Color.Blue)
                                )


                            }
                        }
                        Text(category.categoryName)
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .padding(paddingDefault())
                    .clickable {
                        navigateCategories(registerUiState.details.selectedOption)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        paddingTiny()
                    )
                ) {
                    Box(
                        Modifier

                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    ) {
                        Column(
                            Modifier
                                .padding(paddingDefault())
                                .size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "",
                                Modifier.size(24.dp),
                            )


                        }
                    }
                    Text(stringResource(R.string.settings))
                }


            }
        }
    }
}

@Composable
fun DatePickerField(date: LocalDateTime, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(paddingDefault()))
            .padding(paddingSmall())
            .padding(top = paddingDefault())
            .border(
                0.5.dp,
                Color.Gray,
                RoundedCornerShape(paddingSmall())
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() })
            {
                onClick()
            }

    ) {
        Spacer(
            Modifier
                .padding(vertical = paddingExtraLarge() + paddingDefault())
        )

        Text(
            stringResource(R.string.select_the_date),
            Modifier
                .align(Alignment.TopStart)
                .padding(paddingSmall()),
        )
        Text(
            dateToString(date), Modifier
                .align(Alignment.Center)
                .offset(y = paddingSmall())
                .padding(paddingDefault()),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FieldsCalculator(
    details: RegisterDetails,
    onUpdatePrice: (String) -> Unit,
    onUpdateNotes: (String) -> Unit,
    onUpdateRecurrency: (Boolean) -> Unit,
    onEntryValue: (RegisterDetails) -> Unit,
    showDateDialog: () -> Unit,
    showTakePhotoDialog: () -> Unit,
    viewModelDataStore: DataStoreViewModel = viewModel(factory = ViewModelProviders.Factory)
) {

    val isCompleteCalculator by viewModelDataStore.isHapticEnabled.collectAsState()
    val isKeyboardVisible = WindowInsets.isImeVisible
    var visible by remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var result by remember { mutableStateOf("0") }


    LaunchedEffect(isKeyboardVisible) {
        if (isKeyboardVisible) {
            visible = false
        } else {
            visible = true
            focusManager.clearFocus()
        }
    }


    Text(
        text = result,
        Modifier
            .fillMaxWidth()
            .padding(end = paddingDefault()),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = TextStyle(
            fontSize = 40.sp,
            textAlign = TextAlign.End,
        )
    )

    Box(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = details.notes,
            onValueChange = {
                onUpdateNotes(it)
            },

            trailingIcon = {

            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingDefault())
                .padding(top = paddingSmall()),
            //label = { Text(stringResource(R.string.notes)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            leadingIcon = {
                Text(
                    "${stringResource(R.string.notes)}:",
                    modifier = Modifier.padding(start = paddingSmall())
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Log.d("TAG", "FieldsCalculator: ${details.images.size}")
        IconButton(
            onClick = {

                showTakePhotoDialog()
            }, Modifier
                .align(Alignment.CenterEnd)
                .padding(end = paddingDefault(), top = paddingSmall())
        ) {
            BadgedBox(badge = {
                if (details.images.isNotEmpty()) {
                    Badge(
                        containerColor = colorResource(R.color.yellow),
                        contentColor = Color.Black
                    ) {
                        Text("${details.images.size}")
                    }
                }
            }, Modifier.padding(paddingTiny())) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    "",
                )
            }

        }

    }


    AnimatedVisibility(!visible) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Button(
                onClick = {
                    keyboardController?.hide()
                    visible = true
                }, Modifier
                    .padding(paddingSmall())
                    .padding(end = paddingSmall())
            ) {
                Icon(imageVector = Icons.Default.KeyboardDoubleArrowDown, "")
            }
        }

    }

    AnimatedVisibility(visible) {
        Calculator(
            details = details,
            onEntryValue = onEntryValue,
            onUpdatePrice = {
                onUpdatePrice(it)
            },
            onUpdateInput = { result = it },
            showDateDialog = showDateDialog,
            isCompleteCalculator = isCompleteCalculator,
        )
    }

    if (!details.isEditRecurring) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = paddingTiny())
        ) {
            Checkbox(
                details.isRecurring,
                onCheckedChange = {
                    onUpdateRecurrency(it)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = colorResource(R.color.yellow),
                )
            )
            Text(stringResource(R.string.repeat_every_month))
        }
    }


}


//@Composable
//fun CategoryPickerField(
//    categoryId: Int = 0,
//    categoryName: String = "",
//    categoryIcon: String = "",
//    onClick: () -> Unit,
//    onRemoveCategory: () -> Unit
//) {
//
//    Box(
//        Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(paddingDefault()))
//            .padding(paddingSmall())
//            .padding(top = paddingDefault())
//            .border(
//                0.5.dp,
//                Color.Gray,
//                RoundedCornerShape(paddingSmall())
//            )
//            .clickable(
//                indication = null,
//                interactionSource = remember { MutableInteractionSource() })
//            {
//                onClick()
//            }
//
//    ) {
//        Spacer(
//            Modifier
//                .padding(vertical = paddingExtraLarge() + paddingDefault())
//        )
//
//        Text(
//            stringResource(R.string.select_a_category),
//            Modifier
//                .align(Alignment.TopStart)
//                .padding(paddingSmall()),
//        )
//
//        if (categoryId != 0) {
//            Button(
//                onClick = onRemoveCategory,
//                Modifier.align(Alignment.TopEnd),
//                colors = ButtonDefaults.buttonColors(
//                    contentColor = Color.Red,
//                    containerColor = Color.Transparent
//                ),
//                shape = CircleShape
//            ) {
//                Icon(imageVector = Icons.Outlined.RemoveCircleOutline, "")
//            }
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.Center)
//                .offset(y = paddingDefault())
//                .padding(paddingDefault()),
//
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//
//        ) {
//
//
//            Text(
//                categoryName.ifEmpty { stringResource(R.string.no_category) },
//                fontWeight = FontWeight.SemiBold
//            )
//
//        }
//
//    }
//}
