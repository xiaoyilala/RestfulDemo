package com.ice.good.lib.common

import androidx.lifecycle.*
import java.util.concurrent.ConcurrentHashMap

object MessageBus {

    private val eventMap = ConcurrentHashMap<String, StickyLiveData<*>>()

    class StickyLiveData<T>(private val eventName:String): LiveData<T>() {
        internal var stickyData:T?=null
        internal var version = 0

        fun clearStickyValue(){
            stickyData = null
        }

        fun setStickyValue(stickyData:T){
            this.stickyData = stickyData
            setValue(stickyData)
        }

        fun postStickValue(stickyData:T){
            this.stickyData = stickyData
            postValue(stickyData)
        }

        override fun setValue(value: T) {
            version++
            super.setValue(value)
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            observerSticky(owner,false, observer)
        }

        fun observerSticky(owner: LifecycleOwner, sticky:Boolean, observer: Observer<in T>){
            //监听宿主生命周期变化，反注册
            owner.lifecycle.addObserver(LifecycleEventObserver{ lifecycleOwner, event ->
                if(event == Lifecycle.Event.ON_DESTROY){
                    eventMap.remove(eventName)
                }
            })
            super.observe(owner, StickyObserver(this, sticky, observer))
        }

    }

    internal class StickyObserver<T>(
        val stickyLiveData: StickyLiveData<T>,
        val sticky: Boolean,
        val observer: Observer<in T>
    ):Observer<T>{
        //lastVersion 和livedata的version 对齐的原因，就是为控制黏性事件的分发。
        //sticky 不等于true , 只能接收到注册之后发送的消息，如果要接收黏性事件，则sticky需要传递为true
        private var lastVersion = stickyLiveData.version

        override fun onChanged(t: T) {
            if(lastVersion >= stickyLiveData.version){
                if(sticky && stickyLiveData.stickyData!=null){
                    //就说明stickyLiveData  没有更新的数据需要发送。
                    observer.onChanged(stickyLiveData.stickyData)
                }
                return
            }
            lastVersion = stickyLiveData.version
            observer.onChanged(t)
        }

    }

}