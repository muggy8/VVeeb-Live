package com.muggy.vveeblive
import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Size
import android.view.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.google.mediapipe.components.*
import com.google.mediapipe.framework.*
import com.google.mediapipe.glutil.EglManager
import java.util.*
import com.google.mediapipe.framework.PacketGetter
import com.google.mediapipe.formats.proto.MatrixDataProto.MatrixData
import com.google.mediapipe.modules.facegeometry.FaceGeometryProto.FaceGeometry
import androidx.core.math.MathUtils.clamp
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.protobuf.InvalidProtocolBufferException
import kotlin.random.Random
import android.graphics.Color
import android.webkit.*
import kotlinx.android.synthetic.main.screen_overlay.view.*

class OverlayController : LifecycleOwner {
    private val screenOverlayUrl: String
    private var lifecycleRegistry: LifecycleRegistry
    private var serverPort:Int
    private lateinit var screenOverlayView: View
    private lateinit var mWindowManager: WindowManager
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var mediapipeManager: MediapipeManager
    private lateinit var context: Context

    private val display:Display get() {
//        return mWindowManager.defaultDisplay
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            context.display as Display
        } else {
            mWindowManager.defaultDisplay
        }
    }

    init {
        serverPort = Random.nextInt(5000, 50000)
        screenOverlayUrl = "http://127.0.0.1:${serverPort}/"

        // set onClickListener on the remove button, which removes
        // the view from the window
        // Define the position of the
        // window within the screen
//        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager


        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    fun setup(parentContext:Context){
        context = parentContext
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // getting a LayoutInflater
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // inflating the view with the custom layout we created
        screenOverlayView = layoutInflater.inflate(R.layout.screen_overlay, null)

        mediapipeManager = MediapipeManager(
            context,
            this,
            screenOverlayView,
            {data:MediapipeManager.PointsOfIntrest->onFaceTracking(data)},
            {data:MediapipeManager.PointsOfIntrest->onEyeTracking(data)},
            display,
        )

        screenOverlayParams = WindowManager.LayoutParams(
            display.width,
            display.height,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT,
        )

        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        try {
            server = OverlayHTTPServer(serverPort, context)
            server.start()
            // this chunk starts the face tracking.
        } catch (e: Exception) {
            Log.d("Error1", e.toString())
        }
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    private lateinit var server:OverlayHTTPServer

    private var basicOverlayStarted = false

    private lateinit var screenOverlayParams:WindowManager.LayoutParams
    private var messagePorts: Array<WebMessagePort> = arrayOf()
    private val rendererClient = RendererClient()

    fun startScreenOverlay(hasCameraPermission:Boolean){
        try {
            if (screenOverlayView.windowToken == null) {
                if (screenOverlayView.parent == null) {
                    mWindowManager.addView(screenOverlayView, screenOverlayParams)
                }
            }

            WebView.setWebContentsDebuggingEnabled(true)

            screenOverlayView.webview.setBackgroundColor(Color.TRANSPARENT)
            screenOverlayView.webview.settings.apply {
                javaScriptEnabled = true
                setDomStorageEnabled(true)
            }
            screenOverlayView.webview.apply {
                setWebViewClient(WebViewClient())
                setWebChromeClient(object : WebChromeClient() {
                    override fun onPermissionRequest(request: PermissionRequest) {
                        for (permission in request.resources) {
                            when (permission) {

                                "android.webkit.resource.AUDIO_CAPTURE" -> {
                                    (context as MainActivity).askForWebviewPermission(
                                        request,
                                        Manifest.permission.RECORD_AUDIO,
                                        Random.nextInt(9001)
                                    )
                                }

                                "android.webkit.resource.VIDEO_CAPTURE" -> {
                                    (context as MainActivity).askForWebviewPermission(
                                        request,
                                        Manifest.permission.CAMERA,
                                        Random.nextInt(9001)
                                    )
                                }


                            }
                        }
                    }
                })
                webViewClient = rendererClient
            }
            screenOverlayView.webview.loadUrl(screenOverlayUrl)
            basicOverlayStarted = true

            if (hasCameraPermission){
                mediapipeManager.startTracking()
            }
        } catch (e: Exception) {
            Log.d("Error1", e.toString())
        }
    }

    fun resizeScreenOverlay(){
        screenOverlayParams.width = display.width
        screenOverlayParams.height = display.height
        if (basicOverlayStarted){
            mWindowManager.updateViewLayout(screenOverlayView, screenOverlayParams)
        }
    }

    fun close() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED

        if (basicOverlayStarted){
            try {
                basicOverlayStarted = false
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(screenOverlayView)
                screenOverlayView.webview.destroy()
                // invalidate the view
                screenOverlayView.invalidate()
                // remove all views
                (screenOverlayView.parent as ViewGroup).removeAllViews()
            } catch (e: Exception) {
                Log.d("Error2", e.toString())
            }
        }
        server.stop()
    }

    private var faceLRDeg:Double = 0.0
    private var faceUDDeg:Double = 0.0
    private fun onEyeTracking(pointsOfIntrest: MediapipeManager.PointsOfIntrest){
        screenOverlayView.webview.post {

            // eye lid tracking is kinda ass right now but we're just gonna make the most of what we have at the moment and try again later with a better library
            val deltaEyelidsLeft = pointsOfIntrest.leftEyelidBottom.distanceFrom(pointsOfIntrest.leftEyelidTop)
            val deltaEyelidsRight = pointsOfIntrest.rightEyelidBottom.distanceFrom(pointsOfIntrest.rightEyelidTop)

            val eyeDistanceLeftConstantish = pointsOfIntrest.leftEyeMeasureA.distanceFrom(pointsOfIntrest.leftEyeMeasureB)
            val eyeDistanceRightConstantish = pointsOfIntrest.rightEyeMeasureA.distanceFrom(pointsOfIntrest.rightEyeMeasureB)

            val openessLeft = deltaEyelidsLeft/eyeDistanceLeftConstantish
            val openessRight = deltaEyelidsRight/eyeDistanceRightConstantish
            val eyeOpeness = maxOf(openessLeft, openessRight)

            // we're cheating here by locking both eyes to the same value but this might work ok in the long run
            val eyeOpenessNormalized = logisticBias(mapNumber(eyeOpeness, 1.4f, 1.7f, 0f, 1f))

//            println("openessLeft: ${openessLeft} | openessRight: ${openessRight} | eyeOpeness: ${eyeOpeness} | eyeOpenessNormalized ${eyeOpenessNormalized}")

            // eye gaze tracking
            val rightEyeDistanceFromCenterEdge =
                pointsOfIntrest.irisRight.distanceFrom(pointsOfIntrest.rightEyelidInner)
            val rightEyeDistanceFromOuterEdge = pointsOfIntrest.irisRight.distanceFrom(pointsOfIntrest.rightEyelidOuter)
            val rightEyeWidth = pointsOfIntrest.rightEyelidOuter.distanceFrom(pointsOfIntrest.rightEyelidInner)

            val leftEyeDistanceFromCenterEdge = pointsOfIntrest.irisLeft.distanceFrom(pointsOfIntrest.leftEyelidInner)
            val leftEyeDistanceFromOuterEdge = pointsOfIntrest.irisLeft.distanceFrom(pointsOfIntrest.leftEyelidOuter)
            val leftEyeWidth = pointsOfIntrest.leftEyelidOuter.distanceFrom(pointsOfIntrest.leftEyelidInner)

            val avgHorizontalEyeDistance = (rightEyeWidth + leftEyeWidth) / 2
            val avgFromRight = (rightEyeDistanceFromCenterEdge + leftEyeDistanceFromOuterEdge) / 2
            val avgFromLeft = (rightEyeDistanceFromOuterEdge + leftEyeDistanceFromCenterEdge) / 2

            val gazeLeftPercent = avgFromLeft / avgHorizontalEyeDistance
            val gazeRightPercent = avgFromRight / avgHorizontalEyeDistance

            // .65 = max one side .45 = min one side 0.55 = middle

            val gazedir = (((gazeLeftPercent - 0.55f) * 10) + ((gazeRightPercent - 0.55f) * -10)) / 2

//            println("gazeLeftPercent: ${gazeLeftPercent} | gazeRightPercent: ${gazeRightPercent} | gazedir: ${gazedir}")

            // for now, we skip the stuff to do with the eyeball up down cuz eye tracking suck rn
            val trackingParams: String = (
                "{"
                    + "\"EyeBallsX\":${clamp(gazedir, -1f, 1f)},"
                    + "\"EyeLidsOpen\":${ -1f + eyeOpenessNormalized }"
                + "}"
            )

            if (basicOverlayStarted){
                var message = WebMessage("{\"type\":\"tracking\",\"payload\": ${trackingParams}}")
                if (messagePorts.size > 0){
                    message = WebMessage("{\"type\":\"tracking\",\"payload\": ${trackingParams}}", messagePorts)

                }
                screenOverlayView.webview.postWebMessage(
                    message,
                    Uri.parse(screenOverlayUrl)
                )
            }
        }
    }

    private fun onFaceTracking(pointsOfIntrest: MediapipeManager.PointsOfIntrest){
        screenOverlayView.webview.post {
            val angleX =
                Math.atan((pointsOfIntrest.noseTipTransformed.x / pointsOfIntrest.noseTipTransformed.z).toDouble())
            val angleY =
                Math.atan((pointsOfIntrest.noseTipTransformed.y / pointsOfIntrest.noseTipTransformed.z).toDouble())
            val angleZ = Math.atan((pointsOfIntrest.chinTransformed.x / pointsOfIntrest.chinTransformed.y).toDouble())
            val mouthDistanceY = pointsOfIntrest.lipTop.distanceFrom(pointsOfIntrest.lipBottom)

            faceLRDeg = Math.toDegrees(angleX)
            faceUDDeg = Math.toDegrees(angleY)
            val faceZ = Math.toDegrees(angleZ)

            val mouthCenterY = pointsOfIntrest.lipTop.middlePointFrom(pointsOfIntrest.lipBottom)
            val mouthCenterX = pointsOfIntrest.mouthLeft.middlePointFrom(pointsOfIntrest.mouthRight)

            val trackingParams: String = (
                "{"
                    + "\"EyeLeftSmiling\":${clamp((mouthCenterX.y - mouthCenterY.y) + 0.2f, 0f, 1.0f)},"
                    + "\"EyeRightSmiling\":${clamp((mouthCenterX.y - mouthCenterY.y) + 0.2f, 0f, 1.0f)},"
                    + "\"MouthWidth\":${clamp((mouthCenterX.y - mouthCenterY.y) + 0.2f, -1.0f, 1.0f)},"
                    + "\"MouthOpeness\":${clamp(logisticBias(mouthDistanceY / 6), 0.0f, 1.0f)},"
                    + "\"BodyAngleYaw\":${clamp((faceLRDeg - clamp(faceLRDeg, -30.0, 30.0))/5, -10.0, 10.0)},"
                    + "\"BodyAnglePitch\":${clamp((faceUDDeg - clamp(faceUDDeg, -30.0, 30.0))/5, -10.0, 10.0)},"
                    + "\"BodyAngleRoll\":${clamp((faceZ - clamp(faceZ, -30.0, 30.0))/5, -10.0, 10.0)},"
                    + "\"HeadAngleYaw\":${clamp(faceLRDeg, -30.0, 30.0)},"
                    + "\"HeadAnglePitch\":${clamp(faceUDDeg, -30.0, 30.0)},"
                    + "\"HeadAngleRoll\":${clamp(faceZ, -30.0, 30.0)}"
                + "}"
            )

            if (basicOverlayStarted){
                var message = WebMessage("{\"type\":\"tracking\",\"payload\": ${trackingParams}}")
                if (messagePorts.size > 0){
                    message = WebMessage("{\"type\":\"tracking\",\"payload\": ${trackingParams}}", messagePorts)
                }

                screenOverlayView.webview.postWebMessage(
                    message,
                    Uri.parse(screenOverlayUrl)
                )
            }

        }


    }

    private class RendererClient : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            onFinishCallbacks.forEach { callback -> callback() }
        }

        private val onFinishCallbacks:MutableList<()->Unit> = mutableListOf()
        fun addOnFinishCallback(callback:(()->Unit)):()->Unit{
            onFinishCallbacks.add(callback)
            return {
                val index = onFinishCallbacks.indexOf(callback)
                onFinishCallbacks.removeAt(index)
            }
        }
    }

}

