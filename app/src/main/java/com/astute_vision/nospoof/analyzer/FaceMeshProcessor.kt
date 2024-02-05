package com.astute_vision.nospoof.analyzer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.DisplayMetrics
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.astute_vision.nospoof.classification.Classifier
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.common.Triangle
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions
import com.google.mlkit.vision.facemesh.FaceMeshPoint
import java.util.Vector
import kotlin.math.abs

private fun PointF3D.delta(oldPosition: FaceMeshPoint?): Float {
    return if (oldPosition != null) {
        val vector = Vector<Float>(3)
        vector.add(abs(this.x - oldPosition.position.x))
        vector.add(abs(this.y - oldPosition.position.y))
        vector.add(abs(this.z - oldPosition.position.z))
        vector.sum()
    } else 0f
}

private const val DOTS = 1

@OptIn(ExperimentalGetImage::class)
class FaceMeshProcessor(private val resultListener: FaceMeshResultListener, private val cls: Classifier) {
    private val faceMeshClient = FaceMeshDetection.getClient(
        FaceMeshDetectorOptions.Builder()
            .setUseCase(FaceMeshDetectorOptions.FACE_MESH)
            .build()
    )

    private var previousMeshPoints: MutableList<FaceMeshPoint>? = null
    private var previousPolygons: List<Triangle<FaceMeshPoint>>? = null
    private var noMicroTickCounter = 0
    private var frameCounter = 0
    private var STARTED_NOW = 0
    private var analyzeLimit = 15
    private var attackLimit = 8
    private var attackLimitPixels = 2
    private var predictorCount = 0
    private var algoType = DOTS
    private var statusText = ""

    fun processImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        faceMeshClient.process(image)
            .addOnSuccessListener { result ->
                val p = Paint()
                p.style = Paint.Style.STROKE
                p.isAntiAlias = true
                p.isFilterBitmap = true
                p.isDither = true
                p.color = Color.GREEN

                for (faceMesh in result) {

                    val bitmap = Bitmap.createBitmap(
                        image.height,
                        image.width,
                        Bitmap.Config.ARGB_8888
                    )

                    val croppedBitmap = Bitmap.createBitmap(
                        imageProxy.toBitmap().rotate(-90f),
                        0,
                        0,
                        imageProxy.height,
                        imageProxy.width
                    )

                    val predictResult = cls.predict(croppedBitmap)
                    val predict = predictResult[1] >= 0.4
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(Color.TRANSPARENT)

                    // Счетчик признаков совпадения двух кадров между собой
                    var localCounter = 0
                    if (algoType == DOTS) {
                        if (previousMeshPoints == null) {
                            previousMeshPoints = faceMesh.allPoints
                            STARTED_NOW = 1
                        }
                        for ((index, newFaceMeshpoint) in faceMesh.allPoints.withIndex()) {
                            val newPosition = newFaceMeshpoint.position
                            canvas.drawCircle(newPosition.x, newPosition.y, 2f, p)
                            val oldPosition = previousMeshPoints?.get(index)
                            if (newPosition.delta(oldPosition) < attackLimitPixels) {
                                localCounter++
                            }
                        }
                        previousMeshPoints = faceMesh.allPoints
                    } else {
                        // Gets triangle info
                        val polygons: List<Triangle<FaceMeshPoint>> = faceMesh.allTriangles
                        if (previousPolygons == null) {
                            previousPolygons = faceMesh.allTriangles
                            STARTED_NOW = 1
                        }
                        for ((index, polygon) in polygons.withIndex()) {
                            val stWidth = p.strokeWidth
                            Log.e("width", stWidth.toString())
                            p.strokeWidth = 1F
                            val path = Path()
                            path.moveTo(
                                polygon.allPoints[0].position.x,
                                polygon.allPoints[0].position.y
                            )
                            path.lineTo(
                                polygon.allPoints[1].position.x,
                                polygon.allPoints[1].position.y
                            )
                            path.lineTo(
                                polygon.allPoints[2].position.x,
                                polygon.allPoints[2].position.y
                            )
                            path.lineTo(
                                polygon.allPoints[0].position.x,
                                polygon.allPoints[0].position.y
                            )
                            canvas.drawPath(path, p)

                        }
                    }
                    canvas.drawBitmap(bitmap, 0f, 0f, p)

                    if (localCounter >= 240) {
                        if (STARTED_NOW == 0) {
                            STARTED_NOW = 1
                        } else {
                            noMicroTickCounter++
                        }
                    }

                    if (predict) {
                        predictorCount++
                    }

                    val attSt = noMicroTickCounter * 0.4 + predictorCount * 0.6

                    frameCounter++



                    if (frameCounter == analyzeLimit && attSt < attackLimit) {
                        statusText = "No attack\nnoMTick - ${noMicroTickCounter}\n predCnt - ${predictorCount}\n attSt - ${attSt}"
                        Log.d("Attack_Status", "No Attack")
                        frameCounter = 0
                        noMicroTickCounter = 0
                        predictorCount = 0
                    } else if (frameCounter == analyzeLimit && attSt >= attackLimit) {
                        statusText = "Attack \nnoMTick - ${noMicroTickCounter}\npredCnt - ${predictorCount}\nattSt - ${attSt}"
                        Log.d("Attack_Status", "Attack!!!")
                        frameCounter = 0
                        noMicroTickCounter = 0
                        predictorCount = 0
                    }

                    resultListener.onResult(
                        FaceMeshProcessingResult(
                            bitmap,
                            statusText
                        )
                    )
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}



fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}