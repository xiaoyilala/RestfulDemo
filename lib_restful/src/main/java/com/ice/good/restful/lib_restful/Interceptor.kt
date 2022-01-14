package com.ice.good.restful.lib_restful

interface Interceptor {
    fun intercept(chain: Chain):Boolean

    /**
     * Chain 对象会在派发拦截器的时候创建
     */
    interface Chain{
        //是否是在请求前
        val isPreRequest: Boolean
            get() = true

        fun request():Request

        //这个response对象 在网络发起之前，是为空的
        fun response():Response<*>?
    }
}