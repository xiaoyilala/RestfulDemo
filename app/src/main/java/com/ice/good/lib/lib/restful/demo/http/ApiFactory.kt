package com.ice.good.lib.lib.restful.demo.http

import com.ice.good.lib.lib.restful.YRestful

object ApiFactory {
    val BASE_URL = "https://www.wanandroid.com/"

    private val yRestful: YRestful = YRestful(BASE_URL, RetrofitCallFactory(BASE_URL))

    init {
        yRestful.addInterceptor(BizInterceptor())
    }

    fun <T> create(service: Class<T>):T{
        return yRestful.create(service)
    }
}