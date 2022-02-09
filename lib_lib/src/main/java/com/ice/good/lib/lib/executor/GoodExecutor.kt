package com.ice.good.lib.lib.executor

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.IntRange
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock

object GoodExecutor {

    private const val TAG: String = "GoodExecutor"

    private val goodExecutor: ThreadPoolExecutor
    private val lock = ReentrantLock()
    private val pauseCondition = lock.newCondition()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var pause = false

    init{

        val cpuCount = Runtime.getRuntime().availableProcessors()
        val coreThreadCount = cpuCount+1
        val maxThreadCount = 2*cpuCount+1
        val keepAliveTime = 30L
        val unit = TimeUnit.SECONDS

        val blockingQueue: PriorityBlockingQueue<out Runnable> = PriorityBlockingQueue()

        val seq = AtomicLong()
        val threadFactory = ThreadFactory{
            val thread = Thread(it)
            thread.name = "GoodExecutor-thread-"+seq.getAndIncrement()
            return@ThreadFactory thread
        }

        goodExecutor = object :ThreadPoolExecutor(coreThreadCount,
            maxThreadCount,
            keepAliveTime,
            unit,
            blockingQueue as BlockingQueue<Runnable>,
            threadFactory){
            override fun beforeExecute(t: Thread?, r: Runnable?) {
                if(pause){
                    try {
                        lock.lock()
                        pauseCondition.await()
                    }finally {
                        lock.unlock()
                    }
                }
            }

            override fun afterExecute(r: Runnable?, t: Throwable?) {
                // todo
                Log.d(TAG, "已执行完的任务的优先级是：" + (r as PriorityRunnable).priority)
            }
        }
    }

    fun pause(){
        try {
            lock.lock()
            if(pause){
                return
            }
            pause = true
        }finally {
            lock.unlock()
        }
        Log.d(TAG, TAG+"is paused")
    }

    fun resume(){
        try {
            lock.lock()
            if(!pause){
                return
            }
            pause = false
            pauseCondition.signalAll()
        }finally {
            lock.unlock()
        }
        Log.d(TAG, TAG+"is resumed")
    }

    @JvmOverloads
    fun execute(@IntRange(from = 0, to = 10) priority: Int = 4, runnable: Runnable){
        goodExecutor.execute(PriorityRunnable(priority, runnable))
    }

    @JvmOverloads
    fun execute(@IntRange(from = 0, to = 10) priority: Int = 4, runnable: Callable<*>){
        goodExecutor.execute(PriorityRunnable(priority, runnable))
    }

    class PriorityRunnable(val priority: Int, private val runnable: Runnable):Runnable, Comparable<PriorityRunnable>{
        override fun run() {
            runnable.run()
        }

        override fun compareTo(other: PriorityRunnable): Int {
            return if(this.priority<other.priority) 1 else if(this.priority>other.priority) -1 else 0
        }
    }

    abstract class Callable<T>: Runnable{

        open fun onPrepare(){}
        abstract fun onBackground():T
        abstract fun onCompleted(t: T)

        override fun run() {
            mainHandler.post{onPrepare()}

            val t = onBackground()

            //防止需要执行onCompleted了，onPrepare还没被执行
            mainHandler.removeCallbacksAndMessages(null)
            mainHandler.post{onCompleted(t)}
        }
    }
}