package com.ice.good.restful.demo.http

import com.ice.good.restful.lib_restful.Call
import com.ice.good.restful.lib_restful.annotation.Field
import com.ice.good.restful.lib_restful.annotation.POST
import com.ice.good.restful.lib_restful.annotation.Path

interface TestApi {

    @POST("user/login")
    fun login(@Field("username") userName: String, @Field("password") password:String):Call<String>
}