package com.ice.good.lib.lib.restful.demo.tabtest.fragment

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ice.good.lib.lib.restful.demo.R
import com.ice.good.lib.ui.item.BaseFragment
import kotlinx.android.synthetic.main.fragment_home_tab.*

class HomeTabFragment: BaseFragment() {

    var globalVisibleRect = Rect()
    var localVisibleRect = Rect()

    companion object{
        fun newInstance(categoryName: String):HomeTabFragment{
            val bundle = Bundle()
            bundle.putString("categoryName", categoryName)
            val homeTabFragment = HomeTabFragment()
            homeTabFragment.arguments = bundle
            return homeTabFragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home_tab;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = arguments?.getString("categoryName", "")
        tv.text = name


        /**
         * 不管view可见与不可见
         * getGlobalVisibleRect 以父布局左上角为原点，
         * view可见时，返回可见区域坐标
         * view不可见时，返回整个view坐标
         *
         * view有可见区域时
         * getLocalVisibleRect 以该view左上角为原点，返回可见区域坐标
         * view不可见时
         * getLocalVisibleRect 以父布局左上角为原点，返回整个view坐标
         * */
        btn1.setOnClickListener {
            v.translationX = 0f
            v.getGlobalVisibleRect(globalVisibleRect)
            v.getLocalVisibleRect(localVisibleRect);
            Log.d("HomeTabFragment", "test globalVisibleRect::" + globalVisibleRect)
            Log.d("HomeTabFragment", "test localVisibleRect::" + localVisibleRect)

            v2.translationX = 0f
            v2.getGlobalVisibleRect(globalVisibleRect)
            v2.getLocalVisibleRect(localVisibleRect);
            Log.d("HomeTabFragment", "test globalVisibleRect::" + globalVisibleRect)
            Log.d("HomeTabFragment", "test localVisibleRect::" + localVisibleRect)
        }
        btn2.setOnClickListener {
            v.translationX = -150f
            v.getGlobalVisibleRect(globalVisibleRect)
            v.getLocalVisibleRect(localVisibleRect);
            Log.d("HomeTabFragment", "test globalVisibleRect::" + globalVisibleRect)
            Log.d("HomeTabFragment", "test localVisibleRect::" + localVisibleRect)

            v2.translationX = 150f
            v2.getGlobalVisibleRect(globalVisibleRect)
            v2.getLocalVisibleRect(localVisibleRect);
            Log.d("HomeTabFragment", "test globalVisibleRect::" + globalVisibleRect)
            Log.d("HomeTabFragment", "test localVisibleRect::" + localVisibleRect)
        }
        btn3.setOnClickListener {
            v.translationX = -500f
            v.getGlobalVisibleRect(globalVisibleRect)
            v.getLocalVisibleRect(localVisibleRect);
            Log.d("HomeTabFragment", "test globalVisibleRect::" + globalVisibleRect)
            Log.d("HomeTabFragment", "test localVisibleRect::" + localVisibleRect)

            v2.translationX = 500f
            v2.getGlobalVisibleRect(globalVisibleRect)
            v2.getLocalVisibleRect(localVisibleRect);
            Log.d("HomeTabFragment", "test globalVisibleRect::" + globalVisibleRect)
            Log.d("HomeTabFragment", "test localVisibleRect::" + localVisibleRect)
        }
    }
}