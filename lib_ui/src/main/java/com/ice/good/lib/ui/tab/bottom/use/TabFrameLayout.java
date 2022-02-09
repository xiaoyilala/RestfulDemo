package com.ice.good.lib.ui.tab.bottom.use;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 1.将Fragment的操作内聚
 * 2.提供通用的一些API
 */
public class TabFrameLayout extends FrameLayout {

    private TabViewAdapter adapter;
    private int currentPosition;

    public TabFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public TabFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TabViewAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(TabViewAdapter adapter) {
        if (this.adapter != null || adapter == null) {
            return;
        }
        this.adapter = adapter;
        currentPosition = -1;
    }

    public void setCurrentItem(int position) {
        if (position < 0 || position >= adapter.getCount()) {
            return;
        }
        if (currentPosition != position) {
            currentPosition = position;
            adapter.instantiateItem(this, position);
        }
    }

    public int getCurrentItem() {
        return currentPosition;
    }

    public Fragment getCurrentFragment() {
        if (this.adapter == null) {
            throw new IllegalArgumentException("please call setAdapter first.");
        }
        return adapter.getCurrentFragment();
    }
}
