package com.muggy.vveeb2d

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.content.res.Configuration
import android.view.View
import com.google.ar.core.ArCoreApk
import com.google.ar.core.exceptions.UnavailableException

class MainActivity : AppCompatActivity() {

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        isARCoreSupportedAndUpToDate()
    }

    private fun getOverlayPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 12345)
            }
        }
    }

    private fun isARCoreSupportedAndUpToDate(): Boolean {
        return when (ArCoreApk.getInstance().checkAvailability(this)) {
            ArCoreApk.Availability.SUPPORTED_INSTALLED -> true
            ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD, ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                try {
                    // Request ARCore installation or update if needed.
                    when (ArCoreApk.getInstance().requestInstall(this, true)) {
                        ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                            println("ARCore installation requested.")
                            false
                        }
                        ArCoreApk.InstallStatus.INSTALLED -> true
                    }
                } catch (e: UnavailableException) {
                    println("ARCore not installed" + e.toString())
                    false
                }
            }

            ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE ->
                // This device is not supported for AR.
                false

            ArCoreApk.Availability.UNKNOWN_CHECKING -> {
                // ARCore is checking the availability with a remote query.
                // This function should be called again after waiting 200 ms to determine the query result.
                return ArCoreApk.getInstance().checkAvailability(this).isSupported
            }
            ArCoreApk.Availability.UNKNOWN_ERROR, ArCoreApk.Availability.UNKNOWN_TIMED_OUT -> {
                // There was an error checking for AR availability. This may be due to the device being offline.
                // Handle the error appropriately.
                return false
            }
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
//            overlayService.overlay.resizeWindow(
//                overlayWidth.text.toString().toInt(),
//                overlayHeight.text.toString().toInt(),
//            )
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (Settings.canDrawOverlays(this) && CameraPermissionHelper.hasCameraPermission(this)) {
            startService()
        } else {
            Toast.makeText(this,
                "Not all permissions are granted. Please try again.",
                Toast.LENGTH_SHORT).show()
        }
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (::overlayService.isInitialized){
            overlayService.overlay.refreshView()
        }
    }
}