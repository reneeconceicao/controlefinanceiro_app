package com.firebaseapp.controlefinanceiro.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigateExpenseCategories: () -> Unit,
    navigateIncomeCategories: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(paddingDefault())
            .verticalScroll(rememberScrollState()),
    ) {

        Text(
            "Categories",
            Modifier.padding(paddingDefault()),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Card(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().clickable {
                navigateExpenseCategories()
            }) {
                Text(
                    "Edit expense categories",
                    Modifier
                        .padding(paddingSmall())
                        .padding(start = paddingSmall())
                        .padding(top = paddingSmall())
                        .fillMaxWidth()

                )
            }


            HorizontalDivider()
            Box(Modifier.fillMaxWidth().clickable {
                navigateExpenseCategories()
            }) {
                Text(
                    "Edit income categories",
                    Modifier
                        .padding(paddingSmall())
                        .padding(start = paddingSmall())
                        .padding(bottom = paddingSmall())
                        .fillMaxWidth())
            }
        }

    }
}