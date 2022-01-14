package com.ice.good.restful.lib_restful

interface Callback<T> {
    fun success(response: Response<T>)
    fun fail(throwable: Throwable)
}