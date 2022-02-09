package com.ice.good.lib.lib.restful.demo.http

import com.ice.good.lib.lib.restful.*
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.http.*
import java.lang.IllegalStateException

class RetrofitCallFactory(baseUrl: String): Call.Factory {

    private var apiService: ApiService
    private var convert: Convert

    init{
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .build()

        apiService = retrofit.create(ApiService::class.java)
        convert = GsonConvert()
    }

    override fun newCall(request: Request): Call<Any> {
        return RetrofitCall(request)
    }

    internal inner class RetrofitCall<T>(val request: Request): Call<T> {
        override fun execute(): Response<T> {
            val realCall: retrofit2.Call<ResponseBody> = createRealCall(request)
            val response: retrofit2.Response<ResponseBody> = realCall.execute()
            return parseResponse(response)
        }

        override fun enqueue(callback: Callback<T>) {
            val realCall: retrofit2.Call<ResponseBody> = createRealCall(request)
            realCall.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: retrofit2.Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>
                ) {
                    val parseResponse: Response<T> = parseResponse(response)
                    callback.success(parseResponse)
                }

                override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
                    callback.fail(t)
                }

            })
        }

        private fun parseResponse(response: retrofit2.Response<ResponseBody>): Response<T> {
            var rawData: String? = ""
            if(response.isSuccessful){
                val body: ResponseBody? = response.body()
                if(body!=null){
                    rawData = body.string()
                }
            }else{
                val body: ResponseBody? = response.errorBody()
                if (body != null) {
                    rawData = body.string()
                }
            }
            return convert.convert(rawData!!, request.returnType!!)
        }

        private fun createRealCall(request: Request):retrofit2.Call<ResponseBody>{
            if(request.httpMethod == Request.METHOD.GET){
                return apiService.get(request.headers, request.endPointUrl(), request.parameters)
            }else if(request.httpMethod == Request.METHOD.POST){
                var requestBody: RequestBody = buildRequestBody(request)
                return apiService.post(request.headers, request.endPointUrl(), requestBody)
            }else if(request.httpMethod == Request.METHOD.PUT){
                var requestBody: RequestBody = buildRequestBody(request)
                return apiService.put(request.headers, request.endPointUrl(), requestBody)
            }else if(request.httpMethod == Request.METHOD.DELETE){
                return apiService.delete(request.headers, request.endPointUrl())
            }else {
                throw IllegalStateException("restful only support GET POST PUT DELETE for now ,url=" + request.endPointUrl())
            }
        }

    }

    private fun buildRequestBody(request: Request): RequestBody {
        val parameters: MutableMap<String, String>? = request.parameters
        val builder = FormBody.Builder()
        var requestBody: RequestBody
        var jsonObject = JSONObject()

        if (parameters != null) {
            for ((k, v) in parameters) {
                if (request.formPost) {
                    builder.add(k, v)
                } else {
                    jsonObject.put(k, v)
                }
            }
        }

        if (request.formPost) {
            requestBody = builder.build()
        } else {
            requestBody =
                RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonObject.toString())
        }
        return requestBody
    }

    interface ApiService{
        @GET
        fun get(@HeaderMap headers: MutableMap<String, String>?, @Url url: String,
                @QueryMap(encoded = true) params:MutableMap<String, String>?
        ):retrofit2.Call<ResponseBody>

        @POST
        fun post(@HeaderMap headers: MutableMap<String, String>?, @Url url: String,
                @Body body: RequestBody?
        ):retrofit2.Call<ResponseBody>

        @PUT
        fun put(@HeaderMap headers: MutableMap<String, String>?, @Url url: String,
                 @Body body: RequestBody?
        ):retrofit2.Call<ResponseBody>

        //不可以携带requestBody
        @DELETE
        fun delete(@HeaderMap headers: MutableMap<String, String>?, @Url url: String
        ):retrofit2.Call<ResponseBody>
    }
}