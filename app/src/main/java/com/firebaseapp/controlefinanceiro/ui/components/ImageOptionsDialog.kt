package com.firebaseapp.controlefinanceiro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.defaults.paddingTiny

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageOptionsDialog(
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(paddingDefault())
                )
                .padding(vertical = paddingTiny(), horizontal = paddingDefault())
        ) {
            TextButton(
                onClick = {
                    onCameraSelected()
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(paddingSmall())
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, "")
                    Text(stringResource(R.string.camera), fontSize = 18.sp)
                }
            }

            TextButton(
                onClick = {
                    onGallerySelected()
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(paddingSmall())
                ) {
                    Icon(imageVector = Icons.Default.PhotoLibrary, "")
                    Text(stringResource(R.string.gallery), fontSize = 18.sp)
                }
            }
        }
    }
}