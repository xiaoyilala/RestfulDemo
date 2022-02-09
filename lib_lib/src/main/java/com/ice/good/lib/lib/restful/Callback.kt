package com.ice.good.lib.lib.restful

interface Callback<T> {
    fun success(response: Response<T>)
    fun fail(throwable: Throwable)
}