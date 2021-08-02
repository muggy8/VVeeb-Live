package com.muggy.vveeb2d

import android.content.Context
import android.graphics.PixelFormat
import android.net.Uri
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Build
import android.util.Log
import android.view.*
import android.webkit.WebMessage
import android.webkit.WebSettings
import android.webkit.WebView
import com.google.ar.core.*
import com.muggy.vveeb2d.FastMath.clamp
import kotlinx.android.synthetic.main.overlay.view.*
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class OverlayController(  // declaring required variables
    private val context: Context
){
    private val mView: View
    private var mParams: WindowManager.LayoutParams? = null
    private val mWindowManager: WindowManager
    private val layoutInflater: LayoutInflater
    private val rendererUrl: String = "https://muggy8.github.io/VVeeb2D/"
    var windowWidth: Int = 400
    var windowHeight: Int = 300
    private var arSession:Session
    private val displayRotationHelper: DisplayRotationHelper

    private class ARRenderer(
        private val context: Context,
        private val arSession: Session,
        private val displayRotationHelper: DisplayRotationHelper,
        private val faceDetectedCallback: (AugmentedFace)->Any,
    ):GLSurfaceView.Renderer {

        // I have no idea what i'm doing... i'm just copy pasting code here ;w;
        private val augmentedFaceRenderer = AugmentedFaceRenderer()
        private val backgroundRenderer:BackgroundRenderer = BackgroundRenderer()
        override fun onSurfaceCreated(gl: GL10?, p1: EGLConfig?) {
            GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
            backgroundRenderer.createOnGlThread(context);
            augmentedFaceRenderer.createOnGlThread(context, "wireframe.png")
            augmentedFaceRenderer.setMaterialProperties(0.0f, 1.0f, 0.1f, 6.0f)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            displayRotationHelper.onSurfaceChanged(width, height)
            GLES20.glViewport(0, 0, width, height)
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            displayRotationHelper.updateSessionIfNeeded(arSession);
            try {
                arSession.setCameraTextureName(backgroundRenderer.getTextureId());

                // Obtain the current frame from ARSession. When the configuration is set to
                // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
                // camera framerate.
                val frame: Frame = arSession.update()


                // since we need to test the value of the faces first and we might not even render the output sometimes.
                // we test to see if there's a face first before we do anything else
                val faces:Collection<AugmentedFace> = arSession.getAllTrackables(AugmentedFace::class.java)
                if (faces.size == 0){
                    return
                }

                // If frame is ready, render camera preview image to the GL surface.
                backgroundRenderer.draw(frame);

                val camera = frame.camera

                // Get projection matrix.
                val projectionMatrix = FloatArray(16)
                camera.getProjectionMatrix(projectionMatrix, 0, 0.1f, 100.0f)

                // Get camera matrix and draw.
                val viewMatrix = FloatArray(16)
                camera.getViewMatrix(viewMatrix, 0)

                // Compute lighting from average intensity of the image.
                // The first three components are color scaling factors.
                // The last one is the average pixel intensity in gamma space.
                val colorCorrectionRgba = FloatArray(4)
                frame.lightEstimate.getColorCorrection(colorCorrectionRgba, 0)

                val face:AugmentedFace = faces.first()
                if (face.trackingState == TrackingState.TRACKING){
                    // todo logic to pass faces to live2D renderer
                    // Face objects use transparency so they must be rendered back to front without depth write.

                    GLES20.glDepthMask(false)

                    // Each face's region poses, mesh vertices, and mesh normals are updated every frame.
                    // Render the face mesh first, behind any 3D objects attached to the face regions.
                    val modelMatrix = FloatArray(16)
                    face.centerPose.toMatrix(modelMatrix, 0)
                    augmentedFaceRenderer.draw(
                        projectionMatrix, viewMatrix, modelMatrix, colorCorrectionRgba, face
                    )

                    faceDetectedCallback(face)
                }
            }
            catch (ouf:Error) {
                // whatever
            }
        }
    }

    private var arRenderer:ARRenderer
    private val surfaceView: GLSurfaceView

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

        arSession = Session(context, EnumSet.of(Session.Feature.FRONT_CAMERA))
        val config = Config(arSession)
        config.augmentedFaceMode = Config.AugmentedFaceMode.MESH3D
        arSession.configure(config)

        val filter = CameraConfigFilter(arSession)
        filter.targetFps = EnumSet.of(CameraConfig.TargetFps.TARGET_FPS_30)
        val cameraConfigList = arSession.getSupportedCameraConfigs(filter)
        arSession.cameraConfig = cameraConfigList[0]

        displayRotationHelper = DisplayRotationHelper(context);
        arRenderer = ARRenderer(
            context,
            arSession,
            displayRotationHelper,
            { face:AugmentedFace->onFaceTracking(face) }
        )

        surfaceView = mView.surfaceview
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        surfaceView.setRenderer(arRenderer);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        surfaceView.setWillNotDraw(false);

    }

    fun open() {
        try {
            // check if the view is already
            // inflated or present in the window
            if (mView.windowToken == null) {
                if (mView.parent == null) {
                    mWindowManager.addView(mView, mParams)
                }
            }

            WebView.setWebContentsDebuggingEnabled(true);

            arSession.resume()
            surfaceView.onResume()
            displayRotationHelper.onResume()
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
    }

    fun close() {
        try {
            // remove the view from the window
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(mView)
            // invalidate the view
            mView.invalidate()
            // remove all views
            (mView.parent as ViewGroup).removeAllViews()

            displayRotationHelper.onPause()
            surfaceView.onPause()

            arSession.pause()
            arSession.close()

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("Error2", e.toString())
        }
    }

    private fun onFaceTracking(face:AugmentedFace){
        val quaternion = face.centerPose.getRotationQuaternion()
        val eulerAngles = toAngles(
            quaternion[0],
            quaternion[1],
            quaternion[2],
            quaternion[3],
        )

        val live2Dparams:String = ("{"
                + "\"ParamAngleX\":${ clamp(eulerAngles[0], -30f, 30f) },"
                + "\"ParamAngleY\":${ clamp(eulerAngles[2], -30f, 30f) },"
                + "\"ParamAngleZ\":${ clamp(eulerAngles[1], -30f, 30f) },"
            +"}")

        mView.apply {
            webview.postWebMessage(
                WebMessage("{\"type\":\"params\", \"payload\": ${live2Dparams}}"),
                Uri.parse(rendererUrl)
            )
        }
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