class MediapipeManager (
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val overlayView: View,
    private val faceTrackingCallback: (data:PointsOfIntrest)->Unit,
    private val eyeTrackingCallback: (data:PointsOfIntrest)->Unit,
    private val display:Display?,
){
    private val TAG = "OverlayController" // for logging
    private val objExtendedText:String

    init {
        // Load all native libraries needed by the app.
        System.loadLibrary("mediapipe_jni")
        try {
            System.loadLibrary("opencv_java3")
        } catch (e: UnsatisfiedLinkError) {
            // Some example apps (e.g. template matching) require OpenCV 4.
            System.loadLibrary("opencv_java4")
        }

        val file_name = "obj-base.txt"
        objExtendedText = context.assets.open(file_name).bufferedReader().use{
            it.readText()
        }
    }

    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    private lateinit var processor: FrameProcessor

    // Handles camera access via the {@link CameraX} Jetpack support library.
    private lateinit var cameraHelper: CameraXPreviewHelper

    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
    private lateinit var previewFrameTexture: SurfaceTexture

    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
    private lateinit var previewDisplayView: SurfaceView

    // Creates and manages an {@link EGLContext}.
    private lateinit var eglManager: EglManager

    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    private lateinit var converter: ExternalTextureConverter

    private val EYE_TRACKING_BINARY:String = "face_mesh_and_iris_mobile.binarypb"
    private val INPUT_STREAM_NAME:String = "input_video"
    private val OUTPUT_STREAM_NAME:String = "output_video"
    private val FOCAL_LENGTH_STREAM_NAME:String = "focal_length_pixel"
    private val OUTPUT_EYE_LANDMARKS_STREAM_NAME:String = "face_landmarks_with_iris"
    private val FLIP_FRAMES_VERTICALLY:Boolean = true
    private val NUM_BUFFERS:Int = 2

    private val OUTPUT_FACE_GEOMETRY_STREAM_NAME:String = "multi_face_geometry"

    private val rotate90degMatrixCLW:List<Double> = listOf(
        0.0, 1.0, 0.0,
        -1.0, 0.0, 0.0,
        0.0, 0.0, 1.0,
    )

    private val rotate90degMatrixCCLW:List<Double> = listOf(
        0.0, -1.0, 0.0,
        1.0, 0.0, 0.0,
        0.0, 0.0, 1.0,
    )

    private val rotate180degMatrix:List<Double> = listOf(
        -1.0, 0.0, 0.0,
        0.0, -1.0, 0.0,
        0.0, 0.0, 1.0,
    )

    private val identityMatrix:List<Double> = listOf(
        1.0, 0.0, 0.0,
        0.0, 1.0, 0.0,
        0.0, 0.0, 1.0,
    )

    private fun getScreenRotationCorrectionMatrixFromScreen(display:Display?):List<Double> {
        return when (display?.rotation){
            Surface.ROTATION_0 -> identityMatrix
            Surface.ROTATION_180 -> rotate180degMatrix
            Surface.ROTATION_90 -> rotate90degMatrixCCLW
            Surface.ROTATION_270 -> rotate90degMatrixCLW
            else -> identityMatrix
        }
    }

    private fun simpleMatrixMultiply(transformerMatrix:List<Double>, matrixToBeTransformed:List<Double>):List<Double>{
        // todo fix this math shinanigans
        val matrix1a = transformerMatrix[0]
        val matrix1b = transformerMatrix[1]
        val matrix1c = transformerMatrix[2]
        val matrix1d = transformerMatrix[3]
        val matrix1e = transformerMatrix[4]
        val matrix1f = transformerMatrix[5]
        val matrix1g = transformerMatrix[6]
        val matrix1h = transformerMatrix[7]
        val matrix1i = transformerMatrix[8]

        val matrix2a = matrixToBeTransformed[0]
        val matrix2b = matrixToBeTransformed[1]
        val matrix2c = matrixToBeTransformed[2]
        val matrix2d = matrixToBeTransformed[3]
        val matrix2e = matrixToBeTransformed[4]
        val matrix2f = matrixToBeTransformed[5]
        val matrix2g = matrixToBeTransformed[6]
        val matrix2h = matrixToBeTransformed[7]
        val matrix2i = matrixToBeTransformed[8]

        return listOf(
            (matrix1a * matrix2a) + (matrix1b * matrix2d) + (matrix1c * matrix2g), (matrix1a * matrix2b) + (matrix1b * matrix2e) + (matrix1c * matrix2h), (matrix1a * matrix2c) + (matrix1b * matrix2f) + (matrix1c * matrix2i),
            (matrix1d * matrix2a) + (matrix1e * matrix2d) + (matrix1f * matrix2g), (matrix1d * matrix2b) + (matrix1e * matrix2e) + (matrix1f * matrix2h), (matrix1d * matrix2c) + (matrix1e * matrix2f) + (matrix1f * matrix2i),
            (matrix1g * matrix2a) + (matrix1h * matrix2d) + (matrix1i * matrix2g), (matrix1g * matrix2b) + (matrix1h * matrix2e) + (matrix1i * matrix2h), (matrix1g * matrix2c) + (matrix1h * matrix2f) + (matrix1i * matrix2i),
        )
    }

    fun startTracking() {
        previewDisplayView = SurfaceView(context)

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

        processor.addPacketCallback(
            OUTPUT_EYE_LANDMARKS_STREAM_NAME
        ) { packet: Packet ->
            val landmarksRaw = PacketGetter.getProtoBytes(packet)
            try {
                val landmarks =
                    LandmarkProto.NormalizedLandmarkList.parseFrom(landmarksRaw)
                if (landmarks == null) {
                    Log.v(TAG, "[TS:" + packet.timestamp + "] No landmarks.")
                    return@addPacketCallback
                }

//                var objFile = ""
//                landmarks.landmarkList.forEach { landmark:LandmarkProto.NormalizedLandmark ->
//                    val updatedPoints = Vector3(landmark.x, landmark.y, landmark.z)
//                    objFile += "v ${updatedPoints.x} ${updatedPoints.y} ${updatedPoints.z}\n"
//                }
//                objFile += objExtendedText
//
//                var saveDir = File("$root/VVeeb2D")
//                if (!saveDir.exists()) {
//                    saveDir.mkdirs();
//                }
//                val file = File(saveDir, "landmarks.obj")
//                if (file.exists()){
//                    file.delete()
//                }
//
//                try{
//                    val outputStreamWriter = OutputStreamWriter(FileOutputStream(file), "UTF-8")
//                    outputStreamWriter.write(objFile)
//                    outputStreamWriter.flush()
//                    outputStreamWriter.close()
//                }
//                catch (e:Exception) {
//                    println("failed to write")
//                    e.printStackTrace();
//                }

                val pointsForEyeTracking = PointsOfIntrest(
                    getLandmark(landmarks, POINT_NOSE_TIP),
                    getLandmark(landmarks, POINT_NOSE_RIGHT),
                    getLandmark(landmarks, POINT_NOSE_LEFT),
                    getLandmark(landmarks, POINT_LIP_TOP),
                    getLandmark(landmarks, POINT_LIP_BOTTOM),
                    getLandmark(landmarks, POINT_MOUTH_LEFT),
                    getLandmark(landmarks, POINT_MOUTH_RIGHT),
                    getLandmark(landmarks, POINT_HEAD_TOP),
                    getLandmark(landmarks, POINT_CHIN),
                    getLandmark(landmarks, POINT_NOSE_BRIDGE_LEFT),
                    getLandmark(landmarks, POINT_NOSE_BRIDGE_RIGHT),
                    getLandmark(landmarks, POINT_NOSE_BRIDGE_CENTER),
                    getLandmark(landmarks, POINT_FACE_MEASURE_LEFT),
                    getLandmark(landmarks, POINT_FACE_MEASURE_RIGHT),

                    getLandmark(landmarks, POINT_LEFT_EYE_LID_TOP),
                    getLandmark(landmarks, POINT_LEFT_EYE_LID_BOTTOM),
                    getLandmark(landmarks, POINT_LEFT_EYE_LID_INNER),
                    getLandmark(landmarks, POINT_LEFT_EYE_LID_OUTER),
                    getLandmark(landmarks, POINT_LEFT_EYE_MEASURER_A),
                    getLandmark(landmarks, POINT_LEFT_EYE_MEASURER_B),
                    getLandmark(landmarks, POINT_RIGHT_EYE_LID_TOP),
                    getLandmark(landmarks, POINT_RIGHT_EYE_LID_BOTTOM),
                    getLandmark(landmarks, POINT_RIGHT_EYE_LID_INNER),
                    getLandmark(landmarks, POINT_RIGHT_EYE_LID_OUTER),
                    getLandmark(landmarks, POINT_RIGHT_EYE_MEASURER_A),
                    getLandmark(landmarks, POINT_RIGHT_EYE_MEASURER_B),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_A),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_B),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_C),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_D),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_E),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_F),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_G),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_H),
                    getScreenRotationCorrectionMatrixFromScreen(display),
                )
                pointsForEyeTracking.irisLeft = getLandmark(landmarks, POINT_IRIS_LEFT)
                pointsForEyeTracking.irisLeftTop = getLandmark(landmarks, POINT_IRIS_LEFT_TOP)
                pointsForEyeTracking.irisLeftBottom = getLandmark(landmarks, POINT_IRIS_LEFT_BOTTOM)
                pointsForEyeTracking.irisRight = getLandmark(landmarks, POINT_IRIS_RIGHT)
                pointsForEyeTracking.irisRightTop = getLandmark(landmarks, POINT_IRIS_RIGHT_TOP)
                pointsForEyeTracking.irisRightBottom = getLandmark(landmarks, POINT_IRIS_RIGHT_BOTTOM)

                eyeTrackingCallback(pointsForEyeTracking)

            } catch (e: InvalidProtocolBufferException) {
                Log.e(TAG, "Couldn't Exception received - $e")
                return@addPacketCallback
            }
        }

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
//                println(getPoint(faceGeometry.mesh.vertexBufferList, POINT_NOSE_TIP))
//
//            }

            val faceGeometry = multiFaceGeometry.get(0)
            val poseTransformMatrix: MatrixData = faceGeometry.getPoseTransformMatrix()
            val rotationMatrix:List<Double> = getRotationMatrix(poseTransformMatrix)

