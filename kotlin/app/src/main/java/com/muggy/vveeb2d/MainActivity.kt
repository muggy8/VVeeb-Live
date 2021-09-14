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
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    var modelX:Float = 0.0f
    var modelY:Float = 0.0f
    var modelZoom:Float = 0.0f
    var bgR = 255
    var bgG = 255
    var bgB = 255
    var bgA = 255

    var overlayStartedCallbacks:MutableList<(()->Unit)> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var self = this

        setContentView(R.layout.activity_main)
        startOverlayButton.setOnClickListener {
            overlayStartedCallbacks.add({ startVtuberMode() })
            startOverlay()
        }

        startBasicOverlayButton.setOnClickListener {
            overlayStartedCallbacks.add({ startGenaricOverlays() })
            startOverlay()
        }

        startAllOverlayButton.setOnClickListener {
            overlayStartedCallbacks.add({ startVtuberMode() })
            overlayStartedCallbacks.add({ startGenaricOverlays() })
            startOverlay()
        }

        stopOverlayButton.setOnClickListener {
            overlayStartedCallbacks = mutableListOf()
            stopOverlay()
        }

        setViewStateToOverlaying()

        val overlayWidthUpdateDebouncer = debounceFactory()
        overlayWidth.setText(CacheAccess.readString(self, "overlayWidth") ?: "400")
        overlayWidth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (mBound){
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
                if (mBound){
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

        modelX = (CacheAccess.readString(this, "modelX"))?.toFloat() ?: 0.0f
        modelY = (CacheAccess.readString(this, "modelY"))?.toFloat() ?: 0.0f
        modelZoom = (CacheAccess.readString(this, "modelZoom"))?.toFloat() ?: 0.0f

        val modelYUpdateDebouncer = debounceFactory()
        modelUp.setOnClickListener {
            modelY += 0.1f
            modelYUpdateDebouncer({
                CacheAccess.writeString(self, "modelY", modelY.toString())
            }, 1000)
            if (mBound) {
                try { overlayService.overlay.setTranslation(modelX, modelY) } catch (e: Exception){}
            }
        }
        modelDown.setOnClickListener {
            modelY -= 0.1f
            modelYUpdateDebouncer({
                CacheAccess.writeString(self, "modelY", modelY.toString())
            }, 1000)
            if (mBound) {
                try { overlayService.overlay.setTranslation(modelX, modelY) } catch (e: Exception){}
            }
        }

        val modelXUpdateDebouncer = debounceFactory()
        modelLeft.setOnClickListener {
            modelX -= 0.1f
            modelXUpdateDebouncer({
                CacheAccess.writeString(self, "modelX", modelX.toString())
            }, 1000)
            if (mBound) {
                try { overlayService.overlay.setTranslation(modelX, modelY) } catch (e: Exception){}
            }
        }
        modelRight.setOnClickListener {
            modelX += 0.1f
            modelXUpdateDebouncer({
                CacheAccess.writeString(self, "modelX", modelX.toString())
            }, 1000)
            if (mBound) {
                try { overlayService.overlay.setTranslation(modelX, modelY) } catch (e: Exception){}
            }
        }

        val modelZoomUpdateDebouncer = debounceFactory()
        modelIn.setOnClickListener {
            modelZoom += 0.1f
            modelZoomUpdateDebouncer({
                CacheAccess.writeString(self, "modelZoom", modelZoom.toString())
            }, 1000)
            if (mBound) {
                try { overlayService.overlay.setZoom(modelZoom) } catch (e: Exception){}
            }
        }
        modelOut.setOnClickListener {
            modelZoom -= 0.1f
            modelZoomUpdateDebouncer({
                CacheAccess.writeString(self, "modelZoom", modelZoom.toString())
            }, 1000)
            if (mBound) {
                try { overlayService.overlay.setZoom(modelZoom) } catch (e: Exception){}
            }
        }

        bgR = (CacheAccess.readString(this, "bgR"))?.toInt() ?: 255
        bgG = (CacheAccess.readString(this, "bgG"))?.toInt() ?: 255
        bgB = (CacheAccess.readString(this, "bgB"))?.toInt() ?: 255
        bgA = (CacheAccess.readString(this, "bgA"))?.toInt() ?: 255

        val colorRDebouncer = debounceFactory()
        colorR.setProgress(bgR)
        colorR.setOnSeekBarChangeListener (
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//                    TODO("Not yet implemented")
                    bgR = p1
                    if (mBound) {
                        try { overlayService.overlay.setBgColor(bgR/255f, bgG/255f, bgB/255f, bgA/255f) } catch (e: Exception){}
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
//                    TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
//                    TODO("Not yet implemented")
                    colorRDebouncer({
                        CacheAccess.writeString(self, "bgR", bgR.toString())
                    }, 1000)
                }
            }
        )

        val colorGDebouncer = debounceFactory()
        colorG.setProgress(bgG)
        colorG.setOnSeekBarChangeListener (
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//                    TODO("Not yet implemented")
                    bgG = p1
                    if (mBound) {
                        try { overlayService.overlay.setBgColor(bgR/255f, bgG/255f, bgB/255f, bgA/255f) } catch (e: Exception){}
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
//                    TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
//                    TODO("Not yet implemented")
                    colorGDebouncer({
                        CacheAccess.writeString(self, "bgG", bgG.toString())
                    }, 1000)
                }
            }
        )

        val colorBDebouncer = debounceFactory()
        colorB.setProgress(bgB)
        colorB.setOnSeekBarChangeListener (
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//                    TODO("Not yet implemented")
                    bgB = p1
                    if (mBound) {
                        try { overlayService.overlay.setBgColor(bgR/255f, bgG/255f, bgB/255f, bgA/255f) } catch (e: Exception){}
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
//                    TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
//                    TODO("Not yet implemented")
                    colorBDebouncer({
                        CacheAccess.writeString(self, "bgB", bgB.toString())
                    }, 1000)
                }
            }
        )

        val colorADebouncer = debounceFactory()
        colorA.setProgress(bgA)
        colorA.setOnSeekBarChangeListener (
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//                    TODO("Not yet implemented")
                    bgA = p1
                    if (mBound) {
                        try { overlayService.overlay.setBgColor(bgR/255f, bgG/255f, bgB/255f, bgA/255f) } catch (e: Exception){}
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
//                    TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
//                    TODO("Not yet implemented")
                    colorADebouncer({
                        CacheAccess.writeString(self, "bgA", bgA.toString())
                    }, 1000)
                }
            }
        )
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
            overlayStartedCallbacks.forEach{callback->callback()}
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private fun setViewStateToNotOverlaying(){
        stopOverlayButton.setVisibility(View.VISIBLE)
        modelPositioner.setVisibility(View.VISIBLE)
        modelBG.setVisibility(View.VISIBLE)
        startOverlayButton.setVisibility(View.GONE)
        startBasicOverlayButton.setVisibility(View.GONE)
        startAllOverlayButton.setVisibility(View.GONE)
    }
    private fun setViewStateToOverlaying(){
        stopOverlayButton.setVisibility(View.GONE)
        modelPositioner.setVisibility(View.GONE)
        modelBG.setVisibility(View.GONE)
        startOverlayButton.setVisibility(View.VISIBLE)
        startBasicOverlayButton.setVisibility(View.VISIBLE)
        startAllOverlayButton.setVisibility(View.VISIBLE)
    }

    private fun startOverlay(){
        if (!isStoragePermissionGranted()){
            return
        }
        var modelDir = File("${Environment.getExternalStorageDirectory()}/VVeeb2D/model/")
        if (!modelDir.exists()) {
            modelDir.mkdirs()
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

    private fun startVtuberMode(){
        if (mBound){
            overlayService.overlay.startVtuberOverlay()

            overlayService.overlay.resizeWindow(
                overlayWidth.text.toString().toInt(),
                overlayHeight.text.toString().toInt(),
            )

            overlayService.overlay.setTranslation(modelX, modelY)
            overlayService.overlay.setZoom(modelZoom)
            overlayService.overlay.setBgColor(bgR/255f, bgG/255f, bgB/255f, bgA/255f)
        }
    }

    private fun startGenaricOverlays(){
        if (mBound){
            // do something here
            overlayService.overlay.startScreenOverlay()
        }
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