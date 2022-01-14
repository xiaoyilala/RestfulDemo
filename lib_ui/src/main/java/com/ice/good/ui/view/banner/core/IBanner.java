package com.ice.good.ui.view.banner.core;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.ice.good.ui.view.banner.indicator.IIndicator;

import java.util.List;

public interface IBanner {
    void setBannerData(@LayoutRes int layoutRedId, @NonNull List<? extends BannerData> models);
    void setBannerData(@NonNull List<? extends BannerData> models);

    void setBindAdapter(IBindAdapter bindAdapter);

    void setIndicator(IIndicator indicator);

    void setAutoPlay(boolean autoPlay);

    void setLoop(boolean loop);

    //页面停留时间
    void setIntervalTime(int intervalTime);

    //页面切换动画的时间
    void setScrollDuration(int duration);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener);

    void setOnBannerClickListener(IBanner.OnBannerClickListener onBannerClickListener);

    interface OnBannerClickListener{
        void onBannerClick(@NonNull BannerAdapter.BannerViewHolder viewHolder, @NonNull BannerData data, int position);
    }
}
