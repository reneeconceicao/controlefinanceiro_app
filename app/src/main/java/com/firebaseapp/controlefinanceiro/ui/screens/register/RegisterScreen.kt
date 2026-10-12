package com.firebaseapp.controlefinanceiro.ui.screens.register

import android.media.MediaPlayer
import android.view.SoundEffectConstants
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebaseapp.compose_lucide_icons.helpers.IconsHelper
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.entities.Category
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingExtraLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.defaults.paddingTiny
import com.firebaseapp.controlefinanceiro.helpers.dateToString
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.calculator.FieldsCalculator
import com.firebaseapp.controlefinanceiro.ui.components.CategoriesPickerModal
import com.firebaseapp.controlefinanceiro.ui.components.DatePickerModal
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateCategories: (Int) -> Unit,
    viewModel: RegisterViewModel = viewModel(factory = ViewModelProviders.Factory)
) {

    val registerUiState = viewModel.uiState

    val categoriesExpense by viewModel.categoriesExpense.collectAsState()

    val categoriesIncome by viewModel.categoriesIncome.collectAsState()



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
                categoriesExpense = categoriesExpense,
                categoriesIncome = categoriesIncome,
                navigateCategories = navigateCategories

            )

        }
    }

}

@Composable
fun RegisterScreenBody(
    registerUiState: RegisterUiState,
    onUpdate: (RegisterDetails) -> Unit,
    onSave: (RegisterDetails) -> Unit,
    categoriesExpense: List<Category> = listOf(),
    categoriesIncome: List<Category> = listOf(),
    navigateCategories: (Int) -> Unit,

    ) {

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showCategoriesList by rememberSaveable { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(registerUiState.details.selectedOption) {

        if (registerUiState.details.selectedOption == 0) {
            if (registerUiState.details.currentCategoryId !in categoriesExpense.map { it.id }) {
                onUpdate(
                    registerUiState.details.copy(
                        currentCategoryId = 0,
                        categoryName = "",
                        categoryIcon = ""
                    )
                )
            }
        }

        if (registerUiState.details.selectedOption == 1) {
            if (registerUiState.details.currentCategoryId !in categoriesIncome.map { it.id }) {
                onUpdate(
                    registerUiState.details.copy(
                        currentCategoryId = 0,
                        categoryName = "",
                        categoryIcon = ""
                    )
                )
            }
        }

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

    if (showCategoriesList) {
        CategoriesPickerModal(
            categories = if (registerUiState.details.selectedOption == 0) categoriesExpense else categoriesIncome,
            onItemSelected = {
                onUpdate(
                    registerUiState.details.copy(
                        currentCategoryId = it.id,
                        categoryName = it.categoryName,
                        categoryIcon = it.categoryIcon
                    )
                )

            },
            currentOption = registerUiState.details.selectedOption,
            //navigateCategories = navigateCategories,
            onDismissRequest = { showCategoriesList = false })
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
            //.background(Color.Red)
            //.padding(paddingSmall())
        ) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(bottom = 120.dp)

                //.verticalScroll(rememberScrollState())
            ) {


                ButtonsOptions(registerUiState.details.selectedOption, onOptionSelected = {
                    onUpdate(registerUiState.details.copy(selectedOption = it))
                })

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 96.dp)
                ) {
                    items(if (registerUiState.details.selectedOption == 0) categoriesExpense else categoriesIncome) { category ->

                        Column(
                            modifier = Modifier
                                .padding(paddingDefault())
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {

                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

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
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(
                                    paddingTiny()
                                )) {
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

                                                //colorFilter = ColorFilter.tint(Color.Blue)
                                            )


                                        }
                                    }
                                    Text(category.categoryName)
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(
                                    paddingTiny()
                                )) {
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
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(
                                paddingTiny()
                            )) {
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
                                            //colorFilter = ColorFilter.tint(Color.Blue)
                                        )


                                    }
                                }
                                Text(stringResource(R.string.settings))
                            }



                        }
                    }
                }

            }



            if (registerUiState.details.currentCategoryId != 0) {
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
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(Modifier.padding(vertical = paddingDefault())) {

                        FieldsCalculator(
                            initialValue = registerUiState.details.price,
                            initialDate = registerUiState.details.date,
                            notes = registerUiState.details.notes,
                            onEntryValue = {
                                onSave(registerUiState.details.copy(price = it))
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onUpdateNotes = { onUpdate(registerUiState.details.copy(notes = it)) },
                            onUpdatePrice = { onUpdate(registerUiState.details.copy(price = it)) },
                            showDateDialog = { showDatePicker = true })


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
                .clickable {
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
                .clickable {
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
fun DatePickerField(date: Date, onClick: () -> Unit) {
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

@Composable
fun CategoryPickerField(
    categoryId: Int = 0,
    categoryName: String = "",
    categoryIcon: String = "",
    onClick: () -> Unit,
    onRemoveCategory: () -> Unit
) {

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
            stringResource(R.string.select_a_category),
            Modifier
                .align(Alignment.TopStart)
                .padding(paddingSmall()),
        )

        if (categoryId != 0) {
            Button(
                onClick = onRemoveCategory,
                Modifier.align(Alignment.TopEnd),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Red,
                    containerColor = Color.Transparent
                ),
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Outlined.RemoveCircleOutline, "")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .offset(y = paddingDefault())
                .padding(paddingDefault()),

            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        ) {


            Text(
                categoryName.ifEmpty { stringResource(R.string.no_category) },
                fontWeight = FontWeight.SemiBold
            )
//            if (categoryIcon.isNotEmpty()) {
//                Spacer(Modifier.padding(horizontal = paddingSmall()))
//                AsyncImage(
//                    model = "file:///android_asset/${categoryIcon}.svg",
//                    contentDescription = "",
//                    Modifier.size(20.dp),
//                    //colorFilter = ColorFilter.tint(Color.Blue)
//                )
//
//            }


        }

    }
}
