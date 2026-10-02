package com.firebaseapp.controlefinanceiro.ui.screens.register

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.entities.Category
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingExtraLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.helpers.dateToString
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.components.CategoriesPickerModal
import com.firebaseapp.controlefinanceiro.ui.components.CurrencyOutlinedTextField
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
                actions = {
                    Button(
                        onClick = {
                            viewModel.insertWord(registerUiState.details)
                            navigateBack()
                        },
                        Modifier.padding(end = paddingDefault()),
                        border = BorderStroke(1.dp, Color.Gray),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(paddingSmall()),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Check, "")
                            Text(stringResource(R.string.save))

                        }

                    }

                }

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
                categoriesExpense = viewModel.categoriesExpense,
                categoriesIncome = viewModel.categoriesIncome,
                navigateCategories = navigateCategories
            )

        }
    }

}

@Composable
fun RegisterScreenBody(
    registerUiState: RegisterUiState,
    onUpdate: (RegisterDetails) -> Unit,
    categoriesExpense: List<Category> = listOf(),
    categoriesIncome: List<Category> = listOf(),
    navigateCategories: (Int) -> Unit
) {

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showCategoriesList by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(registerUiState.details.selectedOption) {

        if (registerUiState.details.selectedOption == 0) {
            if (registerUiState.details.currentCategoryId !in categoriesExpense.map { it.id }) {
                onUpdate(
                    registerUiState.details.copy(
                        currentCategoryId = 0,
                        categoryName = ""
                    )
                )
            }
        }

        if (registerUiState.details.selectedOption == 1) {
            if (registerUiState.details.currentCategoryId !in categoriesIncome.map { it.id }) {
                onUpdate(
                    registerUiState.details.copy(
                        currentCategoryId = 0,
                        categoryName = ""
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
                        categoryName = it.categoryName,
                        currentCategoryId = it.id
                    )
                )
            },
            currentOption = registerUiState.details.selectedOption,
            //navigateCategories = navigateCategories,
            onDismissRequest = { showCategoriesList = false })
    }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(
                paddingSmall()
            )
    ) {


        ButtonsOptions(registerUiState.details.selectedOption, onOptionSelected = {
            onUpdate(registerUiState.details.copy(selectedOption = it))
        })


        Column(
            Modifier
                .fillMaxWidth()
                .padding(paddingSmall())
        ) {
            CurrencyOutlinedTextField(
                value = registerUiState.details.price,
                onValueChange = {
                    onUpdate(registerUiState.details.copy(price = it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingSmall()),
                shape = RoundedCornerShape(paddingSmall()),
                textStyle = TextStyle(fontSize = 24.sp),
                colors = OutlinedTextFieldDefaults.colors(

                    cursorColor = if (registerUiState.details.selectedOption == 0) colorResource(
                        R.color.dark_red
                    )
                    else colorResource(R.color.dark_green),
                    focusedBorderColor = if (registerUiState.details.selectedOption == 0) colorResource(
                        R.color.dark_red
                    )
                    else colorResource(R.color.dark_green),
                ),
            )


            DatePickerField(
                registerUiState.details.date,
                onClick = { showDatePicker = true })


            CategoryPickerField(
                categoryId = registerUiState.details.currentCategoryId,
                categoryName = registerUiState.details.categoryName,
                onClick = { showCategoriesList = true },
                onRemoveCategory = {
                    onUpdate(registerUiState.details.copy(currentCategoryId = 0, categoryName = ""))
                }
            )



            OutlinedTextField(
                value = registerUiState.details.notes,
                onValueChange = {
                    onUpdate(registerUiState.details.copy(notes = it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingSmall())
                    .padding(vertical = paddingSmall()),

                label = { Text(stringResource(R.string.notes)) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )


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
    onClick: () -> Unit,
    onRemoveCategory: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(paddingDefault()))
            .padding(paddingSmall())
            .padding(top = paddingSmall())
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
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically

    ) {


        Text(
            stringResource(R.string.category_two_dots),
            Modifier
                .padding(paddingSmall())
                .padding(vertical = paddingSmall()),
        )
        Text(
            categoryName.ifEmpty { stringResource(R.string.no_category) },
            fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.weight(1f))
        if (categoryId != 0) {
            Button(
                onClick = onRemoveCategory,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Red,
                    containerColor = Color.Transparent
                ),
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Outlined.RemoveCircleOutline, "")
            }
        }
    }
}
