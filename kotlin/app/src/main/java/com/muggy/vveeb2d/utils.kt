package com.muggy.vveeb2d

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector

class JavascriptBindings(private val context: Context) {
    private var listeners: HashMap<String, MutableList<(Any)-> Any>> = HashMap<String, MutableList<(Any)-> Any>>()

    fun on(event:String, callback:(Any)-> Any): () -> Boolean? {
        if (listeners[event] == null){
            listeners[event] = mutableListOf()
            listeners[event]?.add(callback)
        }
        else{
            listeners[event]?.add(callback)
        }

        return { listeners[event]?.remove(callback) }
    }

    fun emit(event: String, data: Any){
        listeners[event]?.forEach({ callback -> callback(data) })
    }
}

class FaceTrackingAnalyzer(
    private val faceDetector: FaceDetector,
    val onFaceFound : (List<Face>) -> Unit,
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Pass image to an ML Kit Vision API
            val result = faceDetector.process(image)
                .addOnSuccessListener({ faces ->
                    // Task completed successfully
                    if (faces.size > 0){
                        onFaceFound(faces)
                    }
                })
                .addOnFailureListener({ e ->
                    // Task failed with an exception
                })

            result.addOnCompleteListener({ results -> imageProxy.close() });
        }
    }
}