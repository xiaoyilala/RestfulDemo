package com.ice.good.restful.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.ice.good.lib_common.loadUrl
import com.ice.good.restful.demo.http.ApiFactory
import com.ice.good.restful.demo.http.RetrofitCallFactory
import com.ice.good.restful.demo.http.TestApi
import com.ice.good.restful.lib_restful.Callback
import com.ice.good.restful.lib_restful.Response
import com.ice.good.ui.view.banner.YBanner
import com.ice.good.ui.view.banner.core.BannerData
import com.ice.good.ui.view.banner.indicator.NumIndicator
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nameTv = findViewById<EditText>(R.id.tv_name)
        val pwTv = findViewById<EditText>(R.id.tv_pw)

        findViewById<TextView>(R.id.tv).setOnClickListener {
            val name = nameTv.text.toString()
            val password = pwTv.text.toString()
            ApiFactory.create(TestApi::class.java).login(name, password).enqueue(object : Callback<String> {
                override fun success(response: Response<String>) {
                    Log.d("ApiFactory", "thread："+Thread.currentThread().name)
                    val rawData = response.rawData
                    val errorData = response.errorData
                    val data = response.data.toString()
                    Log.d("ApiFactory", "rawData: "+rawData)
                    Log.d("ApiFactory", "errorData: "+errorData)
                    Log.d("ApiFactory", "data: "+data)
                }

                override fun fail(throwable: Throwable) {
                    Log.d("ApiFactory", "thread："+Thread.currentThread().name)
                    Log.d("ApiFactory", "throwable: "+throwable.message)
                    throwable.printStackTrace()
                }

            })
        }

        val urls = mutableListOf<String>()
        urls.add("https://tenfei05.cfp.cn/creative/vcg/800/new/VCG41N1209433139.jpg")
        urls.add("https://tenfei02.cfp.cn/creative/vcg/800/new/VCG41N1208988499.jpg")
        urls.add("https://alifei01.cfp.cn/creative/vcg/800/new/VCG41N1222716030.jpg")
        urls.add("https://alifei03.cfp.cn/creative/vcg/800/version23/VCG41531024222.jpg")
        urls.add("https://tenfei01.cfp.cn/creative/vcg/800/version23/VCG41532151770.jpg")

        val bannerModels = mutableListOf<BannerData>()
        for(i in 0 until 5){
            val model = object : BannerData() {}
            model.url = urls[i]
            bannerModels.add(model)
        }

        val banner = findViewById<YBanner>(R.id.ybanner)
        banner.setIndicator(NumIndicator(this))
        banner.setBannerData(bannerModels)
        banner.setBindAdapter { viewHolder, data, position ->
            ((viewHolder.rootView) as ImageView).loadUrl(data.url)
        }
        banner.setIntervalTime(5000)
        banner.setOnBannerClickListener { viewHolder, data, position ->
            Log.d("YBanner", "YBanner: "+position)
        }

        var j = 0
        for(i in 1..10){
            j++
            Log.v("test", "v:3")
        }
    }
}