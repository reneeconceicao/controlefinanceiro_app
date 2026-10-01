package com.firebaseapp.controlefinanceiro.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.firebaseapp.controlefinanceiro.data.entities.Category
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesPickerModal(
    categories: List<Category>,
    currentOption: Int,
    onItemSelected: (Category) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Box(Modifier.fillMaxWidth()) {

            Text(
                if (currentOption == 0) "Select a expense category" else "Select a income category",
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {},
                Modifier
                    .padding(horizontal = paddingSmall())
                    .align(Alignment.CenterEnd),
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = Color.Transparent
                )
            ) {
                Icon(imageVector = Icons.Default.Settings, "")
            }
        }
        LazyColumn {
            items(categories) { category ->
                ListItem(
                    headlineContent = { Text(category.categoryName) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onItemSelected(category)
                            onDismissRequest()
                        }
                )
            }
        }

    }
}