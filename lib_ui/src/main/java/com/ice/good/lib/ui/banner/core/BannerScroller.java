package com.ice.good.lib.ui.banner.core;

import android.content.Context;
import android.widget.Scroller;

public class BannerScroller extends Scroller {

    /**
     * 值越大，滑动越慢
     */
    private int duration = 1000;

    public BannerScroller(Context context, int duration) {
        super(context);
        this.duration = duration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, duration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, this.duration);
    }
}