//            var json = "["
//            for (i in 0..467) {
//                val point = getPoint(faceGeometry.mesh.vertexBufferList, i)
//                var jsonBit = ""
//                if (i > 0){
//                    jsonBit += ","
//                }
//                jsonBit += "{\"x\":${point.x},\"y\":${point.y},\"z\":${point.z}}"
//                json += jsonBit
//            }
//            json += "]"
//
//            var saveDir = File("$root/VVeeb2D")
//            if (!saveDir.exists()) {
//                saveDir.mkdirs();
//            }
//            val file = File(saveDir, "landmarks-face-3d-9.json")
//            if (file.exists()){
//                file.delete()
//            }
//            try{
//                val outputStreamWriter = OutputStreamWriter(FileOutputStream(file), "UTF-8")
//                outputStreamWriter.write(json)
//                outputStreamWriter.flush()
//                outputStreamWriter.close()
//            }
//            catch (e:Exception) {
//                e.printStackTrace();
//            }

            faceTrackingCallback(
                PointsOfIntrest(
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_NOSE_TIP),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_NOSE_RIGHT),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_NOSE_LEFT),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_LIP_TOP),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_LIP_BOTTOM),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_MOUTH_LEFT),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_MOUTH_RIGHT),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_HEAD_TOP),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_CHIN),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_NOSE_BRIDGE_LEFT),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_NOSE_BRIDGE_RIGHT),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_NOSE_BRIDGE_CENTER),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_FACE_MEASURE_LEFT),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_FACE_MEASURE_RIGHT),


                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_LEFT_EYE_LID_TOP),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_LEFT_EYE_LID_BOTTOM),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_LEFT_EYE_LID_INNER),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_LEFT_EYE_LID_OUTER),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_LEFT_EYE_MEASURER_A),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_LEFT_EYE_MEASURER_B),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_LID_TOP),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_LID_BOTTOM),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_LID_INNER),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_LID_OUTER),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_MEASURER_A),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_MEASURER_B),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_A),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_B),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_C),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_D),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_E),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_F),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_G),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_H),
                    getScreenRotationCorrectionMatrixFromScreen(display),
                    rotationMatrix,
                )
            )

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

        setupPreviewDisplayView()
    }

    private fun getPoint(pointsBuffer: List<Float>, pointIndex: Int) : Vector3{
        val x = pointsBuffer[pointIndex * 5]
        val y = pointsBuffer[(pointIndex * 5) + 1]
        val z = pointsBuffer[(pointIndex * 5) + 2]

        return Vector3(x, y, z)
    }

    private fun getLandmark(landmarks: LandmarkProto.NormalizedLandmarkList, index: Int) : Vector3{
        val landmark = landmarks.getLandmark(index)
        return Vector3(
            landmark.x,
            landmark.y,
            landmark.z,
        )
    }

    // math from: https://math.stackexchange.com/questions/237369/given-this-transformation-matrix-how-do-i-decompose-it-into-translation-rotati
    private fun getRotationMatrix(poseTransformMatrix: MatrixData):List<Double>{

        val a = poseTransformMatrix.getPackedData(0)
        val b = poseTransformMatrix.getPackedData(4)
        val c = poseTransformMatrix.getPackedData(8)
        val e = poseTransformMatrix.getPackedData(1)
        val f = poseTransformMatrix.getPackedData(5)
        val g = poseTransformMatrix.getPackedData(9)
        val i = poseTransformMatrix.getPackedData(2)
        val j = poseTransformMatrix.getPackedData(6)
        val k = poseTransformMatrix.getPackedData(10)

        val scaleX = Math.sqrt(Math.pow(a.toDouble(), 2.0) + Math.pow(e.toDouble(), 2.0) + Math.pow(i.toDouble(), 2.0))
        val scaleY = Math.sqrt(Math.pow(b.toDouble(), 2.0) + Math.pow(f.toDouble(), 2.0) + Math.pow(j.toDouble(), 2.0))
        val scaleZ = Math.sqrt(Math.pow(c.toDouble(), 2.0) + Math.pow(g.toDouble(), 2.0) + Math.pow(k.toDouble(), 2.0))

        val rotationMatrix: List<Double> = listOf(
            a/scaleX, b/scaleY, c/scaleZ,
            e/scaleX, f/scaleY, g/scaleZ,
            i/scaleX, j/scaleY, k/scaleZ,
        )

        return rotationMatrix
    }

    private fun inverse3x3Matrix(matrix:List<Double>):List<Double> {
        // so i followed a website... i hope this works...
        // source https://www.mathsisfun.com/algebra/matrix-inverse-minors-cofactors-adjugate.html

        // get matrix values
        val a = matrix[0]
        val b = matrix[1]
        val c = matrix[2]

        val d = matrix[3]
        val e = matrix[4]
        val f = matrix[5]

        val g = matrix[6]
        val h = matrix[7]
        val i = matrix[8]

        // simple diagram
        // a , b , c
        // d , e , f
        // g , h , i

        // create Matrix of Minors and transform it into Matrix of Cofactors
        val MatrixOfMinorsCofactored: List<Double> = listOf(
            (e*i)-(h*f) , -((d*i)-(g*f)) , (d*h)-(g*e),
            -((b*i)-(h*c)), (a*i)-(g*c), -((a*h)-(g*b)),
            (b*f)-(e*c) , -((a*f)-(d*c)) , (a*e)-(d*b),
        )

        val adjugateMatrixOfMinorsCofactored: List<Double> = listOf(
            MatrixOfMinorsCofactored[0], MatrixOfMinorsCofactored[3], MatrixOfMinorsCofactored[6],
            MatrixOfMinorsCofactored[1], MatrixOfMinorsCofactored[4], MatrixOfMinorsCofactored[7],
            MatrixOfMinorsCofactored[2], MatrixOfMinorsCofactored[5], MatrixOfMinorsCofactored[8],
        )

        // get the determinant
        val determinant = (a * ((e*i)-(h*f))) - (b * ((d*i)-(g*f))) + (c * (d*h)-(g*e))
        val inverseDeterminant = 1/determinant

        return adjugateMatrixOfMinorsCofactored.map { numberAtPosition -> inverseDeterminant * numberAtPosition }
    }

    // points of intrest
    class PointsOfIntrest(
        private val noseTipRaw: Vector3,
        private val noseLeftRaw: Vector3,
        private val noseRightRaw: Vector3,
        private val lipTopRaw: Vector3,
        private val lipBottomRaw: Vector3,
        private val mouthLeftRaw: Vector3,
        private val mouthRightRaw: Vector3,
        private val headTopRaw: Vector3,
        private val chinRaw: Vector3,
        private val noseBridgeLeftRaw: Vector3,
        private val noseBridgeRightRaw: Vector3,
        private val noseBridgeCenterRaw: Vector3,
        private val faceMeasureLeftRaw: Vector3,
        private val faceMeasureRightRaw: Vector3,
        private val leftEyelidTopRaw: Vector3,
        private val leftEyelidBottomRaw: Vector3,
        private val leftEyelidInnerRaw: Vector3,
        private val leftEyelidOuterRaw: Vector3,
        private val leftEyeMeasureARaw: Vector3,
        private val leftEyeMeasureBRaw: Vector3,
        private val rightEyelidTopRaw: Vector3,
        private val rightEyelidBottomRaw: Vector3,
        private val rightEyelidInnerRaw: Vector3,
        private val rightEyelidOuterRaw: Vector3,
        private val rightEyeMeasureARaw: Vector3,
        private val rightEyeMeasureBRaw: Vector3,

        private val eyeAvarageARaw: Vector3,
        private val eyeAvarageBRaw: Vector3,
        private val eyeAvarageCRaw: Vector3,
        private val eyeAvarageDRaw: Vector3,
        private val eyeAvarageERaw: Vector3,
        private val eyeAvarageFRaw: Vector3,
        private val eyeAvarageGRaw: Vector3,
        private val eyeAvarageHRaw: Vector3,

        // rotation matrix is optional
        protected val screenCorrectionMatrix: List<Double>,
        protected val rotationMatrix: List<Double> = listOf(1.0,0.0,0.0,  0.0,1.0,0.0,  0.0,0.0,1.0)
    ){
        private var irisRightRaw:Vector3 = Vector3(0f,0f,0f)
        var irisRight: Vector3
            get() = transformPoint(irisRightRaw, screenCorrectionMatrix)
            set(value){
                irisRightRaw = value
            }

        private var irisRightTopRaw:Vector3 = Vector3(0f,0f,0f)
        var irisRightTop: Vector3
            get() = transformPoint(irisRightTopRaw, screenCorrectionMatrix)
            set(value){
                irisRightTopRaw = value
            }

        private var irisRightBottomRaw:Vector3 = Vector3(0f,0f,0f)
        var irisRightBottom: Vector3
            get() = transformPoint(irisRightBottomRaw, screenCorrectionMatrix)
            set(value){
                irisRightBottomRaw = value
            }

        private var irisLeftRaw:Vector3 = Vector3(0f,0f,0f)
        var irisLeft: Vector3
            get() = transformPoint(irisLeftRaw, screenCorrectionMatrix)
            set(value){
                irisLeftRaw = value
            }

        private var irisLeftTopRaw:Vector3 = Vector3(0f,0f,0f)
        var irisLeftTop: Vector3
            get() = transformPoint(irisLeftTopRaw, screenCorrectionMatrix)
            set(value){
                irisLeftTopRaw = value
            }

        private var irisLeftBottomRaw:Vector3 = Vector3(0f,0f,0f)
        var irisLeftBottom: Vector3
            get() = transformPoint(irisLeftBottomRaw, screenCorrectionMatrix)
            set(value){
                irisLeftBottomRaw = value
            }


        // no point in having any of these exist and doing math on them until they're actually needed since we might not even use them potentially
        val noseTip: Vector3 get() = transformPoint(noseTipRaw, screenCorrectionMatrix)
        val noseLeft: Vector3 get() = transformPoint(noseLeftRaw, screenCorrectionMatrix)
        val noseRight: Vector3 get() = transformPoint(noseRightRaw, screenCorrectionMatrix)
        val lipTop: Vector3 get() = transformPoint(lipTopRaw, screenCorrectionMatrix)
        val lipBottom: Vector3 get() = transformPoint(lipBottomRaw, screenCorrectionMatrix)
        val mouthLeft: Vector3 get() = transformPoint(mouthLeftRaw, screenCorrectionMatrix)
        val mouthRight: Vector3 get() = transformPoint(mouthRightRaw, screenCorrectionMatrix)
        val headTop: Vector3 get() = transformPoint(headTopRaw, screenCorrectionMatrix)
        val chin: Vector3 get() = transformPoint(chinRaw, screenCorrectionMatrix)
        val noseBridgeLeft: Vector3 get() = transformPoint(noseBridgeLeftRaw, screenCorrectionMatrix)
        val noseBridgeRight: Vector3 get() = transformPoint(noseBridgeRightRaw, screenCorrectionMatrix)
        val noseBridgeCenter: Vector3 get() = transformPoint(noseBridgeCenterRaw, screenCorrectionMatrix)
        val faceMeasureLeft: Vector3 get() = transformPoint(faceMeasureLeftRaw, screenCorrectionMatrix)
        val faceMeasureRight: Vector3 get() = transformPoint(faceMeasureRightRaw, screenCorrectionMatrix)
        val leftEyelidTop: Vector3 get() = transformPoint(leftEyelidTopRaw, screenCorrectionMatrix)
        val leftEyelidBottom: Vector3 get() = transformPoint(leftEyelidBottomRaw, screenCorrectionMatrix)
        val leftEyelidInner: Vector3 get() = transformPoint(leftEyelidInnerRaw, screenCorrectionMatrix)
        val leftEyelidOuter: Vector3 get() = transformPoint(leftEyelidOuterRaw, screenCorrectionMatrix)
        val leftEyeMeasureA: Vector3 get() = transformPoint(leftEyeMeasureARaw, screenCorrectionMatrix)
        val leftEyeMeasureB: Vector3 get() = transformPoint(leftEyeMeasureBRaw, screenCorrectionMatrix)
        val rightEyelidTop: Vector3 get() = transformPoint(rightEyelidTopRaw, screenCorrectionMatrix)
        val rightEyelidBottom: Vector3 get() = transformPoint(rightEyelidBottomRaw, screenCorrectionMatrix)
        val rightEyelidInner: Vector3 get() = transformPoint(rightEyelidInnerRaw, screenCorrectionMatrix)
        val rightEyelidOuter: Vector3 get() = transformPoint(rightEyelidOuterRaw, screenCorrectionMatrix)
        val rightEyeMeasureA: Vector3 get() = transformPoint(rightEyeMeasureARaw, screenCorrectionMatrix)
        val rightEyeMeasureB: Vector3 get() = transformPoint(rightEyeMeasureBRaw, screenCorrectionMatrix)
        val eyeAvarageA: Vector3 get() = transformPoint(eyeAvarageARaw, screenCorrectionMatrix)
        val eyeAvarageB: Vector3 get() = transformPoint(eyeAvarageBRaw, screenCorrectionMatrix)
        val eyeAvarageC: Vector3 get() = transformPoint(eyeAvarageCRaw, screenCorrectionMatrix)
        val eyeAvarageD: Vector3 get() = transformPoint(eyeAvarageDRaw, screenCorrectionMatrix)
        val eyeAvarageE: Vector3 get() = transformPoint(eyeAvarageERaw, screenCorrectionMatrix)
        val eyeAvarageF: Vector3 get() = transformPoint(eyeAvarageFRaw, screenCorrectionMatrix)
        val eyeAvarageG: Vector3 get() = transformPoint(eyeAvarageGRaw, screenCorrectionMatrix)
        val eyeAvarageH: Vector3 get() = transformPoint(eyeAvarageHRaw, screenCorrectionMatrix)

        val noseTipTransformed: Vector3
            get() = transformPoint(transformPoint(noseTipRaw, rotationMatrix), screenCorrectionMatrix)
        val noseLeftTransformed: Vector3
            get() = transformPoint(transformPoint(noseLeftRaw, rotationMatrix), screenCorrectionMatrix)
        val noseRightTransformed: Vector3
            get() = transformPoint(transformPoint(noseRightRaw, rotationMatrix), screenCorrectionMatrix)
        val lipTopTransformed: Vector3
            get() = transformPoint(transformPoint(lipTopRaw, rotationMatrix), screenCorrectionMatrix)
        val lipBottomTransformed: Vector3
            get() = transformPoint(transformPoint(lipBottomRaw, rotationMatrix), screenCorrectionMatrix)
        val mouthLeftTransformed: Vector3
            get() = transformPoint(transformPoint(mouthLeftRaw, rotationMatrix), screenCorrectionMatrix)
        val mouthRightTransformed: Vector3
            get() = transformPoint(transformPoint(mouthRightRaw, rotationMatrix), screenCorrectionMatrix)
        val headTopTransformed: Vector3
            get() = transformPoint(transformPoint(headTopRaw, rotationMatrix), screenCorrectionMatrix)
        val chinTransformed: Vector3
            get() = transformPoint(transformPoint(chinRaw, rotationMatrix), screenCorrectionMatrix)
        val noseBridgeLeftTransformed: Vector3
            get() = transformPoint(transformPoint(noseBridgeLeftRaw, rotationMatrix), screenCorrectionMatrix)
        val noseBridgeRightTransformed: Vector3
            get() = transformPoint(transformPoint(noseBridgeRightRaw, rotationMatrix), screenCorrectionMatrix)
        val noseBridgeCenterTransformed: Vector3
            get() = transformPoint(transformPoint(noseBridgeCenterRaw, rotationMatrix), screenCorrectionMatrix)
        val faceMeasureLeftTransformed: Vector3
            get() = transformPoint(transformPoint(faceMeasureLeftRaw, rotationMatrix), screenCorrectionMatrix)
        val faceMeasureRightTransformed: Vector3
            get() = transformPoint(transformPoint(faceMeasureRightRaw, rotationMatrix), screenCorrectionMatrix)
        val leftEyelidTopTransformed: Vector3
            get() = transformPoint(transformPoint(leftEyelidTopRaw, rotationMatrix), screenCorrectionMatrix)
        val leftEyelidBottomTransformed: Vector3
            get() = transformPoint(transformPoint(leftEyelidBottomRaw, rotationMatrix), screenCorrectionMatrix)
        val leftEyelidInnerTransformed: Vector3
            get() = transformPoint(transformPoint(leftEyelidInnerRaw, rotationMatrix), screenCorrectionMatrix)
        val leftEyelidOuterTransformed: Vector3
            get() = transformPoint(transformPoint(leftEyelidOuterRaw, rotationMatrix), screenCorrectionMatrix)
        val leftEyeMeasureATransformed: Vector3
            get() = transformPoint(transformPoint(leftEyeMeasureARaw, rotationMatrix), screenCorrectionMatrix)
        val leftEyeMeasureBTransformed: Vector3
            get() = transformPoint(transformPoint(leftEyeMeasureBRaw, rotationMatrix), screenCorrectionMatrix)
        val rightEyelidTopTransformed: Vector3
            get() = transformPoint(transformPoint(rightEyelidTopRaw, rotationMatrix), screenCorrectionMatrix)
        val rightEyelidBottomTransformed: Vector3
            get() = transformPoint(transformPoint(rightEyelidBottomRaw, rotationMatrix), screenCorrectionMatrix)
        val rightEyelidInnerTransformed: Vector3
            get() = transformPoint(transformPoint(rightEyelidInnerRaw, rotationMatrix), screenCorrectionMatrix)
        val rightEyelidOuterTransformed: Vector3
            get() = transformPoint(transformPoint(rightEyelidOuterRaw, rotationMatrix), screenCorrectionMatrix)
        val rightEyeMeasureATransformed: Vector3
            get() = transformPoint(transformPoint(rightEyeMeasureARaw, rotationMatrix), screenCorrectionMatrix)
        val rightEyeMeasureBTransformed: Vector3
            get() = transformPoint(transformPoint(rightEyeMeasureBRaw, rotationMatrix), screenCorrectionMatrix)
        val eyeAvarageATransformed: Vector3
            get() = transformPoint(transformPoint(eyeAvarageARaw, rotationMatrix), screenCorrectionMatrix)
        val eyeAvarageBTransformed: Vector3
            get() = transformPoint(transformPoint(eyeAvarageBRaw, rotationMatrix), screenCorrectionMatrix)
        val eyeAvarageCTransformed: Vector3
            get() = transformPoint(transformPoint(eyeAvarageCRaw, rotationMatrix), screenCorrectionMatrix)
        val eyeAvarageDTransformed: Vector3
            get() = transformPoint(transformPoint(eyeAvarageDRaw, rotationMatrix), screenCorrectionMatrix)
        val eyeAvarageETransformed: Vector3
            get() = transformPoint(transformPoint(eyeAvarageERaw, rotationMatrix), screenCorrectionMatrix)
        val eyeAvarageFTransformed: Vector3
            get() = transformPoint(transformPoint(eyeAvarageFRaw, rotationMatrix), screenCorrectionMatrix)
        val eyeAvarageGTransformed: Vector3
            get() = transformPoint(transformPoint(eyeAvarageGRaw, rotationMatrix), screenCorrectionMatrix)
        val eyeAvarageHTransformed: Vector3
            get() = transformPoint(transformPoint(eyeAvarageHRaw, rotationMatrix), screenCorrectionMatrix)

        companion object {
            fun transformPoint(point:Vector3, transformMatrix: List<Double>):Vector3{
                var x = point.x
                var y = point.y
                var z = point.z
                return Vector3(
                    ((transformMatrix.get(0) * x) + (transformMatrix.get(1) * y) + (transformMatrix.get(2) * z)).toFloat(),
                    ((transformMatrix.get(3) * x) + (transformMatrix.get(4) * y) + (transformMatrix.get(5) * z)).toFloat(),
                    ((transformMatrix.get(6) * x) + (transformMatrix.get(7) * y) + (transformMatrix.get(8) * z)).toFloat(),
                )
            }
        }
    }

    private val POINT_NOSE_TIP:Int = 1
    private val POINT_NOSE_RIGHT:Int = 36
    private val POINT_NOSE_LEFT:Int = 266
    private val POINT_LIP_TOP:Int = 12
    private val POINT_LIP_BOTTOM:Int = 15
    private val POINT_MOUTH_LEFT:Int = 292
    private val POINT_MOUTH_RIGHT:Int = 62
    private val POINT_HEAD_TOP:Int = 10
    private val POINT_CHIN:Int = 175
    private val POINT_NOSE_BRIDGE_LEFT = 362
    private val POINT_NOSE_BRIDGE_RIGHT = 133
    private val POINT_NOSE_BRIDGE_CENTER = 168
    private val POINT_FACE_MEASURE_LEFT = 124
    private val POINT_FACE_MEASURE_RIGHT = 352
    private val POINT_IRIS_LEFT = 473
    private val POINT_IRIS_LEFT_TOP = 475
    private val POINT_IRIS_LEFT_BOTTOM = 477
    private val POINT_IRIS_RIGHT = 468
    private val POINT_IRIS_RIGHT_TOP = 470
    private val POINT_IRIS_RIGHT_BOTTOM = 472
    private val POINT_LEFT_EYE_LID_TOP = 386
    private val POINT_LEFT_EYE_LID_BOTTOM = 374
    private val POINT_LEFT_EYE_LID_INNER = 362
    private val POINT_LEFT_EYE_LID_OUTER = 263
    private val POINT_LEFT_EYE_MEASURER_A = 253
    private val POINT_LEFT_EYE_MEASURER_B = 450

    private val POINT_RIGHT_EYE_LID_TOP = 159
    private val POINT_RIGHT_EYE_LID_BOTTOM = 145
    private val POINT_RIGHT_EYE_LID_INNER = 133
    private val POINT_RIGHT_EYE_LID_OUTER = 33
    private val POINT_RIGHT_EYE_MEASURER_A = 23
    private val POINT_RIGHT_EYE_MEASURER_B = 230

    private val POINT_EYE_DISTANCE_AVARAGE_A = 197
    private val POINT_EYE_DISTANCE_AVARAGE_B = 195
    private val POINT_EYE_DISTANCE_AVARAGE_C = 18
    private val POINT_EYE_DISTANCE_AVARAGE_D = 200
    private val POINT_EYE_DISTANCE_AVARAGE_E = 5
    private val POINT_EYE_DISTANCE_AVARAGE_F = 195
    private val POINT_EYE_DISTANCE_AVARAGE_G = 9
    private val POINT_EYE_DISTANCE_AVARAGE_H = 8


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
        previewDisplayView.setVisibility(View.GONE)
    }

    private var haveAddedSidePackets:Boolean = false
    private fun onCameraStarted(surfaceTexture: SurfaceTexture?) {
        if (surfaceTexture != null) {
            previewFrameTexture = surfaceTexture
            previewDisplayView.setVisibility(View.VISIBLE)
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

    private fun cameraTargetResolution(): Size? {
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
        )
    }

    private fun computeViewSize(width: Int, height: Int): Size {
        return Size(width, height)
    }

    private fun onPreviewDisplaySurfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int, height: Int
    ) {
        previewDisplayView.setVisibility(View.GONE)
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