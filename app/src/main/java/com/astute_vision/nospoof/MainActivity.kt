package com.astute_vision.nospoof

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.astute_vision.nospoof.navigation.NavigationGraph
import com.astute_vision.nospoof.ui.theme.NoSpoofTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoSpoofTheme {


                val navController = rememberNavController()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraPermissionRequest {
                        NavigationGraph(navController)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionRequest(onPermissionGranted: @Composable () -> Unit) {
    val cameraPermissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    when (cameraPermissionState.status) {
        PermissionStatus.Granted -> {
            // Разрешение предоставлено: продолжаем к функционалу камеры
            onPermissionGranted()
        }
        else -> {
            // Объясняем необходимость разрешения и запрашиваем его снова
            Text("Необходим доступ к камере для продолжения работы")
            PermissionRequestButton(permissionState = cameraPermissionState)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequestButton(permissionState: PermissionState) {
    Button(onClick = { permissionState.launchPermissionRequest() }) {
        Text("Запросить разрешение на камеру")
    }
}
