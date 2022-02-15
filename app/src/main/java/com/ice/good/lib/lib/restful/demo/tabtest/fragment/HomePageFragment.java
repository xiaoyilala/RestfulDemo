package com.ice.good.lib.lib.restful.demo.tabtest.fragment;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ice.good.lib.lib.restful.demo.R;
import com.ice.good.lib.ui.item.BaseFragment;
import com.ice.good.lib.ui.tab.bottom.BottomTabLayout;
import com.ice.good.lib.ui.tab.common.ITabLayout;
import com.ice.good.lib.ui.tab.top.TopTabInfo;
import com.ice.good.lib.ui.tab.top.TopTabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomePageFragment extends BaseFragment {

    private String[] names = new String[]{"推荐","热榜","抗疫","小说","小视频","体育","文化","音乐","棋牌","家居","图片","游戏","精品课","问答"};
    private ViewPager viewPager;
    private TopTabLayout topTabLayout;
    private int topTabSelectIndex;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home_page;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.view_pager);
        topTabLayout = view.findViewById(R.id.top_tab_layout);
        BottomTabLayout.clipBottomPadding(viewPager);
        initData();
    }

    private void initData() {
        int defaultColor = ContextCompat.getColor(getContext(), R.color.color_333);
        int selectedColor = ContextCompat.getColor(getContext(), R.color.color_dd2);
        List<TopTabInfo<?>> tabInfos = new ArrayList<>();
        for(int i=0; i<names.length; i++){
            TopTabInfo<Integer> topTabInfo = new TopTabInfo<Integer>(names[i], defaultColor, selectedColor);
            tabInfos.add(topTabInfo);
        }

        topTabLayout.inflateInfo(tabInfos);
        topTabLayout.addOnTabSelectedListener(new ITabLayout.OnTabSelectedListener<TopTabInfo<?>>() {
            @Override
            public void onTabSelected(int index, @NonNull TopTabInfo<?> prevInfo, @NonNull TopTabInfo<?> selectedInfo) {
                if(viewPager.getCurrentItem()!=index){
                    viewPager.setCurrentItem(index, false);
                }
            }
        });
        viewPager.setAdapter(new HomePagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, names));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position!=topTabSelectIndex){
                    topTabLayout.defaultSelected(tabInfos.get(position));
                    topTabSelectIndex = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        topTabLayout.defaultSelected(tabInfos.get(1));
    }

    static class HomePagerAdapter extends FragmentPagerAdapter{
        private String[] tabNames;
        private SparseArray<Fragment> fragments;

        public HomePagerAdapter(@NonNull FragmentManager fm, int behavior, String[] tabNames) {
            super(fm, behavior);
            this.tabNames = tabNames;
            fragments = new SparseArray<Fragment>(tabNames.length);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position, null);
            if(fragment==null){
                fragment = HomeTabFragment.Companion.newInstance(tabNames[position]);
                fragments.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }
    }
}
