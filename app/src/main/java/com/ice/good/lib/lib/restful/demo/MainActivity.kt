package com.ice.good.lib.lib.restful.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.ice.good.lib.common.loadUrl
import com.ice.good.lib.lib.executor.GoodExecutor
import com.ice.good.lib.lib.fps.FpsMonitor
import com.ice.good.lib.lib.log.YLog
import com.ice.good.lib.lib.log.YLogManager
import com.ice.good.lib.lib.log.base.LogConfig
import com.ice.good.lib.lib.log.printer.ConsolePrinter
import com.ice.good.lib.lib.log.printer.FilePrinter
import com.ice.good.lib.lib.log.printer.ViewPrinter
import com.ice.good.lib.lib.restful.demo.http.ApiFactory
import com.ice.good.lib.lib.restful.demo.http.TestApi
import com.ice.good.lib.lib.restful.Callback
import com.ice.good.lib.lib.restful.Response
import com.ice.good.lib.lib.restful.demo.http.LoginModel
import com.ice.good.lib.lib.restful.demo.tabtest.TabActivity
import com.ice.good.lib.ui.banner.core.BannerData
import com.ice.good.lib.ui.banner.indicator.NumIndicator
import com.ice.good.lib.ui.banner.YBanner
import kotlinx.android.synthetic.main.activity_main.*
import java.util.logging.LogManager

//ripple,Space,merge,include,ViewStub
class MainActivity : AppCompatActivity() {

    private val channelPayListener = View.OnClickListener {
        channel_wx_pay.isChecked = !channel_wx_pay.isChecked
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        channel_wx_pay.setOnClickListener(channelPayListener)

        val viewPrinter = ViewPrinter(this@MainActivity)
        viewPrinter.viewPrinterProvider.showFloatingView()
        YLogManager.getInstance().addPrinter(viewPrinter)


        findViewById<Button>(R.id.btn_tab).setOnClickListener {
            val intent = Intent(this, TabActivity::class.java)
            startActivity(intent)
        }

        val nameTv = findViewById<EditText>(R.id.tv_name)
        val pwTv = findViewById<EditText>(R.id.tv_pw)

        findViewById<TextView>(R.id.tv).setOnClickListener {
            val name = nameTv.text.toString()
            val password = pwTv.text.toString()
            ApiFactory.create(TestApi::class.java).login(name, password).enqueue(object :
                Callback<LoginModel> {
                override fun success(response: Response<LoginModel>) {
                    YLog.d("ApiFactory", "thread："+Thread.currentThread().name)
                    val rawData = response.rawData
                    val errorData = response.errorData
                    val data = response.data.toString()
                    YLog.d("ApiFactory", "rawData: "+rawData)
                    YLog.d("ApiFactory", "errorData: "+errorData)
                    YLog.d("ApiFactory", "data: "+data)
                }

                override fun fail(throwable: Throwable) {
                    YLog.d("ApiFactory", "thread："+Thread.currentThread().name)
                    YLog.d("ApiFactory", "throwable: "+throwable.message)
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
            YLog.d("YBanner", "YBanner: "+position)
        }

        btn_fps.setOnClickListener {
            FpsMonitor.toggle()
        }

        btn_crash.setOnClickListener {
            GoodExecutor.execute(runnable = {
                1/0
            })
        }

        val array1 = Array<Int>(2, { 1 })
        val array2 = Array<Int>(2, { 2 })
        val ints = array1 + array2
        YLog.d("ints", "ints: "+ints)
    }
}