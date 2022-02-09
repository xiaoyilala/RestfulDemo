package com.ice.good.lib.lib.restful

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

class YRestful(val baseUrl: String, callFactory: Call.Factory) {
    private var interceptors:MutableList<Interceptor> = mutableListOf()
    private var callScheduler: CallScheduler
    private var methodService: ConcurrentHashMap<Method, MethodParse> = ConcurrentHashMap()

    init {
        callScheduler = CallScheduler(callFactory, interceptors)
    }

    fun addInterceptor(interceptor: Interceptor){
        interceptors.add(interceptor)
    }

    /**
     * interface ApiService {
     *  @Headers("auth-token:token", "accountId:123456")
     *  @BaseUrl("https://api.devio.org/as/")
     *  @POST("/cities/{province}")
     *  @GET("/cities")
     * fun listCities(@Path("province") province: Int,@Filed("page") page: Int): HiCall<JsonObject>
     * }
     */
    fun <T> create(service: Class<T>):T{
        return Proxy.newProxyInstance(service.classLoader, arrayOf(service), object :InvocationHandler{
            //bugFix:此处需要考虑 空参数
            override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {
                var methodParse = methodService[method]
                if(methodParse==null){
                    methodParse = MethodParse.parse(baseUrl, method!!)
                    methodService[method] = methodParse
                }

                //bugFix：此处 应当考虑到 methodParser复用，每次调用都应当解析入参
                val newRequest = methodParse.newRequest(method, args)
                return callScheduler.newCall(newRequest)
            }
        }) as T
    }
}