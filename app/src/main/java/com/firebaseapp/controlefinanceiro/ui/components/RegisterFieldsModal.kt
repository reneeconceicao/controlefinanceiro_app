package com.firebaseapp.controlefinanceiro.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebaseapp.compose_lucide_icons.helpers.IconsHelper
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.entities.Category
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.ui.screens.register.DatePickerField
import com.firebaseapp.controlefinanceiro.ui.screens.register.RegisterDetails
import com.firebaseapp.controlefinanceiro.ui.screens.register.RegisterUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterFieldModal(
    registerUiState: RegisterUiState,
    onUpdate: (RegisterDetails) -> Unit,
    showDatePicker: () -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {

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
                onClick = { showDatePicker() })

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