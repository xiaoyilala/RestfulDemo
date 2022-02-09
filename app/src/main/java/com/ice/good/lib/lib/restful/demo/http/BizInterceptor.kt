package com.ice.good.lib.lib.restful.demo.http

import android.util.Log
import com.ice.good.lib.lib.restful.Interceptor
import com.ice.good.lib.lib.restful.Request

class BizInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Boolean {
        val request = chain.request()
        val response = chain.response()
        if(chain.isPreRequest){
            request.addHeader("auth-token", "MTU5Mjg1MDg3NDcwNw==")
        }else if(response !=null){
            var outputBuilder = StringBuilder()
            val httpMethod: String =
                if (request.httpMethod == Request.METHOD.GET) "GET" else "POST"
            val requestUrl: String = request.endPointUrl()
            outputBuilder.append("\n$requestUrl==>$httpMethod\n")

            if (request.headers != null) {
                outputBuilder.append("【headers\n")
                request.headers!!.forEach(action = {
                    outputBuilder.append(it.key + ":" + it.value)
                    outputBuilder.append("\n")
                })
                outputBuilder.append("headers】\n")
            }

            if (request.parameters != null && request.parameters!!.isNotEmpty()) {
                outputBuilder.append("【parameters==>\n")
                request.parameters!!.forEach(action = {
                    outputBuilder.append(it.key + ":" + it.value + "\n")
                })
                outputBuilder.append("parameters】\n")
            }

            outputBuilder.append("【response==>\n")
            outputBuilder.append(response.rawData + "\n")
            outputBuilder.append("response】\n")

            Log.d("BizInterceptor", outputBuilder.toString())
        }
        return false
    }
}