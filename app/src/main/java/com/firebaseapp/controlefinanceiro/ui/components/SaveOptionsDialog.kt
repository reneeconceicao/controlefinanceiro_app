package com.firebaseapp.controlefinanceiro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.defaults.paddingTiny

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveOptionsDialog(
    thisMonthSelected: () -> Unit,
    thisMothAndNextSelected: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
    ) {
        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(paddingDefault())
                )
                .padding(all = paddingSmall())
        ) {

            Text(
                stringResource(R.string.do_you_want_to_edit_only_this_month_or_this_month_and_future_months),
                Modifier
                    .padding(vertical = paddingDefault())
                    .padding(horizontal = paddingSmall())
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(all = paddingSmall()),
                verticalArrangement = Arrangement.spacedBy(paddingTiny()),
                horizontalAlignment = Alignment.End
            ) {
                Button(onClick = {
                    thisMonthSelected()
                    onDismissRequest()
                }) {
                    Text(stringResource(R.string.only_this_month))
                }

                TextButton(onClick = {
                    thisMothAndNextSelected()
                    onDismissRequest()
                }) {
                    Text(stringResource(R.string.this_and_future_months))
                }
            }
        }
    }
}