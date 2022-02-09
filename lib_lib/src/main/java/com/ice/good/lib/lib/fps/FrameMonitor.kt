package com.ice.good.lib.lib.fps

import android.view.Choreographer
import com.ice.good.lib.lib.log.YLog
import java.util.concurrent.TimeUnit

/**
 * 人眼在稳定30fps就不会感觉卡顿，注意稳定这个词，如果前500ms刷新了59帧，后500ms刷新了一帧，仍然会感到卡顿。
 * android的 VSYNC 会杜绝16ms（60帧）内刷新两次，如果fps只有50帧，但是丢失的10帧是均分在这个时间段内就不会感到卡顿，
 * 如果10帧集中在一个绘制点就会感到卡顿
 * */
internal class FrameMonitor :Choreographer.FrameCallback {
    private val choreographer = Choreographer.getInstance()
    //上一次记录的时间
    private var frameStartTime:Long = 0
    private var frameStartTimeForInstant:Long = 0
    private var frameCount = 0

    private var listeners = arrayListOf<FpsMonitor.FpsCallback>()

    override fun doFrame(frameTimeNanos: Long) {
        val currentTimeMills = TimeUnit.NANOSECONDS.toMillis(frameTimeNanos)

//        if(frameStartTimeForInstant>0L){
//            val instantFps = 1000/(currentTimeMills-frameStartTimeForInstant).toDouble()
//            YLog.i("FrameMonitor instantFps:", instantFps)
//            for (listener in listeners){
//                listener.onInstantFrame(instantFps)
//            }
//        }
//        frameStartTimeForInstant = currentTimeMills

        if(frameStartTime>0){
            val timeSpan = currentTimeMills - frameStartTime
            frameCount++
            if(timeSpan>=1000){
                val fps = frameCount * 1000 / timeSpan.toDouble()
                YLog.i("FrameMonitor fps:", fps)
                for (listener in listeners){
                    listener.onFrame(fps)
                }
                frameCount = 0
                frameStartTime = currentTimeMills
            }
        }else{
            frameStartTime = currentTimeMills
        }
        start()
    }

    fun start(){
        choreographer.postFrameCallback(this)
    }

    fun stop(){
        frameStartTime=0
        frameCount=0
        choreographer.removeFrameCallback(this)
        listeners.clear()
    }

    fun addListener(listener:FpsMonitor.FpsCallback){
        listeners.add(listener)
    }
}