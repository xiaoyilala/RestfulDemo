package com.ice.good.lib.ui.tab.top;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.ice.good.lib.ui.R;
import com.ice.good.lib.ui.tab.common.ITab;

public class TopTab extends RelativeLayout implements ITab<TopTabInfo<?>> {

    private ImageView tabIv;
    private TextView tabNameTv;
    private View tabIndicator;

    private TopTabInfo<?> topTabInfo;

    public TopTab(Context context) {
        this(context, null);
    }

    public TopTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.tab_top, this);
        tabIv = findViewById(R.id.iv_image);
        tabNameTv = findViewById(R.id.tv_name);
        tabIndicator = findViewById(R.id.tab_top_indicator);
    }

    @Override
    public void setTabInfo(@NonNull TopTabInfo<?> data) {
        this.topTabInfo = data;
        inflateInfo(false, true);
    }

    private void inflateInfo(boolean selected, boolean init) {
        if (topTabInfo.tabType == TopTabInfo.TabType.TXT) {
            if (init) {
                tabNameTv.setVisibility(VISIBLE);
                tabIv.setVisibility(GONE);
                if (!TextUtils.isEmpty(topTabInfo.name)) {
                    tabNameTv.setText(topTabInfo.name);
                }
            }
            if (selected) {
                tabIndicator.setVisibility(VISIBLE);
                tabNameTv.setTextColor(getTextColor(topTabInfo.tintColor));
            } else {
                tabIndicator.setVisibility(GONE);
                tabNameTv.setTextColor(getTextColor(topTabInfo.defaultColor));
            }

        } else if (topTabInfo.tabType == TopTabInfo.TabType.BITMAP) {
            if (init) {
                tabIv.setVisibility(VISIBLE);
                tabNameTv.setVisibility(GONE);
            }
            if (selected) {
                tabIndicator.setVisibility(VISIBLE);
                tabIv.setImageBitmap(topTabInfo.selectedBitmap);
            } else {
                tabIndicator.setVisibility(GONE);
                tabIv.setImageBitmap(topTabInfo.defaultBitmap);
            }
        }
    }

    @Override
    public void resetHeight(int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = height;
        setLayoutParams(layoutParams);
        getTabNameTv().setVisibility(View.GONE);
    }

    @Override
    public void onTabSelected(int index, @NonNull TopTabInfo<?> prevInfo, @NonNull TopTabInfo<?> selectedInfo) {
        if(prevInfo!=topTabInfo && selectedInfo!=topTabInfo || prevInfo == selectedInfo){
            return;
        }
        if(prevInfo == topTabInfo){
            inflateInfo(false, false);
        }else{
            inflateInfo(true, false);
        }
    }

    public ImageView getTabIv() {
        return tabIv;
    }

    public TextView getTabNameTv() {
        return tabNameTv;
    }

    public View getTabIndicator() {
        return tabIndicator;
    }

    public TopTabInfo<?> getTopTabInfo() {
        return topTabInfo;
    }

    @ColorInt
    private int getTextColor(Object color) {
        if (color instanceof String) {
            return Color.parseColor((String) color);
        } else {
            return (int) color;
        }
    }
}
