package com.muggy.vveeb2d
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.util.Size
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.google.mediapipe.components.*
import com.google.mediapipe.framework.*
import com.google.mediapipe.glutil.EglManager
import kotlinx.android.synthetic.main.overlay.view.*
import java.util.*
import com.google.mediapipe.framework.PacketGetter
import com.google.mediapipe.formats.proto.MatrixDataProto.MatrixData;
import com.google.mediapipe.modules.facegeometry.FaceGeometryProto.FaceGeometry;
import android.webkit.WebMessage
import androidx.core.math.MathUtils.clamp
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.protobuf.InvalidProtocolBufferException
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


class OverlayController ( private val context: Context ) : LifecycleOwner {
    private val mView: View
    private var mParams: WindowManager.LayoutParams? = null
    private val mWindowManager: WindowManager
    private val layoutInflater: LayoutInflater
    private val rendererUrl: String = "https://muggy8.github.io/VVeeb2D/"
    var windowWidth: Int = 400
    var windowHeight: Int = 300
    private var mediapipeManager: MediapipeManager
    private var lifecycleRegistry: LifecycleRegistry

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

        mediapipeManager = MediapipeManager(context, this, mView, {data:MediapipeManager.PointsOfIntrest
            ->onFaceTracking(data)})
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    var latestLandmarks: String = ""
        get() {
            return mediapipeManager.latestLandmarks
        }

    fun open() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        try {
            // check if the view is already
            // inflated or present in the window
            if (mView.windowToken == null) {
                if (mView.parent == null) {
                    mWindowManager.addView(mView, mParams)
                }
            }

            WebView.setWebContentsDebuggingEnabled(true);

            mView.apply {
                webview.loadUrl(rendererUrl)
                webview.settings.apply {
                    javaScriptEnabled = true
                    setDomStorageEnabled(true)
                    setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)
                }
            }
        } catch (e: Exception) {
            Log.d("Error1", e.toString())
        }
        mediapipeManager.startTracking()
        println("started face tracking")
    }

    fun close() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        mediapipeManager.pause()
        try {
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

    private fun onFaceTracking(pointsOfIntrest: MediapipeManager.PointsOfIntrest){
        // a list of params live2D supported params
        //   'ParamAngleX',
        //   'ParamAngleY',
        //   'ParamAngleZ',
        //   'ParamEyeLOpen',
        //   'ParamEyeLSmile',
        //   'ParamEyeROpen',
        //   'ParamEyeRSmile',
        //   'ParamEyeBallX',
        //   'ParamEyeBallY',
        //   'ParamEyeBallForm',
        //   'ParamBrowLY',
        //   'ParamBrowRY',
        //   'ParamBrowLX',
        //   'ParamBrowRX',
        //   'ParamBrowLAngle',
        //   'ParamBrowRAngle',
        //   'ParamBrowLForm',
        //   'ParamBrowRForm',
        //   'ParamMouthForm',
        //   'ParamMouthOpenY',
        //   'ParamCheek',
        //   'ParamBodyAngleX',
        //   'ParamBodyAngleY',
        //   'ParamBodyAngleZ',
        //   'ParamBreath',
        //   'ParamArmLA',
        //   'ParamArmRA',
        //   'ParamArmLB',
        //   'ParamArmRB',
        //   'ParamHandL',
        //   'ParamHandR',
        //   'ParamHairFront',
        //   'ParamHairSide',
        //   'ParamHairBack',
        //   'ParamHairFluffy',
        //   'ParamShoulderY',
        //   'ParamBustX',
        //   'ParamBustY',
        //   'ParamBaseX',
        //   'ParamBaseY',


        mView.webview.post(Runnable {
            val angleX = Math.atan((pointsOfIntrest.noseTip.x/pointsOfIntrest.noseTip.z).toDouble())
            val angleY = Math.atan((pointsOfIntrest.noseTip.y/pointsOfIntrest.noseTip.z).toDouble())
            val angleZ = Math.atan((pointsOfIntrest.chin.x/pointsOfIntrest.chin.y).toDouble())
            val mouthDistanceY = pointsOfIntrest.lipTop.distanceFrom(pointsOfIntrest.lipBottom)

            val rightEyeDelta = pointsOfIntrest.rightEyelidTop.distanceFrom(pointsOfIntrest.rightEyelidBottom)
            val leftEyeDelta = pointsOfIntrest.leftEyelidTop.distanceFrom(pointsOfIntrest.leftEyelidBottom)

            println("leftEye: ${leftEyeDelta} | rightEye: ${rightEyeDelta}")

            val live2Dparams:String = ("{"
                + "\"ParamEyeROpen\":${ -1 + clamp(logisticBias((rightEyeDelta - 0.55) * 2), 0.0, 1.0) },"
                + "\"ParamEyeLOpen\":${ -1 + clamp(logisticBias((leftEyeDelta - 0.55) * 2), 0.0, 1.0) },"
//                + "\"ParamMouthForm\":${ clamp((mouthCenterOffset * -70) + 0.3f, -1.0f, 1.0f) },"
                + "\"ParamMouthOpenY\":${ clamp(logisticBias(mouthDistanceY / 6), 0.0f, 1.0f) },"
                + "\"ParamAngleX\":${ clamp(Math.toDegrees(angleX), -30.0, 30.0) },"
                + "\"ParamAngleY\":${ clamp(Math.toDegrees(angleY), -30.0, 30.0) },"
                + "\"ParamAngleZ\":${ clamp(Math.toDegrees(angleZ), -30.0, 30.0) }"
                + "}"
            )

            println(live2Dparams)

            mView.webview.postWebMessage(
                WebMessage("{\"type\":\"params\",\"payload\": ${live2Dparams}}"),
                Uri.parse(rendererUrl)
            )

        })


    }

    // public apis for ipc with the main activity
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
}

