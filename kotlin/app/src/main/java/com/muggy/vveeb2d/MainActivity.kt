package com.muggy.vveeb2d

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startOverlayButton.setOnClickListener { startOverlay() }

        stopOverlayButton.setOnClickListener { stopOverlay() }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOverlay()
    }

    private fun getOverlayPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 12345)
            }
        }
    }

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
    }

    lateinit private var foregroundServiceIntent : Intent
    fun startService() {
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
        } else {startService(foregroundServiceIntent)
        }
    }

    private fun stopOverlay(){
        if (foregroundServiceIntent != null){
            stopService(foregroundServiceIntent)
        }
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
