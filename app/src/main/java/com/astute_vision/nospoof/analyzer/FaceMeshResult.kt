package com.astute_vision.nospoof.analyzer

import android.graphics.Bitmap

data class FaceMeshProcessingResult(
    val bitmap: Bitmap,
    val statusMessage: String
)
