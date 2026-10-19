package com.firebaseapp.controlefinanceiro.ui.components


import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import android.view.SoundEffectConstants
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.datastore.DataStoreViewModel
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders

@Composable
fun Modifier.hapticClick(
    viewModel: DataStoreViewModel = viewModel(factory = ViewModelProviders.Factory),
    onClick: () -> Unit,
): Modifier {
    val haptic = LocalHapticFeedback.current
    val enabledHaptic by viewModel.isHapticEnabled.collectAsState()
    return this.clickable {
        if (enabledHaptic) {
            haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
        }
        onClick()
    }
}

@Composable
fun Modifier.hapticClick(
    interactionSource: MutableInteractionSource?,
    indication: Indication?,
    viewModel: DataStoreViewModel = viewModel(factory = ViewModelProviders.Factory),
    onClick: () -> Unit,
): Modifier {
    val haptic = LocalHapticFeedback.current
    val enabledHaptic by viewModel.isHapticEnabled.collectAsState()
    return this.clickable(interactionSource = interactionSource, indication = indication) {
        if (enabledHaptic) {
            haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
        }
        onClick()
    }
}

@Composable
fun hapticClick(
    viewModel: DataStoreViewModel = viewModel(factory = ViewModelProviders.Factory),
    onClick: () -> Unit
): () -> Unit {
    val haptic = LocalHapticFeedback.current
    val enabledHaptic by viewModel.isHapticEnabled.collectAsState()

    return {
        if (enabledHaptic) {
            haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
        }
        viewModel.addCounter()
        onClick()
    }
}
