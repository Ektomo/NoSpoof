package com.astute_vision.nospoof.analyzer

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlin.system.measureTimeMillis


class ImageAnalyzer(
    private val processor: FaceMeshProcessor
) : ImageAnalysis.Analyzer {


    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val time = measureTimeMillis {
            processor.processImage(imageProxy)
        }
        Log.d("timer", time.toString())

    }
}

