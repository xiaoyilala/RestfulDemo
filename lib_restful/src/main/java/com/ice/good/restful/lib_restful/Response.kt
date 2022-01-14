package com.ice.good.restful.lib_restful

open class Response<T> {

    companion object{
        const val SUCCESS = 0
        const val NEED_LOGIN = -1001
    }

    // 原始数据
    var rawData:String? = null
    // 状态码
    var code = 0
    // 业务数据
    var data:T?=null
    // 错误状态下的数据
    var errorData:Map<String, String>? = null
    // 错误信息
    var msg:String? = null

    fun successful(): Boolean{
        return code == SUCCESS
    }

}