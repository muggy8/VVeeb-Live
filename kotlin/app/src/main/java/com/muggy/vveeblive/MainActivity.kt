package com.muggy.vveeblive

import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        startOverlayButton.setOnClickListener {
            startOverlay()
            setViewStateToOverlaying()
        }
        stopOvlayButton.setOnClickListener {
            stopOverlay()
            setViewStateToNotOverlaying()
        }

        askForCameraPermission.setOnClickListener {
            requestCameraPermission()
        }

        askForDOOAPermission.setOnClickListener {
            requestOverlayPermission()
        }

        if (this.getExternalFilesDir("overlay") != null){
            openOverlayLocation.setOnClickListener {
                openFolder(Uri.parse(this.getExternalFilesDir("overlay")!!.absolutePath))
            }
        }

        setViewStateToNotOverlaying()

        udpatePermissionRequestButtonStates()

        this.getExternalFilesDir("overlay")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOverlay()
    }

    private val grantedCameraPermission:Boolean get() {
        return CameraPermissionHelper.hasCameraPermission(this)
    }

    private val grantedOverlayPermission:Boolean get() {
        return Settings.canDrawOverlays(this)
    }

    private fun requestOverlayPermission(){
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 12345)
        }
    }

    private fun requestCameraPermission(){
        CameraPermissionHelper.requestCameraPermission(this)
    }

    private lateinit var overlayService: ForegroundService
    private var mBound: Boolean = false
    private val self = this
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as ForegroundService.LocalBinder
            overlayService = binder.service
            mBound = true
            overlayService.overlay.setup(self)
            overlayService.overlay.startScreenOverlay(grantedCameraPermission)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }


    private val VIEW_STATE_NOT_OVERLAYING = 0
    private val VIEW_STATE_OVERLAYING = 1
    private var currentViewState:Int = 0
    private fun setViewStateToNotOverlaying(){
        stopOvlayButton.setVisibility(View.GONE)
        if (grantedOverlayPermission){
            startOverlayButton.setVisibility(View.VISIBLE)
        }
        else {
            startOverlayButton.setVisibility(View.GONE)
        }
        currentViewState = VIEW_STATE_NOT_OVERLAYING
    }
    private fun setViewStateToOverlaying(){
        if (grantedOverlayPermission){
            stopOvlayButton.setVisibility(View.VISIBLE)
        }
        else {
            stopOvlayButton.setVisibility(View.GONE)
        }
        startOverlayButton.setVisibility(View.GONE)
        currentViewState = VIEW_STATE_OVERLAYING
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        udpatePermissionRequestButtonStates()

//        var discardList = webviewCallbacks.map { callback->callback(requestCode, permissions, grantResults) }

        webviewCallbacks = (webviewCallbacks.filter { callback->
            val callbackMatchedRequest = callback(requestCode, permissions, grantResults)
            !callbackMatchedRequest
        }).toMutableList()
    }

    private fun udpatePermissionRequestButtonStates(){
        if (grantedOverlayPermission){
            askForDOOAPermission.setVisibility(View.GONE)
        }
        else {
            askForDOOAPermission.setVisibility(View.VISIBLE)
        }

        if (grantedCameraPermission){
            askForCameraPermission.setVisibility(View.GONE)
        }
        else {
            askForCameraPermission.setVisibility(View.VISIBLE)
        }

        if (currentViewState == VIEW_STATE_NOT_OVERLAYING){
            setViewStateToNotOverlaying()
        }
        else if (currentViewState == VIEW_STATE_OVERLAYING){
            setViewStateToOverlaying()
        }
    }

    private fun startOverlay(){
        setViewStateToNotOverlaying()
        startService()
    }

    lateinit private var foregroundServiceIntent : Intent
    private fun startService() {
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
        bindService(foregroundServiceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun stopOverlay(){
        if (::foregroundServiceIntent.isInitialized){
            unbindService(connection)
            stopService(foregroundServiceIntent)
            mBound = false
        }
        setViewStateToOverlaying()
    }

    private fun openFolder(uri: Uri ) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "resource/folder")
        val pm: PackageManager = this.getPackageManager()
        val apps = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (apps.size > 0) {
            startActivity(intent)
        }
        else {
            Toast.makeText(this, "You do not have an app that can open the folder location", Toast.LENGTH_SHORT).show()
        }
    }

    private var webviewCallbacks:MutableList<(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)->Boolean> = mutableListOf()

    fun askForWebviewPermission(request: PermissionRequest, requestedPermission: String, originalRequestCode: Int){
        // do the request granting thingy
        when {

            // situation 1: the permission is already granted. we just answer the call and give the webview what it needs
            ContextCompat.checkSelfPermission(
                this,
//                Manifest.permission.CAMERA
                requestedPermission,
            ) == PackageManager.PERMISSION_GRANTED -> {
                request.grant(request.resources)
            }

            // situation 2: we dont yet have the permission yet so we ask for it
            else -> {
                // setup request callbacks
                val onRequestGrantedCallback = { requestCode: Int, permissions: Array<out String>, grantResults: IntArray ->
                    var found = false
                    if (requestCode == originalRequestCode) {
                        found = true
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                            request.grant(request.resources)
                        }
                        else {
                            request.deny()
                        }
                    }
                    found
                }

                webviewCallbacks.add(onRequestGrantedCallback)

                // make the actual request
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(requestedPermission),
                    originalRequestCode
                )
            }

        }

    }
}
