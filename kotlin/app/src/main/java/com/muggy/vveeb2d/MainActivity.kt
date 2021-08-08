package com.muggy.vveeb2d

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Size
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.components.CameraHelper.CameraFacing
import com.google.mediapipe.components.CameraHelper.OnCameraStartedListener
import com.google.mediapipe.components.CameraXPreviewHelper
import com.google.mediapipe.components.ExternalTextureConverter
import com.google.mediapipe.components.FrameProcessor
import com.google.mediapipe.components.PermissionHelper
import com.google.mediapipe.framework.AndroidAssetUtil
import com.google.mediapipe.glutil.EglManager


class MainActivity : AppCompatActivity() {

    // copy-paste from mediapipe's arr example repo
    private val TAG: String? = "MainActivity"

    private val BINARY_GRAPH_NAME = "face_detection_mobile_gpu.binarypb"
    private val INPUT_VIDEO_STREAM_NAME = "input_video"
    private val OUTPUT_VIDEO_STREAM_NAME = "output_video"
    private val CAMERA_FACING = CameraFacing.FRONT

    // Flips the camera-preview frames vertically before sending them into FrameProcessor to be
    // processed in a MediaPipe graph, and flips the processed frames back when they are displayed.
    // This is needed because OpenGL represents images assuming the image origin is at the bottom-left
    // corner, whereas MediaPipe in general assumes the image origin is at top-left.
    private val FLIP_FRAMES_VERTICALLY = true

    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
    private var previewFrameTexture: SurfaceTexture? = null

    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
    private var previewDisplayView: SurfaceView? = null

    // Creates and manages an {@link EGLContext}.
    private var eglManager: EglManager? = null

    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    private var processor: FrameProcessor? = null

    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    private var converter: ExternalTextureConverter? = null

    // Handles camera access via the {@link CameraX} Jetpack support library.
    private var cameraHelper: CameraXPreviewHelper? = null
    // copy-paste over

    init {
        System.loadLibrary("mediapipe_jni");
        System.loadLibrary("opencv_java3");
    }

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewDisplayView = SurfaceView(this)
        setupPreviewDisplayView()

        // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
        // binary graphs.

        // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
        // binary graphs.
        AndroidAssetUtil.initializeNativeAssetManager(this)

        eglManager = EglManager(null)
        processor = FrameProcessor(
            this,
            eglManager!!.getNativeContext(),
            BINARY_GRAPH_NAME,
            INPUT_VIDEO_STREAM_NAME,
            OUTPUT_VIDEO_STREAM_NAME
        )
        processor!!.getVideoSurfaceOutput().setFlipY(FLIP_FRAMES_VERTICALLY)

