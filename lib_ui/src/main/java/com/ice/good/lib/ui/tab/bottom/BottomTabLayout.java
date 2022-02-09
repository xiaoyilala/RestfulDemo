package com.ice.good.lib.ui.tab.bottom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ice.good.lib.common.DisplayUtil;
import com.ice.good.lib.common.ViewUtil;
import com.ice.good.lib.ui.R;
import com.ice.good.lib.ui.tab.common.ITabLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BottomTabLayout extends FrameLayout implements ITabLayout<BottomTab, BottomTabInfo<?>> {
    private static final String TAG_TAB_BOTTOM = "TAG_TAB_BOTTOM";
    private List<OnTabSelectedListener<BottomTabInfo<?>>> tabSelectedListeners = new ArrayList<>();
    private List<BottomTabInfo<?>> infoList;
    private BottomTabInfo<?> selectedInfo = null;

    private float bottomAlpha = 1f;
    //TabBottom高度
    private static float bottomHeight = 50;

    //TabBottom的头部线条高度
    private float bottomLineHeight = 0.5f;
    //TabBottom的头部线条颜色
    private String bottomLineColor = "#dfe0e1";

    public BottomTabLayout(@NonNull Context context) {
        this(context, null);
    }

    public BottomTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void inflateInfo(@NonNull List<BottomTabInfo<?>> infoList) {
        if(infoList==null || infoList.isEmpty()){
            return;
        }
        this.infoList = infoList;
        // 移除之前已经添加的View
        for(int i=getChildCount()-1; i>0; i--){
            removeViewAt(i);
        }
        selectedInfo = null;
        addBackground();
        //清除之前添加的HiTabBottom listener，Tips：Java foreach remove问题
        Iterator<OnTabSelectedListener<BottomTabInfo<?>>> iterator = tabSelectedListeners.iterator();
        while ((iterator.hasNext())){
            OnTabSelectedListener<BottomTabInfo<?>> next = iterator.next();
            if(next instanceof BottomTab){
                iterator.remove();
            }
        }
        int height = DisplayUtil.dp2px(bottomHeight);
        FrameLayout fl = new FrameLayout(getContext());
        int displayWidth = DisplayUtil.getDisplayWidthInPx(getContext());
        int width = displayWidth / infoList.size();
        int widthLeft = displayWidth % infoList.size();
        int flLeftPadding = widthLeft/2;
        int flRightPadding = widthLeft - flLeftPadding;
        fl.setPadding(flLeftPadding, 0, flRightPadding, 0);
        fl.setTag(TAG_TAB_BOTTOM);
        for (int i = 0; i < infoList.size(); i++) {
            BottomTabInfo<?> info = infoList.get(i);
            //Tips：为何不用LinearLayout：当动态改变child大小后Gravity.BOTTOM会失效
            LayoutParams params = new LayoutParams(width, height);
            params.gravity = Gravity.BOTTOM;
            params.leftMargin = i*width;

            BottomTab bottomTab = new BottomTab(getContext());
            bottomTab.setTabInfo(info);
            fl.addView(bottomTab, params);

            tabSelectedListeners.add(bottomTab);
            bottomTab.setOnClickListener(v->{
                onSelected(info);
            });
        }
        LayoutParams flPrams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        flPrams.gravity = Gravity.BOTTOM;
        addBottomLine();
        addView(fl, flPrams);

        fixContentView();
    }

    private void onSelected(BottomTabInfo<?> info) {
        for(OnTabSelectedListener<BottomTabInfo<?>> listener:tabSelectedListeners){
            listener.onTabSelected(infoList.indexOf(info), selectedInfo, info);
        }
        this.selectedInfo = info;
    }

    private void addBottomLine() {
        View bottomLine = new View(getContext());
        bottomLine.setBackgroundColor(Color.parseColor(bottomLineColor));

        LayoutParams bottomLineParams =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dp2px(bottomLineHeight, getResources()));
        bottomLineParams.gravity = Gravity.BOTTOM;
        bottomLineParams.bottomMargin = DisplayUtil.dp2px(bottomHeight - bottomLineHeight, getResources());
        addView(bottomLine, bottomLineParams);
        bottomLine.setAlpha(bottomAlpha);
    }

    private void addBackground() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_bottom_layout_bg, null);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dp2px(bottomHeight));
        layoutParams.gravity = Gravity.BOTTOM;
        addView(view, layoutParams);
        view.setAlpha(bottomAlpha);
    }

    @Override
    public void defaultSelected(@NonNull BottomTabInfo<?> defaultInfo) {
        onSelected(defaultInfo);
    }

    @Override
    public BottomTab findTab(@NonNull BottomTabInfo<?> data) {
        ViewGroup fl = findViewWithTag(TAG_TAB_BOTTOM);
        for(int i=0; i<fl.getChildCount(); i++){
            View child = fl.getChildAt(i);
            if(child instanceof BottomTab){
                BottomTab bottomTab = (BottomTab) child;
                if(bottomTab.getTabInfo() == data){
                    return bottomTab;
                }
            }
        }
        return null;
    }

    @Override
    public void addOnTabSelectedListener(OnTabSelectedListener listener) {
        tabSelectedListeners.add(listener);
    }

    public void setTabAlpha(float alpha) {
        this.bottomAlpha = alpha;
    }

    private void fixContentView(){
        if(!(getChildAt(0) instanceof ViewGroup)){
            return;
        }
        ViewGroup rootView = (ViewGroup) getChildAt(0);
        ViewGroup targetView = ViewUtil.findTypeView(rootView, RecyclerView.class);
        if (targetView == null) {
            targetView = ViewUtil.findTypeView(rootView, ScrollView.class);
        }
        if (targetView == null) {
            targetView = ViewUtil.findTypeView(rootView, AbsListView.class);
        }
        if (targetView != null) {
            targetView.setPadding(0, 0, 0, DisplayUtil.dp2px(bottomHeight, getResources()));
            //targetView 的子view在滑动时可绘制到padding处 view内部的padding区也可以显示view
            targetView.setClipToPadding(false);
        }
    }

    public static void clipBottomPadding(ViewGroup targetView) {
        if (targetView != null) {
            targetView.setPadding(0, 0, 0, DisplayUtil.dp2px(bottomHeight));
            targetView.setClipToPadding(false);
        }
    }
}
