package com.ice.good.lib.ui.switchtab;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ice.good.lib.ui.R;

import java.util.ArrayList;
import java.util.List;

public class SwitchTabLayout extends LinearLayout {

    private int selectedIndex = 0;
    private List<TabInfo> tabs = new ArrayList<>();
    private OnTabSelectedListener tabSelectedListener;
    private OnClickListener tabClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            TabView tabView = (TabView) v;
            int index = tabView.getIndex();
            if (index == selectedIndex) {
                return;
            }
            setCurrentTab(index);
            if (tabSelectedListener != null) {
                tabSelectedListener.onTabSelected(index);
            }
        }
    };

    public SwitchTabLayout(Context context) {
        this(context, null);
    }

    public SwitchTabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchTabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setHorizontalScrollBarEnabled(false);
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
    }

    private void setCurrentTab(int index) {
        if (index == selectedIndex || index < 0 || index >= getCount()) {
            return;
        }
        final TabView child = (TabView) this.getChildAt(selectedIndex);
        if (child != null) {
            child.setSelectedStatus(false);
        }
        TabView currChild = (TabView) this.getChildAt(index);
        if (currChild != null) {
            currChild.setSelectedStatus(true);
        }
        selectedIndex = index;
    }

    public int getCount(){
        return tabs.size();
    }

    public void addTabs(List<TabInfo> tabInfoList){
        if(tabInfoList!=null){
            tabs.clear();
            tabs.addAll(tabInfoList);
            this.removeAllViews();
            for (int i = 0; i < getCount(); i++) {
                addTab(tabs.get(i), i);
            }
            final TabView child = (TabView) this.getChildAt(selectedIndex);
            if (child == null) {
                return;
            }
            child.setSelectedStatus(true);
        }
    }

    private void addTab(TabInfo tabInfo, int index){
        TabView tabView = new TabView(getContext(), tabInfo, index);
        tabView.setOnClickListener(tabClickListener);
        this.addView(tabView, new LinearLayout.LayoutParams(0,
                MATCH_PARENT, 1));
    }

    private class TabView extends LinearLayout{
        private TabInfo tabInfo;
        private TextView tabNameTv;
        private int index;

        public TabView(Context context, TabInfo tabInfo, int index) {
            this(context, null, tabInfo, index);
        }

        public TabView(Context context, @Nullable AttributeSet attrs, TabInfo tabInfo, int index) {
            this(context, attrs, 0, tabInfo, index);
        }

        public TabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, TabInfo tabInfo, int index) {
            super(context, attrs, defStyleAttr);
            this.tabInfo = tabInfo;
            this.index = index;
            initTabView();
        }

        public TabInfo getTabInfo() {
            return tabInfo;
        }

        public int getIndex() {
            return index;
        }

        /**
         * 更改选中状态
         *
         * @param isSelect
         */
        public void setSelectedStatus(boolean isSelect) {
            if (null == tabNameTv) {
                return;
            }
            if (isSelect) {
                tabNameTv.setTextColor(tabInfo.getSelectTextColor());
                tabNameTv.setBackgroundResource(tabInfo.getSelectBg());
            } else {
                tabNameTv.setTextColor(tabInfo.getNorTextColor());
                tabNameTv.setBackgroundResource(tabInfo.getNorBg());
            }
        }

        private void initTabView() {
            View view = View.inflate(this.getContext(), tabInfo.getLayoutId(), this);
            tabNameTv = view.findViewById(R.id.tab_name);
            if (null != tabNameTv) {
                tabNameTv.setText(tabInfo.getName());
                tabNameTv.setTextColor(tabInfo.getNorTextColor());
                tabNameTv.setBackgroundResource(tabInfo.getNorBg());
            }
        }
    }

    public void setTabSelectedListener(OnTabSelectedListener tabSelectedListener) {
        this.tabSelectedListener = tabSelectedListener;
    }

    /**
     * tab 点击回调接口
     */
    public interface OnTabSelectedListener {
        /**
         * 点击回调方法
         *
         * @param position
         */
        void onTabSelected(int position);
    }
}
