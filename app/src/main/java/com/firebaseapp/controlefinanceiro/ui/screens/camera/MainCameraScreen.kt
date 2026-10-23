@file:OptIn(ExperimentalPermissionsApi::class)

package com.firebaseapp.controlefinanceiro.ui.screens.camera

import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun MainCameraScreen(
    navigateBack: (String) -> Unit,
    onCancel: () -> Unit
) {

    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    MainContent(
        hasPermission = cameraPermissionState.status.isGranted,
        onRequestPermission = cameraPermissionState::launchPermissionRequest,
        navigateBack = navigateBack,
        onCancel = onCancel
    )
}

@Composable
private fun MainContent(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    navigateBack: (String) -> Unit,
    onCancel: () -> Unit
) {

    if (hasPermission) {
        CameraScreen(
            navigateBack = navigateBack
        )
    } else {
        NoPermissionScreen(
            onRequestPermission = onRequestPermission,
            onCancel = onCancel
        )
    }
}
