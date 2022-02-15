package com.ice.good.lib.lib.restful

import android.util.Log
import com.ice.good.lib.lib.cache.Storage
import com.ice.good.lib.lib.util.MainHandler
import com.ice.good.lib.lib.executor.GoodExecutor
import com.ice.good.lib.lib.restful.annotation.CacheStrategy

/**
 * 代理CallFactory创建出来的call对象，从而实现拦截器的派发动作
 */
class CallScheduler(private val callFactory: Call.Factory, private val interceptors: MutableList<Interceptor>) {

    fun newCall(request: Request): Call<*> {
        val newCall = callFactory.newCall(request)
        return ProxyCall(newCall, request)
    }

    internal inner class ProxyCall<T>(private val call: Call<T>, private val request: Request):
        Call<T> {
        override fun execute(): Response<T> {
            dispatchInterceptor(request, null)
            if(request.cacheStrategy == CacheStrategy.CACHE_FIRST){
                val cacheResponse = readCache<T>()
                if(cacheResponse.data!=null){
                    return cacheResponse
                }
            }
            val response = call.execute()
            saveCacheIfNeed(response)
            dispatchInterceptor(request, response)
            return response
        }

        override fun enqueue(callback: Callback<T>) {
            dispatchInterceptor(request, null)
            if(request.cacheStrategy == CacheStrategy.CACHE_FIRST){
                GoodExecutor.execute(runnable = {
                    val cacheResponse = readCache<T>()
                    if (cacheResponse.data != null) {
                        //抛到主线程里面
                        MainHandler.sendAtFrontOfQueue(runnable = Runnable {
                            callback.success(cacheResponse)
                        })
                        Log.d("CallScheduler","enqueue ,getCache : " + request.getCacheKey())
                    }
                })
            }
            call.enqueue(object : Callback<T> {
                override fun success(response: Response<T>) {
                    dispatchInterceptor(request, response)
                    saveCacheIfNeed(response)
                    callback.success(response)
                }

                override fun fail(throwable: Throwable) {
                    callback.fail(throwable)
                }
            })
        }

        private fun dispatchInterceptor(request: Request, response: Response<T>?){
            if(interceptors.size<=0){
                return
            }
            InterceptorChain(request, response).dispatch()
        }

        private fun <T> readCache():Response<T>{
            val cacheKey = request.getCacheKey()
            val cache = Storage.getCache<T>(cacheKey)
            val cacheResponse = Response<T>()
            cacheResponse.data = cache
            cacheResponse.code = Response.NEED_LOGIN
            cacheResponse.msg = "缓存获取成功"
            return cacheResponse
        }

        private fun saveCacheIfNeed(response:Response<T>){
            if(request.cacheStrategy == CacheStrategy.CACHE_FIRST || request.cacheStrategy == CacheStrategy.NET_CACHE){
                if(response.data!=null){
                    GoodExecutor.execute(runnable = {
                        Storage.saveCache(request.getCacheKey(), response.data)
                    })
                }
            }
        }

        internal inner class InterceptorChain(private val request: Request, private val response: Response<T>?):
            Interceptor.Chain {
            //分发的第几个拦截器
            var index:Int=0

            override val isPreRequest: Boolean
                get() = response == null

            override fun request(): Request {
                return request
            }

            override fun response(): Response<*>? {
                return response
            }

            fun dispatch(){
                val interceptor = interceptors[index]
                val intercept = interceptor.intercept(this)
                index++
                if(!intercept && index<interceptors.size){
                    dispatch()
                }
            }
        }

    }


}