open class MediapipeManager (
    protected val context: Context,
    protected val lifecycleOwner: LifecycleOwner,
    protected val overlayView: View,
    protected val faceTrackingCallback: (data:PointsOfIntrest)->Unit ?,
){
    private val TAG = "OverlayController" // for logging

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

    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    protected lateinit var processor: FrameProcessor

    // Handles camera access via the {@link CameraX} Jetpack support library.
    protected lateinit var cameraHelper: CameraXPreviewHelper

    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
    private lateinit var previewFrameTexture: SurfaceTexture

    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
    private lateinit var previewDisplayView: SurfaceView

    // Creates and manages an {@link EGLContext}.
    private lateinit var eglManager: EglManager

    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    private lateinit var converter: ExternalTextureConverter

    protected val EYE_TRACKING_BINARY:String = "face_mesh_and_iris_mobile.binarypb"
    protected val INPUT_STREAM_NAME:String = "input_video"
    protected val OUTPUT_STREAM_NAME:String = "output_video"
    protected val FOCAL_LENGTH_STREAM_NAME:String = "focal_length_pixel"
    protected val OUTPUT_FACE_LANDMARKS_STREAM_NAME:String = "face_landmarks_with_iris"
    protected val OUTPUT_EYE_LANDMARKS_STREAM_NAME:String = "right_eye_contour_landmarks"
    protected val FLIP_FRAMES_VERTICALLY:Boolean = true
    protected val NUM_BUFFERS:Int = 2

    protected val OUTPUT_FACE_GEOMETRY_STREAM_NAME:String = "multi_face_geometry"
    protected val MATRIX_TRANSLATION_Z_INDEX = 14

    protected var root: String = Environment.getExternalStorageDirectory().toString()

    fun startTracking() {
        previewDisplayView = SurfaceView(context)
        setupPreviewDisplayView()

        // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
        // binary graphs.
        AndroidAssetUtil.initializeNativeAssetManager(context)
        eglManager = EglManager(null)
        processor = FrameProcessor(
            context,
            eglManager.nativeContext,
            EYE_TRACKING_BINARY,
            INPUT_STREAM_NAME,
            OUTPUT_STREAM_NAME
        )
        processor
            .videoSurfaceOutput
            .setFlipY(
                FLIP_FRAMES_VERTICALLY
            )

        cameraHelper = CameraXPreviewHelper()

//        processor.addPacketCallback(
//            OUTPUT_EYE_LANDMARKS_STREAM_NAME
//        ) { packet: Packet ->
//            val landmarksRaw = PacketGetter.getProtoBytes(packet)
//            try {
//                val landmarks =
//                    LandmarkProto.NormalizedLandmarkList.parseFrom(landmarksRaw)
//                if (landmarks == null) {
//                    Log.v(TAG, "[TS:" + packet.timestamp + "] No landmarks.")
//                    return@addPacketCallback
//                }
//
//
//                var saveDir = File("$root/VVeeb2D")
//                if (!saveDir.exists()) {
//                    saveDir.mkdirs();
//                }
//                val file = File(saveDir, "eye-landmarks.txt")
//                if (file.exists()){
//                    file.delete()
//                }
//                try{
//                    val outputStreamWriter = OutputStreamWriter(FileOutputStream(file), "UTF-8")
//                    outputStreamWriter.write(landmarks.toString())
//                    outputStreamWriter.flush()
//                    outputStreamWriter.close()
//                    latestLandmarks = landmarks.toString()
//                }
//                catch (e:Exception) {
//                    e.printStackTrace();
//                }
//
//            } catch (e: InvalidProtocolBufferException) {
//                Log.e(TAG, "Couldn't Exception received - $e")
//                return@addPacketCallback
//            }
//        }

        processor.addPacketCallback(
            OUTPUT_FACE_GEOMETRY_STREAM_NAME
        ) { packet: Packet ->

//            Log.d(TAG, "Received a multi face geometry packet.")
            val multiFaceGeometry: List<FaceGeometry> = PacketGetter.getProtoVector(packet, FaceGeometry.parser())
//            val approxDistanceAwayFromCameraLogMessage = StringBuilder()
//            for (faceGeometry in multiFaceGeometry) {
//                if (approxDistanceAwayFromCameraLogMessage.length > 0) {
//                    approxDistanceAwayFromCameraLogMessage.append(' ')
//                }
//                val poseTransformMatrix: MatrixData = faceGeometry.getPoseTransformMatrix()
//                approxDistanceAwayFromCameraLogMessage.append(
//                    -poseTransformMatrix.getPackedData(MATRIX_TRANSLATION_Z_INDEX)
//                )
//
//                println(getPoint(poseTransformMatrix, faceGeometry.mesh.vertexBufferList, POINT_NOSE_TIP))
//
//            }

            val faceGeometry = multiFaceGeometry.get(0)
            val poseTransformMatrix: MatrixData = faceGeometry.getPoseTransformMatrix()
            var rotationMatrix:List<Double> = getRotationMatrix(poseTransformMatrix)

            var json = "["
            for (i in 0..467) {
                val point = getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, i)
                var jsonBit = ""
                if (i > 0){
                    jsonBit += ","
                }
                jsonBit += "{\"x\":${point.x},\"y\":${point.y},\"z\":${point.z}}"
                json += jsonBit
            }
            json += "]"

            var saveDir = File("$root/VVeeb2D")
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            val file = File(saveDir, "landmarks-face-3d-6.json")
            if (file.exists()){
                file.delete()
            }
            try{
                val outputStreamWriter = OutputStreamWriter(FileOutputStream(file), "UTF-8")
                outputStreamWriter.write(json)
                outputStreamWriter.flush()
                outputStreamWriter.close()
            }
            catch (e:Exception) {
                e.printStackTrace();
            }

//            faceTrackingCallback(
//                PointsOfIntrest(
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_NOSE_TIP),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_NOSE_RIGHT),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_NOSE_LEFT),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_LIP_TOP),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_LIP_BOTTOM),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_MOUTH_LEFT),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_MOUTH_RIGHT),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_HEAD_TOP),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_CHIN),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_NOSE_BRIDGE_LEFT),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_NOSE_BRIDGE_RIGHT),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_NOSE_BRIDGE_CENTER),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_FACE_MEASURE_LEFT),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_FACE_MEASURE_RIGHT),
//
//
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_LEFT_EYE_LID_TOP),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_LEFT_EYE_LID_BOTTOM),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_LEFT_EYE_LID_INNER),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_LEFT_EYE_LID_OUTER),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_LID_TOP),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_LID_BOTTOM),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_LID_INNER),
//                    getPoint(rotationMatrix, faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_LID_OUTER),
//                )
//            )

