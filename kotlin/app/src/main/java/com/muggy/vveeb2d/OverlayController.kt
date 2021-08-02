package com.muggy.vveeb2d

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.*
import android.webkit.WebMessage
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.android.synthetic.main.overlay.view.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class OverlayController(  // declaring required variables
    private val context: Context
){
    private val mView: View
    private var mParams: WindowManager.LayoutParams? = null
    private val mWindowManager: WindowManager
    private val layoutInflater: LayoutInflater
    private val rendererUrl: String = "https://muggy8.github.io/VVeeb2D/"
    var windowWidth:Int = 400
    var windowHeight: Int = 300
    @SuppressLint("JavascriptInterface")
    fun open() {
        try {
            // check if the view is already
            // inflated or present in the window
            if (mView.windowToken == null) {
                if (mView.parent == null) {
                    mWindowManager.addView(mView, mParams)
                }
            }

            val faceTrackingOptions: FaceDetectorOptions = FaceDetectorOptions.Builder()
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setMinFaceSize(0.3F)
                .build()

            faceDetector = FaceDetection.getClient(faceTrackingOptions)

            cameraExecutor = Executors.newSingleThreadExecutor()

            WebView.setWebContentsDebuggingEnabled(true);

            mView.apply {
                webview.loadUrl(rendererUrl)
                webview.settings.apply {
                    javaScriptEnabled = true
                    setDomStorageEnabled(true)
                    setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)
                }
            }

            startCamera()
        } catch (e: Exception) {
            Log.d("Error1", e.toString())
        }
    }

    fun close() {
        try {
            customLifecycle.setLifecycleState(Lifecycle.State.DESTROYED)
            cameraExecutor.shutdown()
            // remove the view from the window
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(mView)
            // invalidate the view
            mView.invalidate()
            // remove all views
            (mView.parent as ViewGroup).removeAllViews()

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("Error2", e.toString())
        }
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = WindowManager.LayoutParams(
                windowWidth,
                windowHeight,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.OPAQUE,
            )
        }
        // getting a LayoutInflater
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // inflating the view with the custom layout we created
        mView = layoutInflater.inflate(R.layout.overlay, null)
        // set onClickListener on the remove button, which removes
        // the view from the window
        // Define the position of the
        // window within the screen
        mParams!!.gravity = Gravity.BOTTOM or Gravity.LEFT
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    }

    private class CustomLifecycle : LifecycleOwner {
        private val lifecycleRegistry: LifecycleRegistry

        init {
            lifecycleRegistry = LifecycleRegistry(this);
            lifecycleRegistry.markState(Lifecycle.State.CREATED)
            lifecycleRegistry.markState(Lifecycle.State.STARTED)
        }

        fun setLifecycleState(newState:Lifecycle.State){
            lifecycleRegistry.markState(newState)
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }
    fun resizeWindow(width: Int, height: Int){
        mParams?.width = width
        mParams?.height = height
        windowWidth = width
        windowHeight = height

        mWindowManager.updateViewLayout(mView, mParams)
    }

    fun refreshView(){
        mView.refreshDrawableState()
    }

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var faceDetector: FaceDetector
    private val customLifecycle:CustomLifecycle = CustomLifecycle()
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // setup our facetracker
            val faceAnalysis: ImageAnalysis = ImageAnalysis.Builder()
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
//                        mView.scanResults.text = resultsText
                        val payloadString:String = ("{"
                                + "\"ParamAngleX\":${ face.headEulerAngleX },"
                                + "\"ParamAngley\":${ face.headEulerAngleY },"
                                + "\"ParamAnglez\":${ face.headEulerAngleZ }"
                        + "}")

                        mView.webview.postWebMessage(
                            WebMessage("{\"type\":\"params\", \"payload\": ${payloadString}}"),
                            Uri.parse(rendererUrl)
                        )
                    }
                )
            )

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(mView.viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    customLifecycle,
                    cameraSelector,
                    faceAnalysis,
                    preview
                )

            } catch(exc: Exception) {
//                Log.e(MainActivity.TAG, "Use case binding failed", exc)
                println("Use case binding failed")
                println(exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }
}