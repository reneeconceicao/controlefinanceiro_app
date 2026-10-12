package com.firebaseapp.controlefinanceiro.ui.calculator

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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import java.util.Date

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FieldsCalculator(
    initialValue: String,
    initialDate: Date,
    notes: String,
    onUpdatePrice: (String) -> Unit,
    onUpdateNotes: (String) -> Unit,
    onEntryValue: (String) -> Unit,
    showDateDialog: () -> Unit,
) {

    //var notes by remember { mutableStateOf("") }
    val isKeyboardVisible = WindowInsets.isImeVisible
    var visible by remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current


    LaunchedEffect(isKeyboardVisible) {
        if (isKeyboardVisible) {
            visible = false
        } else {
            visible = true
            focusManager.clearFocus()
        }
    }

    Box(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = notes,
            onValueChange = {
                onUpdateNotes(it)
            },
            trailingIcon = {

            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingSmall())
                .padding(vertical = paddingSmall()),
            label = { Text(stringResource(R.string.notes)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            )
        )
        IconButton(onClick = { }, Modifier
            .align(Alignment.CenterEnd)
            .padding(end = paddingDefault(), top = paddingSmall())) {
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
            }, Modifier.padding(paddingSmall())) {
                Icon(imageVector = Icons.Default.Done, "")
            }
        }

    }

    AnimatedVisibility(visible) {
        Calculator(
            initialValue = initialValue,
            initialDate = initialDate,
            onEntryValue = onEntryValue,
            onUpdatePrice = onUpdatePrice,
            showDateDialog = showDateDialog
        )
    }


}

