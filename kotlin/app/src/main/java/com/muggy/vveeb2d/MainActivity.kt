package com.muggy.vveeb2d

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


typealias LumaListener = (luma: Double) -> Unit

class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null

//    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var faceDetector:FaceDetector
    private lateinit var jsBindings: JavascriptBindings

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val faceTrackingOptions:FaceDetectorOptions = FaceDetectorOptions.Builder()
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.3F)
            .build()

        faceDetector = FaceDetection.getClient(faceTrackingOptions)

        webview.loadUrl("https://muggy8.github.io/VVeeb2D/")
        webview.settings.apply {
            javaScriptEnabled = true
            setDomStorageEnabled(true)
            setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        jsBindings = JavascriptBindings(this)
        webview.addJavascriptInterface(jsBindings, "appHost")

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listener for take photo button
//        camera_capture_button.setOnClickListener { takePhoto() }

//        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

//    private fun takePhoto() {}

    private class FaceTrackingAnalyzer(
        private val faceDetector:FaceDetector,
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

    private class JavascriptBindings(private val context: Context) {
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

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // setup our facetracker
            val faceAnalysis:ImageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            faceAnalysis.setAnalyzer(
                cameraExecutor,
                FaceTrackingAnalyzer(
                    faceDetector,
                    { faces: List<Face> ->
                        val face: Face = faces[0]
                        val resultsText:String = (
                            "Face x: " + face.headEulerAngleX + "\n"
                            + "Face Y: " + face.headEulerAngleY + "\n"
                            + "Face Z: " + face.headEulerAngleZ + "\n"
                        )
                        scanResults.text = resultsText;
//                        println(resultsText)

                        jsBindings.emit("face-data", face)
                    }
                )
            )

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    faceAnalysis,
                    preview
                )

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

//    private fun getOutputDirectory(): File {
//        val mediaDir = externalMediaDirs.firstOrNull()?.let {
//            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
//        return if (mediaDir != null && mediaDir.exists())
//            mediaDir else filesDir
//    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
