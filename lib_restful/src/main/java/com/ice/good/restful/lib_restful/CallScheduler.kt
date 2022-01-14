package com.ice.good.restful.lib_restful

/**
 * 代理CallFactory创建出来的call对象，从而实现拦截器的派发动作
 */
class CallScheduler(private val callFactory: Call.Factory, private val interceptors: MutableList<Interceptor>) {
    fun newCall(request: Request):Call<*>{
        val newCall = callFactory.newCall(request)
        return ProxyCall(newCall, request)
    }

    internal inner class ProxyCall<T>(private val call:Call<T>, private val request: Request):Call<T>{
        override fun execute(): Response<T> {
            dispatchInterceptor(request, null)
            val response = call.execute()
            dispatchInterceptor(request, response)
            return response
        }

        override fun enqueue(callback: Callback<T>) {
            dispatchInterceptor(request, null)
            call.enqueue(object : Callback<T>{
                override fun success(response: Response<T>) {
                    dispatchInterceptor(request, response)
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

        internal inner class InterceptorChain(private val request: Request, private val response: Response<T>?):Interceptor.Chain{
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