package com.ice.good.lib.ui.item

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class YDataItem<D, VH : RecyclerView.ViewHolder>(data:D?=null) {
    val TAG = "YDataItem"
    var yAdapter:YAdapter?=null
    var mData:D? = null

    init {
        this.mData = data
    }

    /**绑定数据*/
    abstract fun onBindData(holder: VH, position: Int)

    /** YAdapter 中先调用这个，如果返回null 再调用下面两个方法*/
    open fun onCreateViewHolder(parent: ViewGroup): VH? {
        return null
    }

    /**返回该item的布局资源id*/
    open fun getItemLayoutRes():Int{
        return -1
    }

    /**返回该item的view*/
    open fun getItemView(parent:ViewGroup): View?{
        return null
    }

    fun setAdapter(adapter:YAdapter){
        this.yAdapter = adapter
    }

    /**表示item在布局中占用几列*/
    open fun getSpanSize():Int{
        return 0
    }

    /**
     * 该item被滑进屏幕
     */
    open fun onViewAttachedToWindow(holder: VH) {

    }

    /**
     * 该item被滑出屏幕
     */
    open fun onViewDetachedFromWindow(holder: VH) {

    }

    /**
     * 刷新列表
     */
    fun refreshItem() {
        if (yAdapter != null) yAdapter!!.refreshItem(this)
    }

    /**
     * 从列表上移除
     */
    fun removeItem() {
        if (yAdapter != null) yAdapter!!.removeItem(this)
    }
}