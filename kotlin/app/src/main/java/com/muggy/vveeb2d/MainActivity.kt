package com.muggy.vveeb2d

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.FaceDetector
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ExecutorService

class MainActivity : AppCompatActivity() {

//    private lateinit var outputDirectory: File
//    private lateinit var cameraExecutor: ExecutorService

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val faceTrackingOptions:FaceDetectorOptions = FaceDetectorOptions.Builder()
//            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
//            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
//            .setMinFaceSize(0.3F)
//            .build()
//
//        faceDetector = FaceDetection.getClient(faceTrackingOptions)
//
//        webview.loadUrl("https://muggy8.github.io/VVeeb2D/")
//        webview.settings.apply {
//            javaScriptEnabled = true
//            setDomStorageEnabled(true)
//            setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }
//        jsBindings = JavascriptBindings(this)
//        webview.addJavascriptInterface(jsBindings, "appHost")

        // Request camera permissions
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            ActivityCompat.requestPermissions(
//                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
//        }

        // Set up the listener for take photo button
//        camera_capture_button.setOnClickListener { takePhoto() }

//        outputDirectory = getOutputDirectory()

        startOverlayButton.setOnClickListener { startOverlay() }

        stopOverlayButton.setOnClickListener { stopOverlay() }

//        cameraExecutor = Executors.newSingleThreadExecutor()
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults:
//        IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted()) {
//                startCamera()
//            } else {
//                Toast.makeText(this,
//                    "Permissions not granted by the user.",
//                    Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
//    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun getOverlayPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 12345)
            }
        }
    }

//    lateinit var wm:WindowManager
//    lateinit var overlayView: View
    private fun startOverlay(){
        if (!Settings.canDrawOverlays(this)){
            getOverlayPermission()
            return
        }
        if (!allPermissionsGranted()){
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            return
        }

        startService()
//        val mParams: WindowManager.LayoutParams? = WindowManager.LayoutParams(
//            200,
//            200,
//            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//            PixelFormat.OPAQUE)
//
//        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay, null)
//
//        wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        wm.addView(overlayView, mParams)
    }

    fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if (Settings.canDrawOverlays(this)) {
                // start the service based on the android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(Intent(this, ForegroundService::class.java))
                } else {
                    startService(Intent(this, ForegroundService::class.java))
                }
            }
        } else {startService(Intent(this, ForegroundService::class.java))
        }
    }

    private fun stopOverlay(){
//        wm.removeView(overlayView)
//        stopForeground()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
