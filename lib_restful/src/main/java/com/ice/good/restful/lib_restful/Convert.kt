package com.ice.good.restful.lib_restful

import java.lang.reflect.Type

interface Convert {
    fun <T> convert(rawData: String, dataType: Type):Response<T>
}