package com.ice.good.lib.ui.item

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ice.good.lib.lib.log.YLog
import com.ice.good.lib.ui.R

class YRecyclerView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defStyle:Int=0):RecyclerView(context, attributeSet, defStyle) {
    private var footerView: View?=null
    private var loadingMore:Boolean = false
    private var loadMoreScrollListener:OnScrollListener?=null

    fun enableLoadMore(callback:()->Unit, prefetchSize:Int){
        if(adapter !is YAdapter){
            YLog.e("YRecyclerView enableLoadMore must use yadapter")
            return
        }
        loadMoreScrollListener = LoadMoreScrollListener(prefetchSize, callback)
        addOnScrollListener(loadMoreScrollListener!!)
    }

    fun disableLoadMore(){
        if(adapter !is YAdapter){
            YLog.e("YRecyclerView disableLoadMore must use yadapter")
            return
        }
        val yAdapter = adapter as YAdapter
        footerView?.let {
            if(it.parent !=null){
                yAdapter.removeFooterView(it)
            }
        }

        loadMoreScrollListener?.let {
            removeOnScrollListener(it)
            loadMoreScrollListener = null
            footerView = null
            loadingMore = false
        }
    }

    fun isLoadingMore() = loadingMore

    fun loadMoreFinish(success:Boolean){
        if(adapter !is YAdapter){
            YLog.e("YRecyclerView loadMoreFinish must use yadapter")
            return
        }

        loadingMore = false
        val yAdapter = adapter as YAdapter
        if(!success){
            footerView?.let {
                if(it.parent != null){
                    yAdapter.removeFooterView(it)
                }
            }
        }else{
            //nothing to do .
        }
    }

    inner class LoadMoreScrollListener(val prefetchSize:Int, val callback:()->Unit):OnScrollListener(){

        val yAdapter = adapter as YAdapter

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            //需要根据当前的滑动状态  已决定要不要添加footer view ，要不执行上拉加载分页的动作

            if(loadingMore)
                return

            //判断显示的item的数量
            val itemCount = yAdapter.itemCount
            if(itemCount<=0)
                return

            //在滑动状态为 拖动状态时，就要判断要不要添加footer
            //为了防止列表滑动到底部了但是 footerview 还没显示出来

            //列表是否可以向下滑动
            val canScrollVertically = recyclerView.canScrollVertically(1)

            //特殊情况，咱们的列表已经滑动到底部了，但是分页失败了。
            val lastVisibleItem = findLastVisibleItem(recyclerView)
            val firstVisibleItem = findFirstVisibleItem(recyclerView)
            if (lastVisibleItem <= 0)
                return
            // 列表已经滑动到底部 列表没有撑满屏幕
            val arriveBottom =
                lastVisibleItem >= itemCount - 1 && firstVisibleItem > 0
            //可以向下滑动，或者当前已经滑动到最底下了，此时在拖动列表，那也是允许分页的
            if(newState == RecyclerView.SCROLL_STATE_DRAGGING && (canScrollVertically || arriveBottom)){
                addFooterView()
            }

            //不能在 滑动停止了，才去添加footer view
            if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                return
            }

            //预加载 不需要等待 滑动到最后一个item的时候，就出发下一页的加载动作
            val arrivePrefetchPosition = itemCount - lastVisibleItem <= prefetchSize
            if (!arrivePrefetchPosition) {
                return
            }

            loadingMore = true
            callback()
        }

        private fun addFooterView() {
            val footerView = getFooterView()
            //避免重复添加的情况
            //因为可能调用了removeFooterView，但footerView还没从recyclerView上移除掉
            if(footerView.parent !=null){
                footerView.post {
                    addFooterView()
                }
            }else{
                yAdapter.addFooterView(footerView)
            }
        }

        private fun getFooterView():View{
            if (footerView == null) {
                footerView = LayoutInflater.from(context)
                    .inflate(R.layout.layout_footer_loading, this@YRecyclerView, false)

            }
            return footerView!!
        }

        private fun findFirstVisibleItem(recyclerView: RecyclerView):Int {
            when (val layoutManager = recyclerView.layoutManager) {
                //layoutManager is GridLayoutManager
                is LinearLayoutManager -> {
                    return layoutManager.findFirstVisibleItemPosition()
                }
                is StaggeredGridLayoutManager -> {
                    return layoutManager.findFirstVisibleItemPositions(null)[0]
                }
            }
            return -1
        }

        private fun findLastVisibleItem(recyclerView: RecyclerView):Int {
            when(val layoutManager = recyclerView.layoutManager){
                //GridLayoutManager 继承自 LinearLayoutManager
                is LinearLayoutManager -> {
                    return layoutManager.findLastVisibleItemPosition()
                }
                is StaggeredGridLayoutManager -> {
                    return layoutManager.findLastVisibleItemPositions(null)[0]
                }
            }
            return -1
        }

    }
}