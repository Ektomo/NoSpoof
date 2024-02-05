package com.astute_vision.nospoof.ui.view

import androidx.camera.core.CameraSelector
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astute_vision.nospoof.analyzer.FaceMeshProcessingResult
import com.astute_vision.nospoof.analyzer.FaceMeshProcessor
import com.astute_vision.nospoof.analyzer.FaceMeshResultListener
import com.astute_vision.nospoof.analyzer.ImageAnalyzer
import com.astute_vision.nospoof.classification.Classifier
import com.google.mlkit.vision.facemesh.FaceMeshDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    classifier: Classifier
) : ViewModel(), FaceMeshResultListener {

    private val _faceMeshResults = MutableStateFlow<FaceMeshProcessingResult?>(null)
    val faceMeshResults: StateFlow<FaceMeshProcessingResult?> = _faceMeshResults

    private val processor = FaceMeshProcessor(this, classifier)
    val imageAnalysis = ImageAnalyzer(processor)

    private val _cameraSelector = mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    val cameraSelector: State<CameraSelector> = _cameraSelector

    // Функция для переключения камеры
    fun toggleCamera() {
        _cameraSelector.value = if (_cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
    }



    override fun onResult(result: FaceMeshProcessingResult) {
        viewModelScope.launch {
            _faceMeshResults.emit(result)
        }

    }

}