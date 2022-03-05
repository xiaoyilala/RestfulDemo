package com.ice.good.lib.lib.restful.demo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.ice.good.lib.ability.YAbility
import com.ice.good.lib.ability.push.MyPreferences
import com.ice.good.lib.lib.util.loadUrl
import com.ice.good.lib.lib.executor.GoodExecutor
import com.ice.good.lib.lib.fps.FpsMonitor
import com.ice.good.lib.lib.log.YLog
import com.ice.good.lib.lib.log.YLogManager
import com.ice.good.lib.lib.log.printer.ViewPrinter
import com.ice.good.lib.lib.permission.PermissionConstants
import com.ice.good.lib.lib.permission.PermissionUtil
import com.ice.good.lib.lib.restful.demo.http.ApiFactory
import com.ice.good.lib.lib.restful.demo.http.TestApi
import com.ice.good.lib.lib.restful.Callback
import com.ice.good.lib.lib.restful.Response
import com.ice.good.lib.lib.restful.demo.http.LoginModel
import com.ice.good.lib.lib.restful.demo.knowledge.FitSystemActivity
import com.ice.good.lib.lib.restful.demo.refreshtest.HiRefreshDemoActivity
import com.ice.good.lib.lib.restful.demo.refreshtest.RefreshTestActivity
import com.ice.good.lib.lib.restful.demo.tabtest.TabActivity
import com.ice.good.lib.lib.restful.demo.xy.XYTextActivity
import com.ice.good.lib.lib.util.showToast
import com.ice.good.lib.ui.banner.core.BannerData
import com.ice.good.lib.ui.banner.indicator.NumIndicator
import com.ice.good.lib.ui.banner.YBanner
import com.ice.good.lib.ui.switchtab.TabUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

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

        val tabInfoList = TabUtil.getTabInfoList(this);
        stl.addTabs(tabInfoList);

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

        btn_refresh.setOnClickListener {
            startActivity(Intent(this@MainActivity, RefreshTestActivity::class.java))
        }

        btn_refresh_hi.setOnClickListener {
            startActivity(Intent(this@MainActivity, HiRefreshDemoActivity::class.java))
        }

        btn_xy.setOnClickListener {
            startActivity(Intent(this@MainActivity, XYTextActivity::class.java))
        }

        btn_fit_system.setOnClickListener {
            startActivity(Intent(this@MainActivity, FitSystemActivity::class.java))
        }

        btn_transform.setOnClickListener {
            startActivity(Intent(this@MainActivity, TestTransformActivity::class.java))
        }

        btn_sm.setOnClickListener {
            PermissionUtil.permission(PermissionConstants.CAMERA)
                .callback(object : PermissionUtil.SimpleCallback {
                    override fun onGranted() {
                        YAbility.openScanActivity(this@MainActivity, Observer {
                            val split = it.split(":")
                            if(split!=null && split.size>1){
                                split[0].trim().also { tv_name.setText(split[0].trim()) }
                                split[1].trim().also { tv_pw.setText(split[1].trim()) }
                            }
                        })
                    }

                    override fun onDenied() {
                        showToast("你拒绝使用相机权限,扫码功能无法继续使用.")
                    }
                }).request()
        }

        if (hasAgreedAgreement()) {

        } else {
            showAgreementDialog()
        }
    }

    private fun hasAgreedAgreement(): Boolean {
        return MyPreferences.getInstance(this).hasAgreePrivacyAgreement()
    }

    private fun showAgreementDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.agreement_title)
        builder.setMessage(R.string.agreement_msg)
        builder.setPositiveButton(R.string.agreement_ok,
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                //用户点击隐私协议同意按钮后，初始化PushSDK
                MyPreferences.getInstance(applicationContext).setAgreePrivacyAgreement(true)
                YAbility.initPushSDK(this@MainActivity.application, null)
            })
        builder.setNegativeButton(R.string.agreement_cancel,
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                finish()
            })
        builder.create().show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            if(BuildConfig.DEBUG){
                try {
                    val clazz = Class.forName("com.ice.good.lib.debug.DebugToolDialogFragment")
                    val dialog:DialogFragment = clazz.getConstructor().newInstance() as DialogFragment
                    dialog.show(supportFragmentManager, "debug_tool")
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}