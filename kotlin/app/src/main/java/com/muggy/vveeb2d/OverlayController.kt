package com.muggy.vveeb2d

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.*
import android.webkit.WebMessage
import android.webkit.WebSettings
import android.webkit.WebView
import kotlinx.android.synthetic.main.overlay.view.*


class OverlayController(  // declaring required variables
    private val context: Context
){
    private val mView: View
    private var mParams: WindowManager.LayoutParams? = null
    private val mWindowManager: WindowManager
    private val layoutInflater: LayoutInflater
    private val rendererUrl: String = "https://muggy8.github.io/VVeeb2D/"
    var windowWidth:Int = 400
    var windowHeight: Int = 300
    @SuppressLint("JavascriptInterface")
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

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("Error2", e.toString())
        }
    }

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

    }

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