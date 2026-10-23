package com.firebaseapp.controlefinanceiro.ui.screens.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.firebaseapp.compose_lucide_icons.defaults.paddingDefault
import com.firebaseapp.compose_lucide_icons.defaults.paddingLarge
import com.firebaseapp.compose_lucide_icons.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.R

@Composable
fun NoPermissionScreen(
    onRequestPermission: () -> Unit,
    onCancel: () -> Unit
) {

    NoPermissionContent(
        onRequestPermission = onRequestPermission,
        onCancel = onCancel
    )
}

@Composable
private fun NoPermissionContent(
    onRequestPermission: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = paddingDefault()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.to_take_photos_grant_the_app_permission_to_access_the_camera),
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(paddingDefault())
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(paddingDefault()))
            Column(
                verticalArrangement = Arrangement.spacedBy(paddingSmall()),
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(onClick = onRequestPermission) {
                    Text(text = stringResource(R.string.continue_), fontSize = 16.sp)
                }

                TextButton(onClick = onCancel) {
                    Text(text = stringResource(R.string.later), fontSize = 16.sp)
                }
            }
        }
    }

}

