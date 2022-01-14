package com.ice.good.ui.view.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.ice.good.ui.R;
import com.ice.good.ui.view.banner.core.BannerAdapter;
import com.ice.good.ui.view.banner.core.BannerData;
import com.ice.good.ui.view.banner.core.BannerDelegate;
import com.ice.good.ui.view.banner.core.IBanner;
import com.ice.good.ui.view.banner.core.IBindAdapter;
import com.ice.good.ui.view.banner.indicator.IIndicator;

import java.util.List;

public class YBanner extends FrameLayout implements IBanner {

    public BannerDelegate bannerDelegate;

    public YBanner(@NonNull Context context) {
        this(context, null);
    }

    public YBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bannerDelegate = new BannerDelegate(context, this);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.YBanner);
        boolean autoPlay = ta.getBoolean(R.styleable.YBanner_autoPlay, true);
        boolean loop = ta.getBoolean(R.styleable.YBanner_loop, false);
        int intervalTime = ta.getInteger(R.styleable.YBanner_intervalTime, -1);
        setAutoPlay(autoPlay);
        setLoop(loop);
        setIntervalTime(intervalTime);
        ta.recycle();
    }

    @Override
    public void setBannerData(int layoutRedId, @NonNull List<? extends BannerData> models) {
        bannerDelegate.setBannerData(layoutRedId, models);
    }

    @Override
    public void setBannerData(@NonNull List<? extends BannerData> models) {
        bannerDelegate.setBannerData(models);
    }

    @Override
    public void setBindAdapter(IBindAdapter bindAdapter) {
        bannerDelegate.setBindAdapter(bindAdapter);
    }

    @Override
    public void setIndicator(IIndicator indicator) {
        bannerDelegate.setIndicator(indicator);
    }

    @Override
    public void setAutoPlay(boolean autoPlay) {
        bannerDelegate.setAutoPlay(autoPlay);
    }

    @Override
    public void setLoop(boolean loop) {
        bannerDelegate.setLoop(loop);
    }

    @Override
    public void setIntervalTime(int intervalTime) {
        bannerDelegate.setIntervalTime(intervalTime);
    }

    @Override
    public void setScrollDuration(int duration) {
        bannerDelegate.setScrollDuration(duration);
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        bannerDelegate.setOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public void setOnBannerClickListener(OnBannerClickListener onBannerClickListener) {
        bannerDelegate.setOnBannerClickListener(onBannerClickListener);
    }
}
