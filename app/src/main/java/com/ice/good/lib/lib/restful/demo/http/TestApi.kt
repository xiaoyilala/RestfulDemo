package com.ice.good.lib.lib.restful.demo.http

import com.ice.good.lib.lib.restful.Call
import com.ice.good.lib.lib.restful.annotation.CacheStrategy
import com.ice.good.lib.lib.restful.annotation.Field
import com.ice.good.lib.lib.restful.annotation.POST

interface TestApi {

    @CacheStrategy(value = CacheStrategy.CACHE_FIRST)
    @POST("user/login")
    fun login(@Field("username") userName: String, @Field("password") password:String): Call<LoginModel>
}