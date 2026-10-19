package com.firebaseapp.controlefinanceiro.ui.calculator

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.datastore.DataStoreViewModel
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.screens.register.RegisterDetails
import java.time.LocalDateTime
import java.util.Date

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FieldsCalculator(
    details: RegisterDetails,
    onUpdatePrice: (String) -> Unit,
    onUpdateNotes: (String) -> Unit,
    onEntryValue: (RegisterDetails) -> Unit,
    showDateDialog: () -> Unit,
    viewModelDataStore: DataStoreViewModel = viewModel(factory = ViewModelProviders.Factory)
) {

    val isCompleteCalculator by viewModelDataStore.isHapticEnabled.collectAsState()
    //var notes by remember { mutableStateOf("") }
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

    Column() {

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
        IconButton(
            onClick = { }, Modifier
                .align(Alignment.CenterEnd)
                .padding(end = paddingDefault(), top = paddingSmall())
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                "",
            )
        }

    }


    AnimatedVisibility(!visible) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Button(onClick = {
                keyboardController?.hide()
                visible = true
            }, Modifier
                .padding(paddingSmall())
                .padding(end = paddingSmall())) {
                Icon(imageVector = Icons.Default.KeyboardDoubleArrowDown, "")
            }
        }

    }

    AnimatedVisibility(visible) {
        Calculator(
            details = details,
            onEntryValue = onEntryValue,
            onUpdatePrice = {
                Log.d("TAG", "FieldsCalculator price: $it")
                onUpdatePrice(it)
            },
            onUpdateInput = { result = it },
            showDateDialog = showDateDialog,
            isCompleteCalculator = isCompleteCalculator,
        )
    }


}

