package com.ice.good.lib.lib.restful.demo.refreshtest

import android.view.View
import android.view.ViewGroup
import com.ice.good.lib.lib.restful.demo.R
import com.ice.good.lib.ui.item.CommonViewHolder
import com.ice.good.lib.ui.item.YDataItem
import kotlinx.android.synthetic.main.item_refresh_test.*

class DataItem(val name:String, val age:Int):YDataItem<Any, CommonViewHolder>() {
    override fun onBindData(holder: CommonViewHolder, position: Int) {
        val context = holder.itemView.context ?: return

        holder.tv1.text = name
        holder.tv2.text = age.toString()
    }

    override fun getItemLayoutRes(): Int {
        return R.layout.item_refresh_test
    }
}