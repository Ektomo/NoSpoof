package com.astute_vision.nospoof

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

fun assetFilePath(context: Context, assetName: String): String? {
    val file = File(context.filesDir, assetName)
    try {
        val `is` = context.assets.open(assetName)
        try {
            val os: OutputStream = FileOutputStream(file)
            val buffer = ByteArray(4 * 1024)
            var read: Int
            while (`is`.read(buffer).also { read = it } != -1) {
                os.write(buffer, 0, read)
            }
            os.flush()
        } catch (e: Exception) {
            Log.e("pytorchandroid", "Error process asset 1 $assetName to file path")
        }
        return file.absolutePath
    } catch (e: IOException) {
        Log.e("pytorchandroid", "Error process asset 2$assetName to file path")
    }
    return null
}