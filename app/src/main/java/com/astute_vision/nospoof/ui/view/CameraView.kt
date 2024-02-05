package com.astute_vision.nospoof.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.DisplayMetrics
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraView(modifier: Modifier = Modifier, vm: CameraViewModel, navController: NavController) {
    val result by vm.faceMeshResults.collectAsState()
    val context = LocalContext.current

    val previewView = remember {
        PreviewView(context)
    }
    val preview = Preview.Builder().build()

    var showTriangles by remember {
        mutableStateOf(false)
    }


    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraSelector by remember { vm.cameraSelector }


    suspend fun setPreview() {
        val capture = ImageAnalysis.Builder()
            .setTargetResolution(Size(previewView.width, previewView.height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().apply {
                this.setAnalyzer(
                    ContextCompat.getMainExecutor(context), vm.imageAnalysis
                )
            }
        val cameraProvider = context.getCameraProvider(ContextCompat.getMainExecutor(context))


        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, capture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)

    }


    LaunchedEffect(key1 = cameraSelector) {
        setPreview()
    }



    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            { previewView },
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(visible = result != null && showTriangles) {
            val bm = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                flip(result!!.bitmap)
            } else {
                result!!.bitmap
            }
            Image(bitmap = bm.asImageBitmap(), contentDescription = "")
        }

        AnimatedVisibility(visible = result != null && result?.statusMessage?.isNotEmpty() == true) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp, start = 8.dp)
                    .background(Color.DarkGray, RoundedCornerShape(24.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = result!!.statusMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Показывать маску")
                    Spacer(Modifier.padding(2.dp))
                    Switch(
                        checked = showTriangles,
                        onCheckedChange = { showTriangles = !showTriangles })
                }
            }
        }


        Button(
            onClick = {
                vm.toggleCamera()
            }, modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        ) {
            Icon(imageVector = Icons.Default.FlipCameraAndroid, "")
        }
    }
}


private suspend fun Context.getCameraProvider(executor: Executor): ProcessCameraProvider =
    suspendCoroutine { ct ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                ct.resume(cameraProvider.get())
            }, executor)
        }
    }

fun flip(d: Bitmap): Bitmap {
    val m = Matrix()
    m.preScale(-1f, 1f)
    val dst = Bitmap.createBitmap(d, 0, 0, d.width, d.height, m, false)
    dst.density = DisplayMetrics.DENSITY_DEFAULT
    return dst
}