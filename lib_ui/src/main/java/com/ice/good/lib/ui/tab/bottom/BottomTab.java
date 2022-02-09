package com.ice.good.lib.ui.tab.bottom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.ice.good.lib.ui.R;
import com.ice.good.lib.ui.tab.common.ITab;

public class BottomTab extends RelativeLayout implements ITab<BottomTabInfo<?>> {

    private BottomTabInfo<?> tabInfo;
    private ImageView tabIv;
    private TextView tabIconTv;
    private TextView tabNameTv;

    public BottomTab(Context context) {
        this(context, null);
    }

    public BottomTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.tab_bottom, this);
        tabIv = findViewById(R.id.iv_image);
        tabIconTv = findViewById(R.id.tv_icon);
        tabNameTv = findViewById(R.id.tv_name);
    }

    public ImageView getTabIv() {
        return tabIv;
    }

    public TextView getTabIconTv() {
        return tabIconTv;
    }

    public TextView getTabNameTv() {
        return tabNameTv;
    }

    public BottomTabInfo<?> getTabInfo() {
        return tabInfo;
    }

    @Override
    public void setTabInfo(@NonNull BottomTabInfo<?> data) {
        this.tabInfo = data;
        inflateInfo(false, true);
    }

    private void inflateInfo(boolean selected, boolean init) {
        if(tabInfo.tabType == BottomTabInfo.TabType.ICON){
            if(init){
                tabIv.setVisibility(View.GONE);
                tabIconTv.setVisibility(View.VISIBLE);
                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), tabInfo.iconFont);
                tabIconTv.setTypeface(typeface);
                if(!TextUtils.isEmpty(tabInfo.name)){
                    tabNameTv.setText(tabInfo.name);
                }
            }
            if(selected){
                tabIconTv.setText(TextUtils.isEmpty(tabInfo.selectedIconName)?tabInfo.defaultIconName:tabInfo.selectedIconName);
                tabIconTv.setTextColor(getTextColor(tabInfo.tintColor));
                tabNameTv.setTextColor(getTextColor(tabInfo.tintColor));
            }else{
                tabIconTv.setText(tabInfo.defaultIconName);
                tabIconTv.setTextColor(getTextColor(tabInfo.defaultColor));
                tabNameTv.setTextColor(getTextColor(tabInfo.defaultColor));
            }
        }else if(tabInfo.tabType == BottomTabInfo.TabType.BITMAP){
            if(init){
                tabIv.setVisibility(View.VISIBLE);
                tabIconTv.setVisibility(View.GONE);
                if(!TextUtils.isEmpty(tabInfo.name)){
                    tabNameTv.setText(tabInfo.name);
                }
            }
            if(selected){
                tabIv.setImageBitmap(tabInfo.selectedBitmap);
            }else{
                tabIv.setImageBitmap(tabInfo.defaultBitmap);
            }
        }
    }

    /**
     * 改变某个tab的高度
     *
     * @param height
     */
    @Override
    public void resetHeight(@Px int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = height;
        setLayoutParams(layoutParams);
        getTabNameTv().setVisibility(View.GONE);
    }

    @Override
    public void onTabSelected(int index, @NonNull BottomTabInfo<?> prevInfo, @NonNull BottomTabInfo<?> selectedInfo) {
        if(prevInfo!=tabInfo && selectedInfo!=tabInfo || prevInfo==selectedInfo){
            return;
        }
        if(prevInfo == tabInfo){
            inflateInfo(false, false);
        }else{
            inflateInfo(true, false);
        }
    }

    private int getTextColor(Object color){
        if(color instanceof String){
            return Color.parseColor((String) color);
        }else{
            return (int)color;
        }
    }
}
