package com.ice.good.lib.lib.restful.demo.refreshtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ice.good.lib.lib.restful.demo.R

class RefreshTestActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)

        var findFragmentByTag = supportFragmentManager.findFragmentByTag("ListFragment")
        if(findFragmentByTag==null){
            findFragmentByTag = ListFragment()
        }
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.replace(R.id.fl_container, findFragmentByTag, "ListFragment").commitNowAllowingStateLoss()
    }
}