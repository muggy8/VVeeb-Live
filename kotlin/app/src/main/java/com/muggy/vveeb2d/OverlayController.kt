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
import kotlin.random.Random


class OverlayController ( private val context: Context ) : LifecycleOwner {
    private val mView: View
    private var mParams: WindowManager.LayoutParams? = null
    private val mWindowManager: WindowManager
    private val layoutInflater: LayoutInflater
    private val rendererUrl: String
    var windowWidth: Int = 400
    var windowHeight: Int = 300
    private var mediapipeManager: MediapipeManager
    private var lifecycleRegistry: LifecycleRegistry
    private var serverPort:Int

    init {
        serverPort = Random.nextInt(5000, 50000)
        rendererUrl = "http://127.0.0.1:${serverPort}"
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

        mediapipeManager = MediapipeManager(
            context,
            this,
            mView,
            {data:MediapipeManager.PointsOfIntrest->onFaceTracking(data)},
            {data:MediapipeManager.PointsOfIntrest->onEyeTracking(data)},
        )
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    private lateinit var server:RendererServer

    fun open() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        try {

            server = RendererServer(serverPort, context)
            server.start()

            // check if the view is already
            // inflated or present in the window
            if (mView.windowToken == null) {
                if (mView.parent == null) {
                    mWindowManager.addView(mView, mParams)
                }
            }

            WebView.setWebContentsDebuggingEnabled(true);

            println("started web contents startup")
            mView.webview.loadUrl(rendererUrl)
            mView.webview.settings.apply {
                javaScriptEnabled = true
                setDomStorageEnabled(true)
//                    setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)
            }
            println("finished web contents startup?")

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
        server.stop()
    }

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

    private var faceLRDeg:Double = 0.0
    private var faceUDDeg:Double = 0.0
    private fun onEyeTracking(pointsOfIntrest: MediapipeManager.PointsOfIntrest){
        mView.webview.post({

            // eye lid tracking is kinda ass right now but we're just gonna make the most of what we have at the moment and try again later with a better library
            val deltaEyelidsLeft = pointsOfIntrest.leftEyelidBottom.distanceFrom(pointsOfIntrest.leftEyelidTop)
            val deltaEyelidsRight = pointsOfIntrest.rightEyelidBottom.distanceFrom(pointsOfIntrest.rightEyelidTop)
            val eyeOpenMeasurement = (
                pointsOfIntrest.eyeAvarageA.distanceFrom(pointsOfIntrest.eyeAvarageB) +
                pointsOfIntrest.eyeAvarageC.distanceFrom(pointsOfIntrest.eyeAvarageD) +
                pointsOfIntrest.eyeAvarageE.distanceFrom(pointsOfIntrest.eyeAvarageF) +
                pointsOfIntrest.eyeAvarageG.distanceFrom(pointsOfIntrest.eyeAvarageH)
            ) / 4

            var leftEyeOpen = deltaEyelidsLeft / (eyeOpenMeasurement)
            var rightEyeOpen = deltaEyelidsRight / (eyeOpenMeasurement)
            leftEyeOpen = mapNumber(leftEyeOpen, 0.75f, 0.64f, 0f, 1f)
            leftEyeOpen = logisticBias(leftEyeOpen)
            rightEyeOpen = mapNumber(rightEyeOpen, 0.75f, 0.64f, 0f, 1f)
            rightEyeOpen = logisticBias(rightEyeOpen)

            // ratio: 0.77 or higher = open | 0.66 or lower = closed
//            println("faceLRDeg: ${faceLRDeg} | leftEyeOpen: ${leftEyeOpen} | rightEyeOpen: ${rightEyeOpen}")

            // rigth now we cheat cuz eye tracking on mediapipe is ass v.v
            if (faceLRDeg < -15){
                rightEyeOpen = leftEyeOpen
            }
            if (faceLRDeg > 15){
                leftEyeOpen = rightEyeOpen
            }

            // eye gaze tracking
            val rightEyeDistanceFromCenterEdge = pointsOfIntrest.irisRight.distanceFrom(pointsOfIntrest.rightEyelidInner)
            val rightEyeDistanceFromOuterEdge = pointsOfIntrest.irisRight.distanceFrom(pointsOfIntrest.rightEyelidOuter)
            val rightEyeWidth = pointsOfIntrest.rightEyelidOuter.distanceFrom(pointsOfIntrest.rightEyelidInner)

            val leftEyeDistanceFromCenterEdge = pointsOfIntrest.irisLeft.distanceFrom(pointsOfIntrest.leftEyelidInner)
            val leftEyeDistanceFromOuterEdge = pointsOfIntrest.irisLeft.distanceFrom(pointsOfIntrest.leftEyelidOuter)
            val leftEyeWidth = pointsOfIntrest.leftEyelidOuter.distanceFrom(pointsOfIntrest.leftEyelidInner)

            val avgHorizontalEyeDistance = (rightEyeWidth + leftEyeWidth) / 2
            val avgFromRight = (rightEyeDistanceFromCenterEdge + leftEyeDistanceFromOuterEdge) / 2
            val avgFromLeft = (rightEyeDistanceFromOuterEdge + leftEyeDistanceFromCenterEdge) / 2

            val gazeLeftPercent = avgFromLeft/avgHorizontalEyeDistance
            val gazeRightPercent = avgFromRight/avgHorizontalEyeDistance

            // .65 = max one side .45 = min one side 0.55 = middle

            val gazedir = (((gazeLeftPercent - 0.55f) * 10) + ((gazeRightPercent - 0.55f) * -10)) / 2

//            println("gazeLeftPercent: ${gazeLeftPercent} | gazeRightPercent: ${gazeRightPercent} | gazedir: ${gazedir}")

            // for now, we skip the stuff to do with the eyeball up down cuz eye tracking suck rn
            val live2Dparams:String = (
                "{"
                + "\"ParamEyeBallX\":${ clamp(gazedir, -1f, 1f) },"
                + "\"ParamEyeLOpen\":${ 0f - leftEyeOpen },"
                + "\"ParamEyeROpen\":${ 0f - rightEyeOpen }"
                + "}"
            )

            mView.webview.postWebMessage(
                WebMessage("{\"type\":\"params\",\"payload\": ${live2Dparams}}"),
                Uri.parse(rendererUrl)
            )
        })
    }

    private fun onFaceTracking(pointsOfIntrest: MediapipeManager.PointsOfIntrest){
        mView.webview.post({
            val angleX = Math.atan((pointsOfIntrest.noseTipTransformed.x/pointsOfIntrest.noseTipTransformed.z).toDouble())
            val angleY = Math.atan((pointsOfIntrest.noseTipTransformed.y/pointsOfIntrest.noseTipTransformed.z).toDouble())
            val angleZ = Math.atan((pointsOfIntrest.chinTransformed.x/pointsOfIntrest.chinTransformed.y).toDouble())
            val mouthDistanceY = pointsOfIntrest.lipTop.distanceFrom(pointsOfIntrest.lipBottom)

            faceLRDeg = Math.toDegrees(angleX)
            faceUDDeg = Math.toDegrees(angleY)

            val mouthCenterY = pointsOfIntrest.lipTop.middlePointFrom(pointsOfIntrest.lipBottom)
            val mouthCenterX = pointsOfIntrest.mouthLeft.middlePointFrom(pointsOfIntrest.mouthRight)

            val live2Dparams:String = (
                "{"
                + "\"ParamEyeLSmile\":${ clamp((mouthCenterX.y - mouthCenterY.y) + 0.2f, 0f, 1.0f) },"
                + "\"ParamEyeRSmile\":${ clamp((mouthCenterX.y - mouthCenterY.y) + 0.2f, 0f, 1.0f) },"
                + "\"ParamMouthForm\":${ clamp((mouthCenterX.y - mouthCenterY.y) + 0.2f, -1.0f, 1.0f) },"
                + "\"ParamMouthOpenY\":${ clamp(logisticBias(mouthDistanceY / 6), 0.0f, 1.0f) },"
                + "\"ParamAngleX\":${ clamp(Math.toDegrees(angleX), -30.0, 30.0) },"
                + "\"ParamAngleY\":${ clamp(Math.toDegrees(angleY), -30.0, 30.0) },"
                + "\"ParamAngleZ\":${ clamp(Math.toDegrees(angleZ), -30.0, 30.0) }"
                + "}"
            )

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
    protected val faceTrackingCallback: (data:PointsOfIntrest)->Unit,
    protected val eyeTrackingCallback: (data:PointsOfIntrest)->Unit,
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
    protected val OUTPUT_EYE_LANDMARKS_STREAM_NAME:String = "face_landmarks_with_iris"
    protected val FLIP_FRAMES_VERTICALLY:Boolean = true
    protected val NUM_BUFFERS:Int = 2

    protected val OUTPUT_FACE_GEOMETRY_STREAM_NAME:String = "multi_face_geometry"

    protected var root: String = Environment.getExternalStorageDirectory().toString()

    // initialize as identity matrix cuz we dont want to deal with it right now
    var inverseTransformationMatrix:List<Double> = listOf(
        1.0, 0.0, 0.0,
        0.0, 1.0, 0.0,
        0.0, 0.0, 1.0,
    )

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

//                var json = "["
//                landmarks.landmarkList.forEach { landmark:LandmarkProto.NormalizedLandmark ->
//                    if (json.length > 1){
//                        json += ", "
//                    }
//                    val updatedPoints = PointsOfIntrest.transformPoint(Vector3(landmark.x, landmark.y, landmark.z), inverseTransformationMatrix)
//
//                    json += "{\"x\":${updatedPoints.x},\"y\":${updatedPoints.y},\"z\":${updatedPoints.z}}"
//                }
//                json += "]"
//
//                var saveDir = File("$root/VVeeb2D")
//                if (!saveDir.exists()) {
//                    saveDir.mkdirs();
//                }
//                val file = File(saveDir, "landmarks-eyes-4.json")
//                if (file.exists()){
//                    file.delete()
//                }
//
//                try{
//                    val outputStreamWriter = OutputStreamWriter(FileOutputStream(file), "UTF-8")
//                    outputStreamWriter.write(json)
//                    outputStreamWriter.flush()
//                    outputStreamWriter.close()
//                }
//                catch (e:Exception) {
//                    println("failed to write")
//                    e.printStackTrace();
//                }


                var pointsForEyeTracking = PointsOfIntrest(
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
                    getLandmark(landmarks, POINT_RIGHT_EYE_LID_TOP),
                    getLandmark(landmarks, POINT_RIGHT_EYE_LID_BOTTOM),
                    getLandmark(landmarks, POINT_RIGHT_EYE_LID_INNER),
                    getLandmark(landmarks, POINT_RIGHT_EYE_LID_OUTER),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_A),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_B),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_C),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_D),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_E),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_F),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_G),
                    getLandmark(landmarks, POINT_EYE_DISTANCE_AVARAGE_H),
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
            var rotationMatrix:List<Double> = getRotationMatrix(poseTransformMatrix)

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
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_LID_TOP),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_LID_BOTTOM),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_LID_INNER),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_RIGHT_EYE_LID_OUTER),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_A),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_B),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_C),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_D),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_E),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_F),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_G),
                    getPoint(faceGeometry.mesh.vertexBufferList, POINT_EYE_DISTANCE_AVARAGE_H),
                    rotationMatrix,
                )
            )

            inverseTransformationMatrix = inverse3x3Matrix(listOf(
                poseTransformMatrix.getPackedData(0), poseTransformMatrix.getPackedData(4), poseTransformMatrix.getPackedData(8),
                poseTransformMatrix.getPackedData(1), poseTransformMatrix.getPackedData(5), poseTransformMatrix.getPackedData(9),
                poseTransformMatrix.getPackedData(2), poseTransformMatrix.getPackedData(6), poseTransformMatrix.getPackedData(10)
            ).map {number->number.toDouble()})

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

    protected fun getPoint(pointsBuffer: List<Float>, pointIndex: Int) : Vector3{
        var x = pointsBuffer[pointIndex * 5]
        var y = pointsBuffer[(pointIndex * 5) + 1]
        var z = pointsBuffer[(pointIndex * 5) + 2]

        return Vector3(x, y, z)
    }

    protected fun getLandmark(landmarks: LandmarkProto.NormalizedLandmarkList, index: Int) : Vector3{
        val landmark = landmarks.getLandmark(index)
        return Vector3(
            landmark.x,
            landmark.y,
            landmark.z,
        )
    }

    // math from: https://math.stackexchange.com/questions/237369/given-this-transformation-matrix-how-do-i-decompose-it-into-translation-rotati
    protected fun getRotationMatrix(poseTransformMatrix: MatrixData):List<Double>{

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

    protected fun inverse3x3Matrix(matrix:List<Double>):List<Double> {
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
        val eyeAvarageA: Vector3,
        val eyeAvarageB: Vector3,
        val eyeAvarageC: Vector3,
        val eyeAvarageD: Vector3,
        val eyeAvarageE: Vector3,
        val eyeAvarageF: Vector3,
        val eyeAvarageG: Vector3,
        val eyeAvarageH: Vector3,
        // rotation matrix is optional
        protected val rotationMatrix: List<Double> = listOf(1.0,0.0,0.0,  0.0,1.0,0.0,  0.0,0.0,1.0)
    ){
        lateinit var irisRight: Vector3
        lateinit var irisRightTop: Vector3
        lateinit var irisRightBottom: Vector3

        lateinit var irisLeft: Vector3
        lateinit var irisLeftTop: Vector3
        lateinit var irisLeftBottom: Vector3

        val noseTipTransformed: Vector3
            get() = transformPoint(noseTip, rotationMatrix)
        val noseLeftTransformed: Vector3
            get() = transformPoint(noseLeft, rotationMatrix)
        val noseRightTransformed: Vector3
            get() = transformPoint(noseRight, rotationMatrix)
        val lipTopTransformed: Vector3
            get() = transformPoint(lipTop, rotationMatrix)
        val lipBottomTransformed: Vector3
            get() = transformPoint(lipBottom, rotationMatrix)
        val mouthLeftTransformed: Vector3
            get() = transformPoint(mouthLeft, rotationMatrix)
        val mouthRightTransformed: Vector3
            get() = transformPoint(mouthRight, rotationMatrix)
        val headTopTransformed: Vector3
            get() = transformPoint(headTop, rotationMatrix)
        val chinTransformed: Vector3
            get() = transformPoint(chin, rotationMatrix)
        val noseBridgeLeftTransformed: Vector3
            get() = transformPoint(noseBridgeLeft, rotationMatrix)
        val noseBridgeRightTransformed: Vector3
            get() = transformPoint(noseBridgeRight, rotationMatrix)
        val noseBridgeCenterTransformed: Vector3
            get() = transformPoint(noseBridgeCenter, rotationMatrix)
        val faceMeasureLeftTransformed: Vector3
            get() = transformPoint(faceMeasureLeft, rotationMatrix)
        val faceMeasureRightTransformed: Vector3
            get() = transformPoint(faceMeasureRight, rotationMatrix)
        val leftEyelidTopTransformed: Vector3
            get() = transformPoint(leftEyelidTop, rotationMatrix)
        val leftEyelidBottomTransformed: Vector3
            get() = transformPoint(leftEyelidBottom, rotationMatrix)
        val leftEyelidInnerTransformed: Vector3
            get() = transformPoint(leftEyelidInner, rotationMatrix)
        val leftEyelidOuterTransformed: Vector3
            get() = transformPoint(leftEyelidOuter, rotationMatrix)
        val rightEyelidTopTransformed: Vector3
            get() = transformPoint(rightEyelidTop, rotationMatrix)
        val rightEyelidBottomTransformed: Vector3
            get() = transformPoint(rightEyelidBottom, rotationMatrix)
        val rightEyelidInnerTransformed: Vector3
            get() = transformPoint(rightEyelidInner, rotationMatrix)
        val rightEyelidOuterTransformed: Vector3
            get() = transformPoint(rightEyelidOuter, rotationMatrix)
        val eyeAvarageATransformed: Vector3
            get() = transformPoint(eyeAvarageA, rotationMatrix)
        val eyeAvarageBTransformed: Vector3
            get() = transformPoint(eyeAvarageB, rotationMatrix)
        val eyeAvarageCTransformed: Vector3
            get() = transformPoint(eyeAvarageC, rotationMatrix)
        val eyeAvarageDTransformed: Vector3
            get() = transformPoint(eyeAvarageD, rotationMatrix)
        val eyeAvarageETransformed: Vector3
            get() = transformPoint(eyeAvarageE, rotationMatrix)
        val eyeAvarageFTransformed: Vector3
            get() = transformPoint(eyeAvarageF, rotationMatrix)
        val eyeAvarageGTransformed: Vector3
            get() = transformPoint(eyeAvarageG, rotationMatrix)
        val eyeAvarageHTransformed: Vector3
            get() = transformPoint(eyeAvarageH, rotationMatrix)

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
    protected val POINT_IRIS_LEFT_TOP = 475
    protected val POINT_IRIS_LEFT_BOTTOM = 477
    protected val POINT_IRIS_RIGHT = 468
    protected val POINT_IRIS_RIGHT_TOP = 470
    protected val POINT_IRIS_RIGHT_BOTTOM = 472
    protected val POINT_LEFT_EYE_LID_TOP = 386
    protected val POINT_LEFT_EYE_LID_BOTTOM = 374
    protected val POINT_LEFT_EYE_LID_INNER = 362
    protected val POINT_LEFT_EYE_LID_OUTER = 263
    protected val POINT_RIGHT_EYE_LID_TOP = 159
    protected val POINT_RIGHT_EYE_LID_BOTTOM = 145
    protected val POINT_RIGHT_EYE_LID_INNER = 133
    protected val POINT_RIGHT_EYE_LID_OUTER = 33
    protected val POINT_EYE_DISTANCE_AVARAGE_A = 197
    protected val POINT_EYE_DISTANCE_AVARAGE_B = 195
    protected val POINT_EYE_DISTANCE_AVARAGE_C = 18
    protected val POINT_EYE_DISTANCE_AVARAGE_D = 200
    protected val POINT_EYE_DISTANCE_AVARAGE_E = 5
    protected val POINT_EYE_DISTANCE_AVARAGE_F = 195
    protected val POINT_EYE_DISTANCE_AVARAGE_G = 9
    protected val POINT_EYE_DISTANCE_AVARAGE_H = 8


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