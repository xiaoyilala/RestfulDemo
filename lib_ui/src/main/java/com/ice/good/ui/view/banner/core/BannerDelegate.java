package com.ice.good.ui.view.banner.core;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.ice.good.ui.R;
import com.ice.good.ui.view.banner.YBanner;
import com.ice.good.ui.view.banner.indicator.CircleIndicator;
import com.ice.good.ui.view.banner.indicator.IIndicator;

import java.util.List;

public class BannerDelegate implements IBanner, ViewPager.OnPageChangeListener{

    private Context context;
    private YBanner banner;
    private boolean autoPlay;
    private boolean loop;
    private int intervalTime;
    private int scrollDuration;
    private List<? extends BannerData> bannerModels;
    private BannerAdapter adapter;
    private BannerViewPager viewPager;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private IBanner.OnBannerClickListener onBannerClickListener;
    private IIndicator indicator;

    public BannerDelegate(Context context, YBanner banner) {
        this.context = context;
        this.banner = banner;
    }

    public void setAdapter(BannerAdapter bannerAdapter){
        this.adapter = bannerAdapter;
    }

    @Override
    public void setBannerData(int layoutRedId, @NonNull List<? extends BannerData> models) {
        bannerModels = models;
        init(layoutRedId);
    }

    private void init(int layoutRedId) {
        if(adapter ==null){
            adapter = new BannerAdapter(context);
        }
        if(indicator == null){
            indicator = new CircleIndicator(context);
        }
        indicator.onInflate(bannerModels.size());

        adapter.setLayoutResId(layoutRedId);
        adapter.setData(bannerModels);
        adapter.setAutoPlay(autoPlay);
        adapter.setLoop(loop);
        adapter.setBannerClickListener(onBannerClickListener);

        viewPager = new BannerViewPager(context);
        viewPager.setIntervalTime(intervalTime);
        viewPager.setScrollDuration(scrollDuration);
        viewPager.setAutoPlay(autoPlay);
        viewPager.addOnPageChangeListener(this);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        viewPager.setAdapter(adapter);

        if((loop || autoPlay) && adapter.getRealCount()>0){
            int firstItemPosition = adapter.getFirstItemPosition();
            viewPager.setCurrentItem(firstItemPosition, false);
        }

        //清除缓存view
        banner.removeAllViews();
        banner.addView(viewPager, layoutParams);
        banner.addView(indicator.get(), layoutParams);
    }

    @Override
    public void setBannerData(@NonNull List<? extends BannerData> models) {
        setBannerData(R.layout.banner_item_image, models);
    }

    @Override
    public void setBindAdapter(IBindAdapter bindAdapter) {
        if(adapter!=null){
            adapter.setBindAdapter(bindAdapter);
        }
    }

    @Override
    public void setIndicator(IIndicator indicator) {
        this.indicator = indicator;
    }

    @Override
    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
        if(adapter!=null){
            adapter.setAutoPlay(autoPlay);
        }
        if(viewPager!=null){
            viewPager.setAutoPlay(autoPlay);
        }
    }

    @Override
    public void setLoop(boolean loop) {
        this.loop = loop;
        if(adapter!=null){
            adapter.setLoop(loop);
        }
    }

    @Override
    public void setIntervalTime(int intervalTime) {
        if(intervalTime>0){
            this.intervalTime = intervalTime;
        }
        if(viewPager!=null){
            viewPager.setIntervalTime(intervalTime);
        }
    }

    @Override
    public void setScrollDuration(int duration) {
        if(duration>0){
            this.scrollDuration = duration;
            if(viewPager!=null){
                viewPager.setScrollDuration(duration);
            }
        }
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    @Override
    public void setOnBannerClickListener(OnBannerClickListener onBannerClickListener) {
        this.onBannerClickListener = onBannerClickListener;
        if(adapter!=null){
            adapter.setBannerClickListener(onBannerClickListener);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(onPageChangeListener!=null && adapter.getRealCount()>0){
            onPageChangeListener.onPageScrolled(position % adapter.getRealCount(), positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (adapter.getRealCount() <= 0) {
            return;
        }
        position = position % adapter.getRealCount();
        if(onPageChangeListener!=null){
            onPageChangeListener.onPageSelected(position);
        }
        if(indicator!=null){
            indicator.onChange(position, adapter.getRealCount());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(onPageChangeListener!=null){
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }
}