        PermissionHelper.checkAndRequestCameraPermissions(this)

//        startOverlayButton.setOnClickListener { startOverlay() }
//
//        stopOverlayButton.setOnClickListener { stopOverlay() }
//
//        toggleRenderer.setOnClickListener { toggleOverlayRenderer() }
//
//        setViewStateToOverlaying()
//
//        overlayWidth.setText("400")
//        overlayWidth.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable) {
//                if (::overlayService.isInitialized){
//                    try{
//                        overlayService.overlay.windowWidth = s.toString().toInt()
//                        overlayService.overlay.resizeWindow(
//                            overlayService.overlay.windowWidth,
//                            overlayService.overlay.windowHeight,
//                        )
//                    }
//                    catch (ouf: Error){
//                        // whatever
//                    }
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
//        })
//
//        overlayHeight.setText("300")
//        overlayHeight.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable) {
//                if (::overlayService.isInitialized){
//                    try{
//                        overlayService.overlay.windowHeight = s.toString().toInt()
//                        overlayService.overlay.resizeWindow(
//                            overlayService.overlay.windowWidth,
//                            overlayService.overlay.windowHeight,
//                        )
//                    }
//                    catch (ouf: Error){
//                        // whatever
//                    }
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
//        })
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOverlay()
    }

    override fun onResume() {
        super.onResume()
        converter = ExternalTextureConverter(eglManager!!.context)
        converter!!.setFlipY(FLIP_FRAMES_VERTICALLY)
        converter!!.setConsumer(processor)
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        converter!!.close()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults!!)
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getOverlayPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 12345)
            }
        }
    }

    private fun setupPreviewDisplayView() {
        previewDisplayView!!.visibility = View.GONE
        val viewGroup:ViewGroup = findViewById(R.id.preview_display_layout)
        viewGroup.addView(previewDisplayView)
        previewDisplayView!!
            .getHolder()
            .addCallback(
                object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        processor!!.videoSurfaceOutput.setSurface(holder.surface)
                    }

                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                        // (Re-)Compute the ideal size of the camera-preview display (the area that the
                        // camera-preview frames get rendered onto, potentially with scaling and rotation)
                        // based on the size of the SurfaceView that contains the display.
                        val viewSize = Size(width, height)
                        val displaySize: Size = cameraHelper!!.computeDisplaySizeFromViewSize(viewSize)

                        // Connect the converter to the camera-preview frames as its input (via
                        // previewFrameTexture), and configure the output width and height as the computed
                        // display size.
                        converter!!.setSurfaceTextureAndAttachToGLContext(
                            previewFrameTexture, displaySize.getWidth(), displaySize.getHeight()
                        )
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        processor!!.videoSurfaceOutput.setSurface(null)
                    }
                })
    }

    private lateinit var overlayService: ForegroundService
    private var mBound: Boolean = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as ForegroundService.LocalBinder
            overlayService = binder.service
            mBound = true
//            overlayService.overlay.resizeWindow(
//                overlayWidth.text.toString().toInt(),
//                overlayHeight.text.toString().toInt(),
//            )
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private fun startCamera() {
        cameraHelper = CameraXPreviewHelper()
        cameraHelper!!.setOnCameraStartedListener(
            OnCameraStartedListener { surfaceTexture: SurfaceTexture ? ->
                previewFrameTexture = surfaceTexture
                // Make the display view visible to start showing the preview. This triggers the
                // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
                previewDisplayView!!.visibility = View.VISIBLE
            })
        cameraHelper!!.startCamera(this, CAMERA_FACING,  /*surfaceTexture=*/null)
    }


    private fun setViewStateToNotOverlaying(){
//        toggleRenderer.setVisibility(View.VISIBLE)
//        stopOverlayButton.setVisibility(View.VISIBLE)
//        startOverlayButton.setVisibility(View.GONE)
    }
    private fun setViewStateToOverlaying(){
//        toggleRenderer.setVisibility(View.GONE)
//        stopOverlayButton.setVisibility(View.GONE)
//        startOverlayButton.setVisibility(View.VISIBLE)
    }

    private fun startOverlay(){
        if (!CameraPermissionHelper.hasCameraPermission(this)){
            CameraPermissionHelper.requestCameraPermission(this)
            return
        }
        if (!Settings.canDrawOverlays(this)){
            getOverlayPermission()
            return
        }

        setViewStateToNotOverlaying()
        startService()
    }

    lateinit private var foregroundServiceIntent : Intent
    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if (Settings.canDrawOverlays(this)) {
                // start the service based on the android version
                foregroundServiceIntent = Intent(this, ForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(foregroundServiceIntent)
                } else {
                    startService(foregroundServiceIntent)
                }
            }
        } else {
            startService(foregroundServiceIntent)
        }
        bindService(foregroundServiceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun stopOverlay(){
        if (::foregroundServiceIntent.isInitialized){
            unbindService(connection)
            stopService(foregroundServiceIntent)
        }
        setViewStateToOverlaying()
    }

    private fun toggleOverlayRenderer(){
        overlayService.overlay.toggleShowLive2DModel()
    }
}