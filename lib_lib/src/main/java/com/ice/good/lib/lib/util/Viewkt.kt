package com.ice.good.lib.lib.util

import android.widget.Toast

fun <T> T.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(
    AppGlobals.get()!!,
    message, duration
).show()