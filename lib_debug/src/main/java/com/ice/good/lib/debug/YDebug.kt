package com.ice.good.lib.debug

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class YDebug(val name:String, val desc:String)
