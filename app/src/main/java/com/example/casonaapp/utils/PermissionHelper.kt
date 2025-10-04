package com.example.casonaapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat

object PermissionHelper {
    fun hasAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun rememberAudioPermissionState(): AudioPermissionState {
    val permissionState = remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState.value = isGranted
    }

    return object : AudioPermissionState {
        override val hasPermission: Boolean
            get() = permissionState.value

        override fun requestPermission() {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
}

interface AudioPermissionState {
    val hasPermission: Boolean
    fun requestPermission()
}