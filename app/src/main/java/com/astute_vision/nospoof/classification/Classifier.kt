package com.astute_vision.nospoof.classification

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.util.Arrays
import java.util.stream.IntStream
import kotlin.math.exp

class Classifier(modelPath: String?) {
    private val model: Module

    init {
        model = Module.load(modelPath)
    }

    // приведение размера картинки и конвертация ее в тензор
    //    public Tensor preprocess(Bitmap bitmap, int size){
    //        bitmap = Bitmap.createScaledBitmap(bitmap, size, size,false);
    //        return TensorImageUtils.bitmapToFloat32Tensor(bitmap, this.mean, this.std);
    //    }
    fun preprocess(bp: Bitmap): Tensor {
        // Здесь не совсем правильное преобразование!!
        var bitmap = bp
        var k = 0.0
        if (bitmap.width < bitmap.height) {
            k = 1.0 * IMG_SIZE / bitmap.width
            val newW = IMG_SIZE
            val newH = (k * bitmap.height).toInt()
            bitmap = Bitmap.createScaledBitmap(bitmap, newW, newH, false)
            bitmap = Bitmap.createBitmap(
                bitmap,
                0,
                (bitmap.height - IMG_SIZE) / 2,
                IMG_SIZE,
                IMG_SIZE
            )
        } else {
            k = 1.0 * IMG_SIZE / bitmap.height
            val newW = (k * bitmap.width).toInt()
            val newH = IMG_SIZE
            bitmap = Bitmap.createScaledBitmap(bitmap, newW, newH, false)
            bitmap = Bitmap.createBitmap(
                bitmap,
                bitmap.width - IMG_SIZE,
                0,
                IMG_SIZE,
                IMG_SIZE
            )
        }
        return TensorImageUtils.bitmapToFloat32Tensor(bitmap, mean, std)
    }

    // найти номер максимального элемента в массиве
    fun argMax(inputs: FloatArray): Int {
        var maxIndex = -1
        var maxvalue = 0.0f
        for (i in inputs.indices) {
            if (inputs[i] > maxvalue) {
                maxIndex = i
                maxvalue = inputs[i]
            }
        }
        return maxIndex
    }

    private fun softmax(input: Double, neuronValues: DoubleArray): Double {
        val total = Arrays.stream(neuronValues).map { a: Double ->
            exp(a)
        }.sum()
        return exp(input) / total
    }

    // использование НС
    fun predict(bitmap: Bitmap): DoubleArray {
        val tensor: Tensor = preprocess(bitmap)
        val inputs: IValue = IValue.from(tensor)
        val outputs: Tensor = model.forward(inputs).toTensor()
        val scoresFA: FloatArray = outputs.dataAsFloatArray
        val scoresDA = IntStream.range(0, scoresFA.size).mapToDouble { i: Int ->
            scoresFA[i].toDouble()
        }.toArray()
        val result = DoubleArray(scoresFA.size)
        for (i in scoresFA.indices) result[i] = softmax(scoresFA[i].toDouble(), scoresDA)
        return result
    }

    companion object {
        const val IMG_SIZE = 200
        val IMAGENET_CLASSES = arrayOf("NoAttack", "Attack")
        val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
        val std = floatArrayOf(0.229f, 0.224f, 0.225f)
    }
}