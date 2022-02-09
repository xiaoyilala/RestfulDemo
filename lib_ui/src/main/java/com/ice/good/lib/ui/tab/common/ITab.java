package com.ice.good.lib.ui.tab.common;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

public interface ITab<D> extends ITabLayout.OnTabSelectedListener<D>{

    void setTabInfo(@NonNull D data);

    /**
     * 动态修改某个item的大小
     *
     * @param height
     */
    void resetHeight(@Px int height);
}
