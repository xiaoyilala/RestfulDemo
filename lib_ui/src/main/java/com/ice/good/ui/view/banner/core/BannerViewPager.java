package com.ice.good.ui.view.banner.core;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

/**
 * 使用RecyclerView + ViewPager 的两个大坑    https://blog.csdn.net/u011002668/article/details/72884893
 * 1. RecyclerView滚动上去，直至ViewPager看不见，再滚动下来，ViewPager下一次切换没有动画
 * 2. 当ViewPage滚动到一半的时候，RecyclerView滚动上去，再滚动下来，ViewPager会卡在一半
 *
 * 问题1原因：ViewPager里有一个私有变量mFirstLayout，它是表示是不是第一次显示布局，如果是true，则使用无动画的方式显示当前item，如果是false，则使用动画方式显示当前item。
 * 当ViewPager滚动上去后，因为RecyclerView的回收机制，ViewPager会走onDetachFromWindow，当再次滚动下来时，ViewPager会走onAttachedToWindow，而问题就出在onAttachToWindow。
 * 在源码onAttachedToWindow里mFirstLayout 会被设置为true
 *
 * 问题2原因：onDetachFromWindow里把动画停了
 * */
public class BannerViewPager extends ViewPager {

    private int intervalTime;
    private boolean autoPlay = true;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            next();
            handler.postDelayed(this, intervalTime);
        }
    };

    public BannerViewPager(@NonNull Context context) {
        super(context);
    }

    public void setAutoPlay(boolean autoPlay){
        this.autoPlay = autoPlay;
        if(!autoPlay){
            stop();
        }
    }

    /**
     * 设置ViewPager的滚动速度
     *
     * @param duration page切换的时间长度
     */
    public void setScrollDuration(int duration){
        if(duration<=0){
            return;
        }
        try {
            Field mScrollerField = ViewPager.class.getDeclaredField("mScroller");
            mScrollerField.setAccessible(true);
            mScrollerField.set(this, new BannerScroller(getContext(), duration));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setIntervalTime(int intervalTime){
        this.intervalTime = intervalTime;
    }

    public void start(){
        handler.removeCallbacksAndMessages(null);
        if(autoPlay){
            handler.postDelayed(runnable, intervalTime);
        }
    }

    public void stop(){
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 设置下一个要显示的item，并返回item的pos
     *
     * @return 下一个要显示item的pos
     */
    private int next(){
        int nextPosition = -1;
        if(getAdapter()==null || getAdapter().getCount()<=1){
            stop();
            return nextPosition;
        }
        nextPosition = getCurrentItem()+1;
        //下一个索引大于等于adapter的view的最大数量时重新开始
        if(nextPosition>=getAdapter().getCount()){
            nextPosition = ((BannerAdapter) getAdapter()).getFirstItemPosition();
        }
        setCurrentItem(nextPosition, true);
        return nextPosition;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                start();
                break;
            default:
                stop();
                break;
        }

        return super.onTouchEvent(ev);
    }

    //修复RecyclerView + ViewPager bug
    private boolean isLayout;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        isLayout = true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(isLayout && getAdapter()!=null && getAdapter().getCount()>0){
            try {
                //fix 使用RecyclerView + ViewPager
                Field mScroller = ViewPager.class.getDeclaredField("mFirstLayout");
                mScroller.setAccessible(true);
                mScroller.set(this, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if(((Activity)getContext()).isFinishing()){
            super.onDetachedFromWindow();
        }
        stop();
    }
}
