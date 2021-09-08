package com.muggy.vveeb2d

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    var modelX:Float = 1.0f
    var modelY:Float = 1.0f
    var modelZoom:Float = 1.0f

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var self = this

        setContentView(R.layout.activity_main)
        startOverlayButton.setOnClickListener { startOverlay() }

        stopOverlayButton.setOnClickListener { stopOverlay() }

        setViewStateToOverlaying()

        val overlayWidthUpdateDebouncer = debounceFactory()
        overlayWidth.setText(CacheAccess.readString(self, "overlayWidth") ?: "400")
        overlayWidth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (::overlayService.isInitialized){
                    try{
                        overlayService.overlay.windowWidth = s.toString().toInt()
                        overlayService.overlay.resizeWindow(
                            overlayService.overlay.windowWidth,
                            overlayService.overlay.windowHeight,
                        )
                    }
                    catch (ouf: Error){
                        // whatever
                    }
                }

                overlayWidthUpdateDebouncer({
                    CacheAccess.writeString(self, "overlayWidth", s.toString())
                }, 1000)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        val overlayHeightUpdateDebouncer = debounceFactory()
        overlayHeight.setText(CacheAccess.readString(self, "overlayHeight") ?: "300")
        overlayHeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (::overlayService.isInitialized){
                    try{
                        overlayService.overlay.windowHeight = s.toString().toInt()
                        overlayService.overlay.resizeWindow(
                            overlayService.overlay.windowWidth,
                            overlayService.overlay.windowHeight,
                        )
                    }
                    catch (ouf: Error){
                        // whatever
                    }
                }

                overlayHeightUpdateDebouncer({
                    CacheAccess.writeString(self, "overlayHeight", s.toString())
                }, 1000)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        modelX = (CacheAccess.readString(this, "modelX"))?.toFloat() ?: 1.0f
        modelY = (CacheAccess.readString(this, "modelY"))?.toFloat() ?: 1.0f
        modelZoom = (CacheAccess.readString(this, "modelZoom"))?.toFloat() ?: 1.0f

        val modelYUpdateDebouncer = debounceFactory()
        modelUp.setOnClickListener {
            modelY += 0.1f
            modelYUpdateDebouncer({
                CacheAccess.writeString(self, "modelY", modelY.toString())
            }, 1000)
            if (::overlayService.isInitialized) {
                try { overlayService.overlay.setTranslation(modelX, modelY) } catch (e: Exception){}
            }
        }
        modelDown.setOnClickListener {
            modelY -= 0.1f
            modelYUpdateDebouncer({
                CacheAccess.writeString(self, "modelY", modelY.toString())
            }, 1000)
            if (::overlayService.isInitialized) {
                try { overlayService.overlay.setTranslation(modelX, modelY) } catch (e: Exception){}
            }
        }

        val modelXUpdateDebouncer = debounceFactory()
        modelLeft.setOnClickListener {
            modelX += 0.1f
            modelXUpdateDebouncer({
                CacheAccess.writeString(self, "modelX", modelX.toString())
            }, 1000)
            if (::overlayService.isInitialized) {
                try { overlayService.overlay.setTranslation(modelX, modelY) } catch (e: Exception){}
            }
        }
        modelRight.setOnClickListener {
            modelX -= 0.1f
            modelXUpdateDebouncer({
                CacheAccess.writeString(self, "modelX", modelX.toString())
            }, 1000)
            if (::overlayService.isInitialized) {
                try { overlayService.overlay.setTranslation(modelX, modelY) } catch (e: Exception){}
            }
        }

        val modelZoomUpdateDebouncer = debounceFactory()
        modelIn.setOnClickListener {
            modelZoom += 0.1f
            modelZoomUpdateDebouncer({
                CacheAccess.writeString(self, "modelZoom", modelZoom.toString())
            }, 1000)
            if (::overlayService.isInitialized) {
                try { overlayService.overlay.setZoom(modelZoom) } catch (e: Exception){}
            }
        }
        modelOut.setOnClickListener {
            modelZoom -= 0.1f
            modelZoomUpdateDebouncer({
                CacheAccess.writeString(self, "modelZoom", modelZoom.toString())
            }, 1000)
            if (::overlayService.isInitialized) {
                try { overlayService.overlay.setZoom(modelZoom) } catch (e: Exception){}
            }
        }
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
            overlayService.overlay.resizeWindow(
                overlayWidth.text.toString().toInt(),
                overlayHeight.text.toString().toInt(),
            )

            overlayService.overlay.setTranslation(modelX, modelY)
            overlayService.overlay.setZoom(modelZoom)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private fun setViewStateToNotOverlaying(){
        stopOverlayButton.setVisibility(View.VISIBLE)
        modelPositioner.setVisibility(View.VISIBLE)
        startOverlayButton.setVisibility(View.GONE)
    }
    private fun setViewStateToOverlaying(){
        stopOverlayButton.setVisibility(View.GONE)
        modelPositioner.setVisibility(View.GONE)
        startOverlayButton.setVisibility(View.VISIBLE)
    }

    private fun startOverlay(){
        if (!isStoragePermissionGranted()){
            return
        }
        var modelDir = File("${Environment.getExternalStorageDirectory()}/VVeeb2D/model/")
        if (!modelDir.exists()) {
            modelDir.mkdirs();
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
        }
        setViewStateToOverlaying()
    }
}