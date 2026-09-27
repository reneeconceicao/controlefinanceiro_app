package com.firebaseapp.controlefinanceiro.ui.screens.main

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingExtraLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.helpers.dateToString
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.components.CategoriesPickerModal
import com.firebaseapp.controlefinanceiro.ui.components.CurrencyOutlinedTextField
import com.firebaseapp.controlefinanceiro.ui.components.DatePickerModal
import java.math.BigDecimal
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: RegisterViewModel = viewModel(factory = ViewModelProviders.Factory)
) {

    var selectedOption by rememberSaveable { mutableIntStateOf(0) }
    var price by rememberSaveable { mutableStateOf("0") }
    var date by rememberSaveable { mutableStateOf(Date(System.currentTimeMillis())) }
    var notes by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var categories by rememberSaveable { mutableStateOf(listOf("")) }

    LaunchedEffect(selectedOption) {

        if (selectedOption == 0) {
            categories = listOf(
                "Food",
                "Car",
                "House",
                "Table",
                "Kart",
                "Father",
                "Beach",
                "Rest",
                "Bindable",
                "Basket",
                "Clothes",
                "Flights",
                "Food",
                "Car",
                "House",
                "Table",
                "Kart",
                "Father",
                "Beach",
                "Rest",
                "Bindable",
                "Basket",
                "Clothes",
                "Flights"
            )
        } else {
            categories = listOf(
                "Fish",
                "Sea",
                "Rest"
            )
        }

        if (category !in categories) {
            category = ""
        }
    }

    var showDatePicker by rememberSaveable() { mutableStateOf(false) }
    var showCategoriesList by rememberSaveable() { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                title = { Text("Register") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            "Back"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {

                            val word = Word(
                                date = date,
                                type = if (selectedOption == 0) WordType.Income else WordType.Expense,
                                value = BigDecimal(price),
                                categoryId = 0,
                                categoryName = "",
                                notes = notes
                            )
                            viewModel.insertWord(word)
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
                            Text("SAVE")

                        }

                    }

                }

            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
        ) {


            if (showDatePicker) {
                DatePickerModal(
                    date = date,
                    onDateSelected = { selectedDate ->
                        if (selectedDate != null) {
                            date = selectedDate
                        }
                    },
                    onDismiss = {
                        showDatePicker = false
                    }
                )
            }

            if (showCategoriesList) {
                CategoriesPickerModal(
                    categories = categories,
                    onItemSelected = { category = it },
                    currentOption = selectedOption,
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


                ButtonsOptions(selectedOption, onOptionSelected = {
                    selectedOption = it
                })


                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(paddingSmall())
                ) {
                    CurrencyOutlinedTextField(
                        value = price,
                        onValueChange = {
                            price = it
                            Log.d("TAG", "RegisterScreen: $price")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = paddingSmall()),
                        shape = RoundedCornerShape(paddingSmall()),
                        textStyle = TextStyle(fontSize = 24.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (selectedOption == 0) colorResource(R.color.dark_green)
                            else colorResource(R.color.dark_red),
                        ),
                    )


                    DatePickerField(date, onClick = { showDatePicker = true })

                    CategoryPickerField(
                        category = category,
                        onClick = { showCategoriesList = true },
                        onRemoveCategory = { category = "" }
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = {
                            notes = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = paddingSmall())
                            .padding(vertical = paddingSmall()),

                        label = { Text("Notes") },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )


                }


//                SaveButton(selectedOption) {
//
//                }

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
                    R.color.dark_green
                ) else Color.Gray,
                contentColor = Color.White//if (selectedOption == 0) Color.White else Color.LightGray
            )
        ) {
            Text(
                "+ Income",
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
                    R.color.dark_red
                ) else Color.Gray,
                contentColor = Color.White//if (selectedOption == 1) MaterialTheme.colorScheme.onPrimary else Color.White
            )
        ) {
            Text(
                "- Expense",
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
            "Select the date",
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
    category: String,
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
            "Category: ",
            Modifier
                .padding(paddingSmall())
                .padding(vertical = paddingSmall()),
        )
        Text(category.ifEmpty { "No Category" }, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.weight(1f))
        if (category.isNotEmpty()) {
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

@Composable
fun SaveButton(selectedOption: Int, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(paddingDefault())
    ) {
        Button(
            onClick = onClick, Modifier.weight(1f), colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedOption == 0) colorResource(
                    R.color.dark_green
                ) else colorResource(
                    R.color.dark_red
                ),
            )
        ) {
            Text(
                if (selectedOption == 0) "Register income" else "Register expense",
                Modifier.padding(paddingSmall()),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}