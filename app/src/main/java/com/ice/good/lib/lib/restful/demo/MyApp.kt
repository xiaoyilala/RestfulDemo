package com.ice.good.lib.lib.restful.demo

import android.app.Application
import com.google.gson.Gson
import com.ice.good.lib.ability.YAbility
import com.ice.good.lib.lib.util.ActivityManager
import com.ice.good.lib.lib.crash.CrashManager
import com.ice.good.lib.lib.log.YLogManager
import com.ice.good.lib.lib.log.base.LogConfig
import com.ice.good.lib.lib.log.printer.ConsolePrinter
import com.ice.good.lib.lib.log.printer.FilePrinter

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        YLogManager.init(object : LogConfig(){
            override fun injectJsonParser(): JsonParser {
                return JsonParser { Gson().toJson(it) }
            }
        }, ConsolePrinter(), FilePrinter.getInstance(cacheDir.absolutePath, 0))
//        TaskStartUpTest.start()

        ActivityManager.instance.init(this)
        CrashManager.init();
        YAbility.initPushSDK(this, null)
    }
}