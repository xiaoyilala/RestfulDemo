package com.ice.good.lib.ui.banner.core;

/**
 * Banner的数据绑定接口，基于该接口可以实现数据的绑定和框架层解耦
 */
public interface IBindAdapter {
    void onBind(BannerAdapter.BannerViewHolder viewHolder, BannerData data, int position);
}
