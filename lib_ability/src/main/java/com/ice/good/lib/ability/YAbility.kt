package com.ice.good.lib.ability

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ice.good.lib.ability.push.IPushMessageHandler
import com.ice.good.lib.ability.push.MyPreferences
import com.ice.good.lib.ability.push.UMengPushHelper
import com.ice.good.lib.ability.scan.ScanActivity
import com.ice.good.lib.ability.share.ShareBundle
import com.ice.good.lib.ability.share.ShareManager
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.utils.UMUtils

object YAbility {

    private val scanResultLiveData = MutableLiveData<String>()

    /**
     * 初始化push SDK
     */
    fun initPushSDK(application: Application, iPushMessageHandler: IPushMessageHandler?) {
        //日志开关
        UMConfigure.setLogEnabled(true)
        //预初始化
        UMengPushHelper.preInit(application)
        //是否同意隐私政策
        val agreed: Boolean = MyPreferences.getInstance(application).hasAgreePrivacyAgreement()
        if (!agreed) {
            return
        }
        val isMainProcess = UMUtils.isMainProgress(application)
        if (isMainProcess) {
            //启动优化：建议在子线程中执行初始化
            Thread { UMengPushHelper.init(application, iPushMessageHandler) }.start()
        } else {
            //若不是主进程（":channel"结尾的进程），直接初始化sdk，不可在子线程中执行
            UMengPushHelper.init(application, iPushMessageHandler)
        }
    }

    /**
     * 唤起分享面板
     */
    fun share(context: Context, shareBundle: ShareBundle) {
        ShareManager.share(context, shareBundle)
    }

    /**
     * 打开扫码页,结果通过observer来接收
     */
    fun openScanActivity(activity: Activity, observer: Observer<String>) {
        if (activity is LifecycleOwner) {
            scanResultLiveData.observe(activity, observer)
        }
        activity.startActivity(Intent(activity, ScanActivity::class.java))
    }

    internal fun onScanResult(scanResult: String) {
        scanResultLiveData.postValue(scanResult)
        scanResultLiveData.value = null
    }
}