//            Log.d(
//                TAG,
//                "[TS:"
//                        + packet.timestamp
//                        + "] size = "
//                        + multiFaceGeometry.size
//                        + "; approx. distance away from camera in cm for faces = ["
//                        + approxDistanceAwayFromCameraLogMessage
//                        + "]"
//            )
        }

        resume()


    }

    lateinit var latestLandmarks: String;

    protected fun getPoint(rotationMatrix: List<Double>, pointsBuffer: List<Float>, pointIndex: Int) : Vector3{
        var x = pointsBuffer[pointIndex * 5]
        var y = pointsBuffer[(pointIndex * 5) + 1]
        var z = pointsBuffer[(pointIndex * 5) + 2]

        // i have no idea what this sorcery does or how it works but my friend Jack did the galaxy brain math and came up with this and it works so err ya....
        x = ((rotationMatrix.get(0) * x) + (rotationMatrix.get(1) * y) + (rotationMatrix.get(2) * z)).toFloat()
        y = ((rotationMatrix.get(4) * x) + (rotationMatrix.get(5) * y) + (rotationMatrix.get(6) * z)).toFloat()
        z = ((rotationMatrix.get(8) * x) + (rotationMatrix.get(9) * y) + (rotationMatrix.get(10) * z)).toFloat()

        return Vector3(x, y, z)
    }

    // math from: https://math.stackexchange.com/questions/237369/given-this-transformation-matrix-how-do-i-decompose-it-into-translation-rotati
    protected fun getRotationMatrix(poseTransformMatrix: MatrixData):List<Double>{

        val a = poseTransformMatrix.getPackedData(0)
        val b = poseTransformMatrix.getPackedData(1)
        val c = poseTransformMatrix.getPackedData(2)
        val e = poseTransformMatrix.getPackedData(4)
        val f = poseTransformMatrix.getPackedData(5)
        val g = poseTransformMatrix.getPackedData(6)
        val i = poseTransformMatrix.getPackedData(8)
        val j = poseTransformMatrix.getPackedData(9)
        val k = poseTransformMatrix.getPackedData(10)

        val scaleX = Math.sqrt(Math.pow(a.toDouble(), 2.0) + Math.pow(e.toDouble(), 2.0) + Math.pow(i.toDouble(), 2.0))
        val scaleY = Math.sqrt(Math.pow(b.toDouble(), 2.0) + Math.pow(f.toDouble(), 2.0) + Math.pow(j.toDouble(), 2.0))
        val scaleZ = Math.sqrt(Math.pow(c.toDouble(), 2.0) + Math.pow(g.toDouble(), 2.0) + Math.pow(k.toDouble(), 2.0))

        val rotationMatrix: List<Double> = listOf(
            a/scaleX, b/scaleY, c/scaleZ, 0.0,
            e/scaleX, f/scaleY, g/scaleZ, 0.0,
            i/scaleX, j/scaleY, k/scaleZ, 0.0,
            0.0, 0.0, 0.0, 1.0,
        )

        println(rotationMatrix)

        return rotationMatrix
    }

    // points of intrest
    data class PointsOfIntrest(
        val noseTip: Vector3,
        val noseLeft: Vector3,
        val noseRight: Vector3,
        val lipTop: Vector3,
        val lipBottom: Vector3,
        val mouthLeft: Vector3,
        val mouthRight: Vector3,
        val headTop: Vector3,
        val chin: Vector3,
        val noseBridgeLeft: Vector3,
        val noseBridgeRight: Vector3,
        val noseBridgeCenter: Vector3,
        val faceMeasureLeft: Vector3,
        val faceMeasureRight: Vector3,
        val leftEyelidTop: Vector3,
        val leftEyelidBottom: Vector3,
        val leftEyelidInner: Vector3,
        val leftEyelidOuter: Vector3,
        val rightEyelidTop: Vector3,
        val rightEyelidBottom: Vector3,
        val rightEyelidInner: Vector3,
        val rightEyelidOuter: Vector3,
    )
    protected val POINT_NOSE_TIP:Int = 1
    protected val POINT_NOSE_RIGHT:Int = 36
    protected val POINT_NOSE_LEFT:Int = 266
    protected val POINT_LIP_TOP:Int = 12
    protected val POINT_LIP_BOTTOM:Int = 15
    protected val POINT_MOUTH_LEFT:Int = 292
    protected val POINT_MOUTH_RIGHT:Int = 62
    protected val POINT_HEAD_TOP:Int = 10
    protected val POINT_CHIN:Int = 175
    protected val POINT_NOSE_BRIDGE_LEFT = 362
    protected val POINT_NOSE_BRIDGE_RIGHT = 133
    protected val POINT_NOSE_BRIDGE_CENTER = 168
    protected val POINT_FACE_MEASURE_LEFT = 124
    protected val POINT_FACE_MEASURE_RIGHT = 352
    protected val POINT_IRIS_LEFT = 473
    protected val POINT_IRIS_RIGHT = 468
    protected val POINT_LEFT_EYE_LID_TOP = 386
    protected val POINT_LEFT_EYE_LID_BOTTOM = 374
    protected val POINT_LEFT_EYE_LID_INNER = 362
    protected val POINT_LEFT_EYE_LID_OUTER = 263
    protected val POINT_RIGHT_EYE_LID_TOP = 159
    protected val POINT_RIGHT_EYE_LID_BOTTOM = 145
    protected val POINT_RIGHT_EYE_LID_INNER = 133
    protected val POINT_RIGHT_EYE_LID_OUTER = 33
    protected val POINT_FACE_CENTER_FINDER_LEFT = 454
    protected val POINT_FACE_CENTER_FINDER_RIGHT = 234


    fun resume() {
        converter = ExternalTextureConverter(
            eglManager.context,
            NUM_BUFFERS
        )
        converter.setFlipY(
            FLIP_FRAMES_VERTICALLY
        )
        converter.setConsumer(processor)
        startCamera()
    }

    fun pause() {
        converter.close()
        previewDisplayView.setVisibility(View.GONE);
    }

    protected var haveAddedSidePackets:Boolean = false
    protected fun onCameraStarted(surfaceTexture: SurfaceTexture?) {
        if (surfaceTexture != null) {
            previewFrameTexture = surfaceTexture
            previewDisplayView.setVisibility(View.VISIBLE);
        }

        if (!haveAddedSidePackets) {
            val focalLength = cameraHelper.focalLengthPixels
            if (focalLength != Float.MIN_VALUE) {
                val focalLengthSidePacket = processor.packetCreator.createFloat32(focalLength)
                val inputSidePackets: MutableMap<String, Packet> = HashMap()
                inputSidePackets[FOCAL_LENGTH_STREAM_NAME] = focalLengthSidePacket
                processor.setInputSidePackets(inputSidePackets)
            }
            haveAddedSidePackets = true
        }
    }

    protected fun cameraTargetResolution(): Size? {
        return null // No preference and let the camera (helper) decide.
    }

    fun startCamera() {
        cameraHelper.setOnCameraStartedListener { surfaceTexture: SurfaceTexture? ->
            onCameraStarted(
                surfaceTexture
            )
        }
        val cameraFacing = CameraHelper.CameraFacing.FRONT
        cameraHelper.startCamera(
            context,
            lifecycleOwner,
            cameraFacing, /*unusedSurfaceTexture=*/
            cameraTargetResolution()
        );
    }

    protected fun computeViewSize(width: Int, height: Int): Size {
        return Size(width, height)
    }

    protected fun onPreviewDisplaySurfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int, height: Int
    ) {
        previewDisplayView.setVisibility(View.GONE);
        // (Re-)Compute the ideal size of the camera-preview display (the area that the
        // camera-preview frames get rendered onto, potentially with scaling and rotation)
        // based on the size of the SurfaceView that contains the display.
        val viewSize = computeViewSize(width, height)
        val displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize)
        val isCameraRotated = cameraHelper.isCameraRotated

        // Connect the converter to the camera-preview frames as its input (via
        // previewFrameTexture), and configure the output width and height as the computed
        // display size.
        if (this::previewFrameTexture.isInitialized) {
            converter.setSurfaceTextureAndAttachToGLContext(
                previewFrameTexture,
                if (isCameraRotated) displaySize.height else displaySize.width,
                if (isCameraRotated) displaySize.width else displaySize.height
            )
        }

    }

    private fun setupPreviewDisplayView() {

        val viewGroup = overlayView.findViewById<ViewGroup>(R.id.preview_display_layout)
        viewGroup.addView(previewDisplayView)
        previewDisplayView
            .getHolder()
            .addCallback(
                object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        processor.videoSurfaceOutput.setSurface(holder.surface)
                    }

                    override fun surfaceChanged(
                        holder: SurfaceHolder,
                        format: Int,
                        width: Int,
                        height: Int
                    ) {
                        onPreviewDisplaySurfaceChanged(holder, format, width, height)
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        processor.videoSurfaceOutput.setSurface(null)
                    }
                })
    }
}