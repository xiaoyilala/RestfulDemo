package com.ice.good.restful.lib_restful

import kotlin.jvm.Throws

interface Call<T> {
    @Throws(Exception::class)
    fun execute():Response<T>

    fun enqueue(callback: Callback<T>)

    interface Factory{
        fun newCall(request:Request):Call<*>
    }
}