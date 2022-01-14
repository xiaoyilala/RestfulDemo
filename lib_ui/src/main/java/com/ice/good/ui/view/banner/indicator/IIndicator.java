package com.ice.good.ui.view.banner.indicator;

import android.view.View;

/**
 * 指示器统一接口
 * ，实现该接口来定义你需要样式的指示器
 *
 */
public interface IIndicator<T extends View> {
    T get();

    /**
     * 初始化Indicator
     *
     * @param count 幻灯片数量
     */
    void onInflate(int count);

    /**
     * 幻灯片切换回调
     *
     * @param current 切换到的幻灯片位置
     * @param count   幻灯片数量
     */
    void onChange(int current, int count);
}
