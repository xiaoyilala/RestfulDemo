package com.ice.good.lib.lib.restful

import java.lang.reflect.Type

interface Convert {
    fun <T> convert(rawData: String, dataType: Type): Response<T>
}