package com.ice.good.lib.ui.tab.top;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;


import com.ice.good.lib.lib.util.DisplayUtil;
import com.ice.good.lib.ui.tab.common.ITabLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TopTabLayout extends HorizontalScrollView implements ITabLayout<TopTab, TopTabInfo<?>> {

    private List<OnTabSelectedListener> tabSelectedListeners = new ArrayList<>();
    private TopTabInfo<?> selectedTopTabInfo;
    private List<TopTabInfo<?>> infoList;
    private Scroller scroller;

    public TopTabLayout(Context context) {
        this(context, null);
    }

    public TopTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVerticalScrollBarEnabled(false);
        scroller = new Scroller(getContext());
    }

    @Override
    public void inflateInfo(@NonNull List<TopTabInfo<?>> infoList) {
        if(infoList==null || infoList.isEmpty()){
            return;
        }
        this.infoList = infoList;
        selectedTopTabInfo = null;
        LinearLayout linearLayout = getRootLayout(true);
        Iterator<OnTabSelectedListener> iterator = tabSelectedListeners.iterator();
        while (iterator.hasNext()){
            OnTabSelectedListener next = iterator.next();
            if(next instanceof TopTab){
                iterator.remove();
            }
        }
        for(int i=0; i<infoList.size(); i++){
            TopTabInfo<?> topTabInfo = infoList.get(i);
            TopTab topTab = new TopTab(getContext());
            topTab.setTabInfo(topTabInfo);
            linearLayout.addView(topTab);
            tabSelectedListeners.add(topTab);
            topTab.setOnClickListener(v->{
                onSelected(topTabInfo);
            });
        }
    }

    private void onSelected(TopTabInfo<?> topTabInfo) {
        for(OnTabSelectedListener listener:tabSelectedListeners){
            listener.onTabSelected(infoList.indexOf(topTabInfo), selectedTopTabInfo, topTabInfo);
        }
        this.selectedTopTabInfo = topTabInfo;
        autoScroll(topTabInfo);
    }

    private LinearLayout getRootLayout(boolean clear){
        LinearLayout rootView = (LinearLayout) getChildAt(0);
        if(rootView==null){
            rootView = new LinearLayout(getContext());
            rootView.setOrientation(LinearLayout.HORIZONTAL);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            addView(rootView, layoutParams);
        }else if(clear){
            rootView.removeAllViews();
        }
        return rootView;
    }

    @Override
    public void defaultSelected(@NonNull TopTabInfo<?> defaultInfo) {
        onSelected(defaultInfo);
    }

    @Override
    public TopTab findTab(@NonNull TopTabInfo<?> data) {
        LinearLayout ll = getRootLayout(false);
        for(int i=0; i<ll.getChildCount(); i++){
            View child = ll.getChildAt(i);
            if(child instanceof TopTab){
                TopTab topTab = (TopTab) child;
                if(topTab.getTopTabInfo() == data){
                    return topTab;
                }
            }
        }
        return null;
    }

    @Override
    public void addOnTabSelectedListener(OnTabSelectedListener<TopTabInfo<?>> listener) {
        tabSelectedListeners.add(listener);
    }

    /**
     * 自动滚动，实现点击的位置能够自动滚动以展示前后2个
     *
     * @param topTabInfo 点击tab的info
     */
    private void autoScroll(TopTabInfo<?> topTabInfo) {
        TopTab tab = findTab(topTabInfo);
        if(tab==null) return;
        int index = infoList.indexOf(topTabInfo);
        int[] loc = new int[2];
        //获取点击的控件在屏幕的位置
        tab.getLocationInWindow(loc);
        int tabWidth = tab.getWidth();
        int scrollWidth;
        //判断点击了屏幕左侧还是右侧
        if((loc[0]+tabWidth/2) > DisplayUtil.getDisplayWidthInPx(getContext())/2){
            scrollWidth = rangeScrollWidth(index, 2);
        }else{
            scrollWidth = rangeScrollWidth(index, -2);
        }
        int startX = getScrollX();
//        scrollTo(startX + scrollWidth, 0);

        scroller.startScroll(startX, 0, scrollWidth, 0);
    }

    @Override
    public void computeScroll() {
        if(scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    /**
     * 获取可滚动的范围
     *
     * @param index 从第几个开始
     * @param range 向前向后的范围
     * @return 可滚动的范围
     */
    private int rangeScrollWidth(int index, int range){
        int scrollWidth = 0;
        for (int i = 0; i <= Math.abs(range); i++) {
            int next;
            if (range < 0) {
                next = range + i + index;
            } else {
                next = range - i + index;
            }
            if (next >= 0 && next < infoList.size()) {
                if (range < 0) {
                    scrollWidth -= scrollWidth(next, false);
                } else {
                    scrollWidth += scrollWidth(next, true);
                }
            }
        }
        return scrollWidth;
    }

    /**
     * 指定位置的控件可滚动的距离
     *
     * @param index   指定位置的控件
     * @param toRight 是否是点击了屏幕右侧
     * @return 可滚动的距离
     */
    private int scrollWidth(int index, boolean toRight) {
        TopTab tab = findTab(infoList.get(index));
        int tabWidth = tab.getWidth();
        if(tab == null) return 0;
        Rect rect = new Rect();
        tab.getLocalVisibleRect(rect);
        if(toRight){
            if (rect.right > tabWidth) {//right坐标大于控件的宽度时，说明完全没有显示
                return tabWidth;
            } else {//显示部分，减去已显示的宽度
                return tabWidth - rect.right;
            }
        } else {
            if (rect.left <= -tabWidth) {//left坐标小于等于-控件的宽度，说明完全没有显示
                return tabWidth;
            } else if (rect.left > 0) {//显示部分
                return rect.left;
            }
            return 0;
        }
    }
}
