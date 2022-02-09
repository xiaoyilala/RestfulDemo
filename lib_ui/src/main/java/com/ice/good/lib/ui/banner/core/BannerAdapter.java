package com.ice.good.lib.ui.banner.core;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class BannerAdapter extends PagerAdapter {

    private List<? extends BannerData> models;

    private SparseArray<BannerViewHolder> cacheViews = new SparseArray<>();

    private int layoutResId = -1;
    private Context context;
    private IBanner.OnBannerClickListener bannerClickListener;
    private IBindAdapter bindAdapter;

    /**
     * 是否开启自动轮播
     */
    private boolean autoPlay = true;

    /**
     * 非自动轮播状态下是否可以循环切换
     */
    private boolean loop = false;

    public BannerAdapter(Context context) {
        this.context = context;
    }

    public void setData(@NonNull List<? extends BannerData> data){
        models = data;
        initCacheView();
        notifyDataSetChanged();
    }

    private void initCacheView() {
        cacheViews.clear();
        for(int i=0; i<models.size(); i++){
            BannerViewHolder bannerViewHolder = new BannerViewHolder(createView());
            cacheViews.put(i, bannerViewHolder);
        }
    }

    public void setLayoutResId(@LayoutRes int layoutResId) {
        this.layoutResId = layoutResId;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setBannerClickListener(IBanner.OnBannerClickListener bannerClickListener) {
        this.bannerClickListener = bannerClickListener;
    }

    public void setBindAdapter(IBindAdapter bindAdapter) {
        this.bindAdapter = bindAdapter;
    }

    /**
     * 获取Banner页面数量
     *
     * @return
     */
    public int getRealCount() {
        return models == null ? 0 : models.size();
    }

    /**
     * 获取初次展示的item位置
     */
    public int getFirstItemPosition(){
        return Integer.MAX_VALUE/2 - (Integer.MAX_VALUE/2)%getRealCount();
    }

    private View createView() {
        if(layoutResId == -1){
            throw new IllegalArgumentException("you must be set setLayoutResId first");
        }
        return LayoutInflater.from(context).inflate(layoutResId, null);
    }

    @Override
    public int getCount() {
        return autoPlay ? Integer.MAX_VALUE:(loop ? Integer.MAX_VALUE:getRealCount());
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int realPosition = position;
        if(getRealCount()>0){
            realPosition = position % getRealCount();
        }
        BannerViewHolder viewHolder = cacheViews.get(realPosition);
        if(container.equals(viewHolder.rootView.getParent())){
            container.removeView(viewHolder.rootView);
        }
        if(viewHolder.rootView.getParent()!=null){
            ((ViewGroup)viewHolder.rootView.getParent()).removeView(viewHolder.rootView);
        }
        container.addView(viewHolder.rootView);
        onBind(viewHolder, models.get(realPosition), realPosition);
        return viewHolder.rootView;
    }

    // 如果 Item 的位置如果没有发生变化，则返回 POSITION_UNCHANGED。如果返回了 POSITION_NONE，表示该位置的 Item 已经不存在了。
    // 默认的实现是假设 Item 的位置永远不会发生变化，而返回 POSITION_UNCHANGED。
    @Override
    public int getItemPosition(@NonNull Object object) {
        //返回 POSITION_NONE 该 Item 会被 destroyItem(ViewGroup container, int position, Object object) 方法 remove 掉
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }

    private void onBind(BannerViewHolder viewHolder, BannerData data, int realPosition) {
        viewHolder.rootView.setOnClickListener(v->{
            if(bannerClickListener!=null){
                bannerClickListener.onBannerClick(viewHolder, data, realPosition);
            }
        });
        if(bindAdapter!=null){
            bindAdapter.onBind(viewHolder, data, realPosition);
        }
    }

    public static class BannerViewHolder{
        private SparseArray<View> childViewCaches;

        private View rootView;

        public BannerViewHolder(View rootView) {
            this.rootView = rootView;
        }

        public View getRootView() {
            return rootView;
        }

        public <V extends View> V findViewById(int id){
            if (!(rootView instanceof ViewGroup)) {
                return (V) rootView;
            }
            if(childViewCaches==null){
                childViewCaches = new SparseArray<>(1);
            }
            V childView = (V)childViewCaches.get(id);
            if(childView==null){
                childView = rootView.findViewById(id);
                childViewCaches.put(id, childView);
            }
            return childView;
        }
    }
}
