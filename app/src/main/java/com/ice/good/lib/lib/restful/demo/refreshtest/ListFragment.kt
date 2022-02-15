package com.ice.good.lib.lib.restful.demo.refreshtest

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ice.good.lib.lib.util.MainHandler
import com.ice.good.lib.lib.executor.GoodExecutor
import com.ice.good.lib.ui.item.AbsListFragment
import kotlin.random.Random

class ListFragment:AbsListFragment() {

    var y=1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableLoadMore {
            loadData()
        }
        loadData();
    }

    private fun loadData() {
        val random = Random(5)
        val nextInt = random.nextInt(1, 4)
        MainHandler.postDelay(nextInt*1000L, runnable = {
            GoodExecutor.execute(runnable = {
                val arrayListOf = arrayListOf<DataItem>()
                for(i in 1..10){
                    val dataItem = DataItem("ice", y++)
                    arrayListOf.add(dataItem)
                }
                MainHandler.post(runnable = {
                    finishRefreshOrLoadMore(arrayListOf)
                })
            })
        })
    }

    override fun onRefresh() {
        super.onRefresh()
        y=1
        loadData()
    }

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(context, 2)
    }


}