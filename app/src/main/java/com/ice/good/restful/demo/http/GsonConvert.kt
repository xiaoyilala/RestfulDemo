package com.ice.good.restful.demo.http

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ice.good.restful.lib_restful.Convert
import com.ice.good.restful.lib_restful.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type

class GsonConvert: Convert {

    private var gson = Gson()

    override fun <T> convert(rawData: String, dataType: Type): Response<T> {
        val response = Response<T>()
        try{
            val jsonObject = JSONObject(rawData)
            response.code = jsonObject.optInt("code")
            response.msg = jsonObject.optString("msg")
            val data = jsonObject.opt("data")
            if(data is JSONObject || data is JSONArray){
                if(response.code == Response.SUCCESS){
                    response.data = gson.fromJson(data.toString(), dataType)
                }else{
                    response.errorData = gson.fromJson<MutableMap<String, String>>(data.toString(), object : TypeToken<MutableMap<String, String>>(){}.type)
                }
            }else{
                response.data = data as T?
            }
        }catch (e: JSONException){
            e.printStackTrace()
            response.code = -1
            response.msg = e.message
        }
        response.rawData = rawData
        return response
    }
}