package com.astute_vision.nospoof.di

import android.content.Context
import com.astute_vision.nospoof.assetFilePath
import com.astute_vision.nospoof.classification.Classifier
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetector
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideClassifier(@ApplicationContext context: Context): Classifier =
        Classifier(assetFilePath(context, "model.pt"))

}