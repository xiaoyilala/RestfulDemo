package com.ice.good.lib.lib.crash

import com.ice.good.lib.lib.util.AppGlobals
import org.devio.`as`.proj.libbreakpad.NativeCrashHandler
import java.io.File

object CrashManager {
    val context = AppGlobals.get()!!
    private const val CRASH_DIR_JAVA = "java_crash"
    private const val CRASH_DIR_NATIVE = "native_crash"

    fun init(){
        val javaCrashDir = getJavaCrashDir()
        val nativeCrashDir = getNativeCrashDir()

        CrashHandler.init(javaCrashDir.absolutePath)
        NativeCrashHandler.init(nativeCrashDir.absolutePath)
    }

    //kotlin的数组相加
    fun getAllCrashFiles() = getJavaCrashDir().listFiles() + getNativeCrashDir().listFiles()

    private fun getJavaCrashDir(): File {
        val javaCrashFile = File(context.cacheDir, CRASH_DIR_JAVA)
        if (!javaCrashFile.exists()) {
            javaCrashFile.mkdirs()
        }
        return javaCrashFile
    }


    private fun getNativeCrashDir(): File {
        val nativeCrashFile = File(context.cacheDir, CRASH_DIR_NATIVE)
        if (!nativeCrashFile.exists()) {
            nativeCrashFile.mkdirs()
        }
        return nativeCrashFile
    }
}