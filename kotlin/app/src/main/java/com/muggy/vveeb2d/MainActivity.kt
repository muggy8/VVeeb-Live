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

        setViewStateToNotOverlaying()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOverlay()
    }

    private fun getOverlayPermission(){
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 12345)
        }
    }

    fun isStoragePermissionGranted(): Boolean {
        val TAG = "Storage Permission"
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
//                Log.v(TAG, "Permission is granted")
                true
            } else {
//                Log.v(TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
//            Log.v(TAG, "Permission is granted")
            true
        }
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

    private fun setViewStateToNotOverlaying(){
        startOverlayButton.setVisibility(View.VISIBLE)
    }
    private fun setViewStateToOverlaying(){
        startOverlayButton.setVisibility(View.GONE)
    }

    private fun startOverlay(){
        if (!isStoragePermissionGranted()){
            return
        }

        var overlayDir = File("${Environment.getExternalStorageDirectory()}/VVeeb2D/overlay/")
        if (!overlayDir.exists()) {
            overlayDir.mkdirs()
        }

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
            mBound = false
        }
        setViewStateToOverlaying()
    }
}