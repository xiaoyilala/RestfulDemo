package com.ice.good.lib.debug

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.Observer
import com.ice.good.lib.ability.YAbility
import com.ice.good.lib.ability.share.ShareBundle
import com.ice.good.lib.lib.util.ActivityManager
import com.ice.good.lib.lib.fps.FpsMonitor
import com.ice.good.lib.lib.util.showToast
import com.ice.good.lib.lib.permission.PermissionConstants
import com.ice.good.lib.lib.permission.PermissionUtil

class DebugTools {

    fun buildVersion():String{
        return "构建版本:" + BuildConfig.VERSION_NAME + ":" + BuildConfig.VERSION_CODE
    }

    fun buildType():String{
        return "构建类型: " + if (BuildConfig.DEBUG) "DEBUG" else "RELEASE"
    }

    fun buildTime():String{
        return "构建时间：" + BuildConfig.BUILD_TIME
    }

    fun deviceInfo():String{
        // 构建版本 ： 品牌-sdk-abi
        return "设备信息:" + Build.BRAND + "-" + Build.VERSION.SDK_INT + "-" + Build.CPU_ABI
    }

    @YDebug(name = "查看Crash日志", desc = "可以一键分享给开发同学，迅速定位偶现问题")
    fun crashLog(context: Context){
        context.startActivity(Intent(context, CrashLogActivity::class.java))
    }

    @YDebug(name = "打开/关闭Fps", desc = "打开后可以查看页面实时的FPS")
    fun toggleFps(context: Context) {
        FpsMonitor.toggle()
    }

    @YDebug(name = "分享到QQ好友", desc = "")
    fun share2QQFriend(context: Context) {

        val shareBundle = ShareBundle()
        shareBundle.title = "测试分享title"
        shareBundle.summary = "测试分享summary"
        shareBundle.appName = "好物App"
        shareBundle.targetUrl = "https://class.imooc.com/sale/mobilearchitect"
        shareBundle.thumbUrl = "https://img2.sycdn.imooc.com/5ece134c0001d50a02400180.jpg"
        shareBundle.channels = listOf("发送给朋友", "添加到微信收藏", "发送给好友", "发送到朋友圈")

        // 微信分享请查看feature/wx_share分支代码
        // 微信分享请查看feature/wx_share分支代码
        // 微信分享请查看feature/wx_share分支代码
        val topActivity = ActivityManager.instance.getTopActivity(true)
        topActivity?.apply {
            YAbility.share(topActivity, shareBundle)
        }
    }

    @YDebug(name = "扫码", desc = "自定义扫码")
    fun go2ScanActivity(context: Activity) {
        PermissionUtil.permission(PermissionConstants.CAMERA)
            .callback(object : PermissionUtil.SimpleCallback {
                override fun onGranted() {
                    YAbility.openScanActivity(context, Observer {
                        showToast(it)
                    })
                }

                override fun onDenied() {
                    showToast("你拒绝使用相机权限,扫码功能无法继续使用.")
                }
            }).request()
    }
}