package com.example.mediapipe_test

import android.R
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ViewGroup.LayoutParams
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.components.*
import com.google.mediapipe.components.CameraHelper.CameraFacing
import com.google.mediapipe.formats.proto.MatrixDataProto.MatrixData
import com.google.mediapipe.framework.AndroidAssetUtil
import com.google.mediapipe.framework.Packet
import com.google.mediapipe.framework.PacketGetter
import com.google.mediapipe.glutil.EglManager
import com.google.mediapipe.modules.facegeometry.FaceGeometryProto.FaceGeometry
import java.util.List


/** Main activity of MediaPipe basic app.  */
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"

        // Flips the camera-preview frames vertically by default, before sending them into FrameProcessor
        // to be processed in a MediaPipe graph, and flips the processed frames back when they are
        // displayed. This maybe needed because OpenGL represents images assuming the image origin is at
        // the bottom-left corner, whereas MediaPipe in general assumes the image origin is at the
        // top-left corner.
        // NOTE: use "flipFramesVertically" in manifest metadata to override this behavior.
        private const val FLIP_FRAMES_VERTICALLY = true

        // Number of output frames allocated in ExternalTextureConverter.
        // NOTE: use "converterNumBuffers" in manifest metadata to override number of buffers. For
        // example, when there is a FlowLimiterCalculator in the graph, number of buffers should be at
        // least `max_in_flight + max_in_queue + 1` (where max_in_flight and max_in_queue are used in
        // FlowLimiterCalculator options). That's because we need buffers for all the frames that are in
        // flight/queue plus one for the next frame from the camera.
        private const val NUM_BUFFERS = 2

        init {
            // Load all native libraries needed by the app.
            System.loadLibrary("mediapipe_jni")
            try {
                System.loadLibrary("opencv_java3")
            } catch (e: UnsatisfiedLinkError) {
                // Some example apps (e.g. template matching) require OpenCV 4.
                System.loadLibrary("opencv_java4")
            }
        }
    }

    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    protected var processor: FrameProcessor? = null

    // Handles camera access via the {@link CameraX} Jetpack support library.
    protected var cameraHelper: CameraXPreviewHelper? = null

    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
    private var previewFrameTexture: SurfaceTexture? = null

    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
    private var previewDisplayView: SurfaceView? = null

    // Creates and manages an {@link EGLContext}.
    private var eglManager: EglManager? = null

    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    private var converter: ExternalTextureConverter? = null

    // ApplicationInfo for retrieving metadata defined in the manifest.
    private var applicationInfo: ApplicationInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentViewLayoutResId)
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Cannot find application info: $e")
        }
        previewDisplayView = SurfaceView(this)
        setupPreviewDisplayView()

        // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
        // binary graphs.
        AndroidAssetUtil.initializeNativeAssetManager(this)
        eglManager = EglManager(null)
        processor = FrameProcessor(
            this,
            eglManager!!.nativeContext,
            applicationInfo!!.metaData.getString("binaryGraphName"),
            applicationInfo!!.metaData.getString("inputVideoStreamName"),
            applicationInfo!!.metaData.getString("outputVideoStreamName")
        )
        processor!!
            .videoSurfaceOutput
            .setFlipY(
                applicationInfo!!.metaData.getBoolean("flipFramesVertically", FLIP_FRAMES_VERTICALLY)
            )
        PermissionHelper.checkAndRequestCameraPermissions(this)

        setupTracking()
    }

    private val TAG = "MainActivity"
    private val USE_FACE_DETECTION_INPUT_SOURCE_INPUT_SIDE_PACKET_NAME = "use_face_detection_input_source"
    private val SELECTED_EFFECT_ID_INPUT_STREAM_NAME = "selected_effect_id"
    private val OUTPUT_FACE_GEOMETRY_STREAM_NAME = "multi_face_geometry"

    private val EFFECT_SWITCHING_HINT_TEXT = "Tap to switch between effects!"

    private val USE_FACE_DETECTION_INPUT_SOURCE = false
    private val MATRIX_TRANSLATION_Z_INDEX = 14

    private val SELECTED_EFFECT_ID_AXIS = 0
    private val SELECTED_EFFECT_ID_FACEPAINT = 1
    private val SELECTED_EFFECT_ID_GLASSES = 2

    private val effectSelectionLock = Any()
    private var selectedEffectId = 0

    private var effectSwitchingHintView: View? = null
    private var tapGestureDetector: GestureDetector? = null
    protected fun setupTracking() {

        // Add an effect switching hint view to the preview layout.
        effectSwitchingHintView = createEffectSwitchingHintView()
        effectSwitchingHintView.setVisibility(View.INVISIBLE)
        val viewGroup = findViewById<ViewGroup>(R.id.preview_display_layout)
        viewGroup.addView(effectSwitchingHintView)

        // By default, render the axis effect for the face detection input source and the glasses effect
        // for the face landmark input source.
        if (USE_FACE_DETECTION_INPUT_SOURCE) {
            selectedEffectId = SELECTED_EFFECT_ID_AXIS
        } else {
            selectedEffectId = SELECTED_EFFECT_ID_GLASSES
        }

        // Pass the USE_FACE_DETECTION_INPUT_SOURCE flag value as an input side packet into the graph.
        val inputSidePackets: MutableMap<String, Packet> = HashMap()
        inputSidePackets[USE_FACE_DETECTION_INPUT_SOURCE_INPUT_SIDE_PACKET_NAME] =
            processor!!.packetCreator.createBool(USE_FACE_DETECTION_INPUT_SOURCE)
        processor!!.setInputSidePackets(inputSidePackets)

        // This callback demonstrates how the output face geometry packet can be obtained and used
        // in an Android app. As an example, the Z-translation component of the face pose transform
        // matrix is logged for each face being equal to the approximate distance away from the camera
        // in centimeters.
        processor!!.addPacketCallback(
            OUTPUT_FACE_GEOMETRY_STREAM_NAME
        ) { packet: Packet ->
            effectSwitchingHintView.post {
                effectSwitchingHintView.setVisibility(
                    if (USE_FACE_DETECTION_INPUT_SOURCE) View.INVISIBLE else View.VISIBLE
                )
            }
            Log.d(TAG, "Received a multi face geometry packet.")
            val multiFaceGeometry: List<FaceGeometry> =
                PacketGetter.getProtoVector(packet, FaceGeometry.parser())
            val approxDistanceAwayFromCameraLogMessage = StringBuilder()
            for (faceGeometry in multiFaceGeometry) {
                if (approxDistanceAwayFromCameraLogMessage.length > 0) {
                    approxDistanceAwayFromCameraLogMessage.append(' ')
                }
                val poseTransformMatrix: MatrixData = faceGeometry.getPoseTransformMatrix()
                approxDistanceAwayFromCameraLogMessage.append(
                    -poseTransformMatrix.getPackedData(MATRIX_TRANSLATION_Z_INDEX)
                )
            }
            Log.d(
                TAG,
                "[TS:"
                        + packet.timestamp
                        + "] size = "
                        + multiFaceGeometry.size
                        + "; approx. distance away from camera in cm for faces = ["
                        + approxDistanceAwayFromCameraLogMessage
                        + "]"
            )
        }

        // Alongside the input camera frame, we also send the `selected_effect_id` int32 packet to
        // indicate which effect should be rendered on this frame.
        processor!!.setOnWillAddFrameListener { timestamp: Long ->
            var selectedEffectIdPacket: Packet? = null
            try {
                synchronized(effectSelectionLock) {
                    selectedEffectIdPacket = processor!!.packetCreator.createInt32(selectedEffectId)
                }
                processor
                    .getGraph()
                    .addPacketToInputStream(
                        SELECTED_EFFECT_ID_INPUT_STREAM_NAME, selectedEffectIdPacket, timestamp
                    )
            } catch (e: RuntimeException) {
                Log.e(
                    TAG, "Exception while adding packet to input stream while switching effects: $e"
                )
            } finally {
                if (selectedEffectIdPacket != null) {
                    selectedEffectIdPacket.release()
                }
            }
        }

        // We use the tap gesture detector to switch between face effects. This allows users to try
        // multiple pre-bundled face effects without a need to recompile the app.
        tapGestureDetector = GestureDetector(
            this,
            object : SimpleOnGestureListener() {
                override fun onLongPress(event: MotionEvent) {
                    switchEffect()
                }

                override fun onSingleTapUp(event: MotionEvent): Boolean {
                    switchEffect()
                    return true
                }

                private fun switchEffect() {
                    // Avoid switching the Axis effect for the face detection input source.
                    if (USE_FACE_DETECTION_INPUT_SOURCE) {
                        return
                    }

                    // Looped effect order: glasses -> facepaint -> axis -> glasses -> ...
                    synchronized(effectSelectionLock) {
                        when (selectedEffectId) {
                            SELECTED_EFFECT_ID_AXIS -> {
                                selectedEffectId = SELECTED_EFFECT_ID_GLASSES
                            }
                            SELECTED_EFFECT_ID_FACEPAINT -> {
                                selectedEffectId = SELECTED_EFFECT_ID_AXIS
                            }
                            SELECTED_EFFECT_ID_GLASSES -> {
                                selectedEffectId = SELECTED_EFFECT_ID_FACEPAINT
                            }
                            else -> {
                            }
                        }
                    }
                }
            })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return tapGestureDetector!!.onTouchEvent(event)
    }

    private fun createEffectSwitchingHintView(): View? {
        val effectSwitchingHintView = TextView(applicationContext)
        effectSwitchingHintView.layoutParams =
            RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)
        effectSwitchingHintView.text = EFFECT_SWITCHING_HINT_TEXT
        effectSwitchingHintView.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        effectSwitchingHintView.setPadding(0, 0, 0, 480)
        effectSwitchingHintView.setTextColor(Color.parseColor("#ffffff"))
        effectSwitchingHintView.textSize = 24f
        return effectSwitchingHintView
    }

    // Used to obtain the content view for this application. If you are extending this class, and
    // have a custom layout, override this method and return the custom layout.
    protected val contentViewLayoutResId: Int
        protected get() = R.layout.activity_main

    override fun onResume() {
        super.onResume()
        converter = ExternalTextureConverter(
            eglManager!!.context,
            applicationInfo!!.metaData.getInt("converterNumBuffers", NUM_BUFFERS)
        )
        converter!!.setFlipY(
            applicationInfo!!.metaData.getBoolean("flipFramesVertically", FLIP_FRAMES_VERTICALLY)
        )
        converter!!.setConsumer(processor)
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        converter!!.close()

        // Hide preview display until we re-open the camera again.
        previewDisplayView!!.visibility = View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    protected fun onCameraStarted(surfaceTexture: SurfaceTexture?) {
        previewFrameTexture = surfaceTexture
        // Make the display view visible to start showing the preview. This triggers the
        // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
        previewDisplayView!!.visibility = View.VISIBLE
    }

    protected fun cameraTargetResolution(): Size? {
        return null // No preference and let the camera (helper) decide.
    }

    fun startCamera() {
        cameraHelper = CameraXPreviewHelper()
        previewFrameTexture = converter!!.surfaceTexture
        cameraHelper!!.setOnCameraStartedListener { surfaceTexture: SurfaceTexture? -> onCameraStarted(surfaceTexture) }
        val cameraFacing = if (applicationInfo!!.metaData.getBoolean(
                "cameraFacingFront",
                false
            )
        ) CameraFacing.FRONT else CameraFacing.BACK
        cameraHelper!!.startCamera(
            this, cameraFacing, previewFrameTexture, cameraTargetResolution()
        )
    }

    protected fun computeViewSize(width: Int, height: Int): Size {
        return Size(width, height)
    }

    protected fun onPreviewDisplaySurfaceChanged(
        holder: SurfaceHolder?, format: Int, width: Int, height: Int
    ) {
        // (Re-)Compute the ideal size of the camera-preview display (the area that the
        // camera-preview frames get rendered onto, potentially with scaling and rotation)
        // based on the size of the SurfaceView that contains the display.
        val viewSize = computeViewSize(width, height)
        val displaySize = cameraHelper!!.computeDisplaySizeFromViewSize(viewSize)
        val isCameraRotated = cameraHelper!!.isCameraRotated

        // Configure the output width and height as the computed display size.
        converter!!.setDestinationSize(
            if (isCameraRotated) displaySize.height else displaySize.width,
            if (isCameraRotated) displaySize.width else displaySize.height
        )
    }

    private fun setupPreviewDisplayView() {
        previewDisplayView!!.visibility = View.GONE
        val viewGroup = findViewById<ViewGroup>(R.id.preview_display_layout)
        viewGroup.addView(previewDisplayView)
        previewDisplayView
            .getHolder()
            .addCallback(
                object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        processor!!.videoSurfaceOutput.setSurface(holder.surface)
                    }

                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                        onPreviewDisplaySurfaceChanged(holder, format, width, height)
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        processor!!.videoSurfaceOutput.setSurface(null)
                    }
                })
    }
}
