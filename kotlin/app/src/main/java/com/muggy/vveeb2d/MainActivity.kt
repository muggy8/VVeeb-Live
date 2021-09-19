package com.muggy.vveeb2d

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleObserver
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


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

        askForStoragePermission.setOnClickListener {
            requestStoragePermission()
        }

        setViewStateToNotOverlaying()

        udpatePermissionRequestButtonStates()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOverlay()
    }

    private val grantedStoragePermission:Boolean get() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                return true
            }
            return false
        } else { //permission is automatically granted on sdk<23 upon installation
            return true
        }
    }

    private val grantedCameraPermission:Boolean get() {
        return CameraPermissionHelper.hasCameraPermission(this)
    }

    private val grantedOverlayPermission:Boolean get() {
        return Settings.canDrawOverlays(this)
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
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
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as ForegroundService.LocalBinder
            overlayService = binder.service
            mBound = true
            overlayService.overlay.startScreenOverlay()
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
        if (grantedOverlayPermission && grantedStoragePermission){
            startOverlayButton.setVisibility(View.VISIBLE)
        }
        else {
            startOverlayButton.setVisibility(View.GONE)
        }
        currentViewState = VIEW_STATE_NOT_OVERLAYING
    }
    private fun setViewStateToOverlaying(){
        if (grantedOverlayPermission && grantedStoragePermission){
            stopOvlayButton.setVisibility(View.VISIBLE)
        }
        else {
            stopOvlayButton.setVisibility(View.GONE)
        }
        startOverlayButton.setVisibility(View.GONE)
        currentViewState = VIEW_STATE_OVERLAYING
    }

    private fun udpatePermissionRequestButtonStates(){
        if (grantedStoragePermission){
            askForStoragePermission.setVisibility(View.GONE)
        }
        else {
            askForStoragePermission.setVisibility(View.VISIBLE)
        }

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
            askForDOOAPermission.setVisibility(View.VISIBLE)
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
            mBound = false
        }
        setViewStateToOverlaying()
    }
}
