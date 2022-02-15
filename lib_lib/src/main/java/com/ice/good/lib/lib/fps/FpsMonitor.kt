package com.ice.good.lib.lib.fps

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import com.ice.good.lib.lib.util.ActivityManager
import com.ice.good.lib.lib.util.AppGlobals
import com.ice.good.lib.lib.R
import java.text.DecimalFormat

object FpsMonitor {

    private val fpsViewer = FpsView()
    fun toggle() {
        fpsViewer.toggle()
    }

    fun listener(callback: FpsCallback) {
        fpsViewer.addListener(callback)
    }

    interface FpsCallback{
        //平均帧数
        fun onFrame(fps:Double)
        //瞬时帧数
        fun onInstantFrame(fps:Double)
    }

    private class FpsView{
        private val params = WindowManager.LayoutParams()
        private var show = false
        private val application = AppGlobals.get()!!
        private val fpsView = LayoutInflater.from(application).inflate(R.layout.fps_view, null) as TextView

        private val decimal = DecimalFormat("#.0 fps")
        private var windowManager:WindowManager?=null
        private val frameMonitor = FrameMonitor()

        init {
            windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            params.width = WindowManager.LayoutParams.WRAP_CONTENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            params.format = PixelFormat.TRANSLUCENT
            params.gravity = Gravity.RIGHT or Gravity.TOP

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                params.type = WindowManager.LayoutParams.TYPE_TOAST
            }

            frameMonitor.addListener(object :FpsCallback{
                override fun onFrame(fps: Double) {
                    fpsView.text = decimal.format(fps)
                }

                override fun onInstantFrame(fps: Double) {
                }

            })

            ActivityManager.instance.addFrontBackCallback(object : ActivityManager.FrontBackCallback{
                override fun onChanged(front: Boolean) {
                    if (front) {
                        play()
                    } else {
                        stop();
                    }
                }

            })
        }

        private fun play() {
            if(!hasOverlayPermission()){
                startOverlaySettingActivity()
                return
            }
            frameMonitor.start()
            if(!show){
                show = true
                windowManager?.addView(fpsView, params)
            }
        }

        private fun stop() {
            frameMonitor.stop()
            if(show){
                show = false
                windowManager?.removeView(fpsView)
            }
        }

        fun toggle() {
            if (show) {
                stop()
            } else {
                play()
            }
        }

        fun addListener(callback: FpsCallback) {
            frameMonitor.addListener(callback)
        }

        private fun startOverlaySettingActivity(){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                application.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+application.packageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }

        private fun hasOverlayPermission():Boolean{
            return Build.VERSION.SDK_INT<Build.VERSION_CODES.M || Settings.canDrawOverlays(application)
        }
    }
}