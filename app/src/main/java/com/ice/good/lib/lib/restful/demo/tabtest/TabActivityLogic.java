package com.ice.good.lib.lib.restful.demo.tabtest;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;

import com.ice.good.lib.lib.restful.demo.tabtest.fragment.CategoryFragment;
import com.ice.good.lib.lib.restful.demo.tabtest.fragment.FavoriteFragment;
import com.ice.good.lib.lib.restful.demo.tabtest.fragment.HomePageFragment;
import com.ice.good.lib.lib.restful.demo.tabtest.fragment.ProfileFragment;
import com.ice.good.lib.lib.restful.demo.tabtest.fragment.RecommendFragment;
import com.ice.good.lib.ui.tab.bottom.BottomTabLayout;
import com.ice.good.lib.ui.tab.bottom.use.TabViewAdapter;
import com.ice.good.lib.ui.tab.bottom.use.TabFrameLayout;
import com.ice.good.lib.ui.tab.common.ITabLayout;
import com.ice.good.lib.ui.tab.bottom.BottomTabInfo;

import com.ice.good.lib.lib.restful.demo.R;

import java.util.ArrayList;
import java.util.List;

public class TabActivityLogic {

    private final static String SAVED_CURRENT_ID = "SAVED_CURRENT_ID";

    private ActivityProvider activityProvider;
    private int currentItemIndex;
    private BottomTabLayout bottomTabLayout;
    private TabFrameLayout tabFrameLayout;
    private List<BottomTabInfo<?>> infoList;

    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SAVED_CURRENT_ID, currentItemIndex);
    }

    public TabActivityLogic(ActivityProvider activityProvider, @Nullable Bundle savedInstanceState) {
        this.activityProvider = activityProvider;
        //fix 不保留活动导致的Fragment重叠问题
        if(savedInstanceState!=null){
            currentItemIndex = savedInstanceState.getInt(SAVED_CURRENT_ID);
        }
        initBottomTab();
    }

    private void initBottomTab() {
        bottomTabLayout = activityProvider.findViewById(R.id.tab_bottom_layout);
        bottomTabLayout.setTabAlpha(0.85f);
        infoList = new ArrayList<BottomTabInfo<?>>();
        int defaultColor = activityProvider.getResources().getColor(R.color.tabBottomDefaultColor);
        int tintColor = activityProvider.getResources().getColor(R.color.tabBottomTintColor);
        BottomTabInfo homeInfo = new BottomTabInfo<Integer>(
                "首页",
                "fonts/iconfont.ttf",
                activityProvider.getString(R.string.if_home),
                null,
                defaultColor,
                tintColor
        );
        homeInfo.fragment = HomePageFragment.class;
        BottomTabInfo infoFavorite = new BottomTabInfo<Integer>(
                "收藏",
                "fonts/iconfont.ttf",
                activityProvider.getString(R.string.if_favorite),
                null,
                defaultColor,
                tintColor
        );
        infoFavorite.fragment = FavoriteFragment.class;
        BottomTabInfo infoCategory = new BottomTabInfo<Integer>(
                "分类",
                "fonts/iconfont.ttf",
                activityProvider.getString(R.string.if_category),
                null,
                defaultColor,
                tintColor
        );
        infoCategory.fragment = CategoryFragment.class;
        BottomTabInfo infoRecommend = new BottomTabInfo<Integer>(
                "推荐",
                "fonts/iconfont.ttf",
                activityProvider.getString(R.string.if_recommend),
                null,
                defaultColor,
                tintColor
        );
        infoRecommend.fragment = RecommendFragment.class;
        BottomTabInfo infoProfile = new BottomTabInfo<Integer>(
                "我的",
                "fonts/iconfont.ttf",
                activityProvider.getString(R.string.if_profile),
                null,
                defaultColor,
                tintColor
        );
        infoProfile.fragment = ProfileFragment.class;
        infoList.add(homeInfo);
        infoList.add(infoFavorite);
        infoList.add(infoCategory);
        infoList.add(infoRecommend);
        infoList.add(infoProfile);
        bottomTabLayout.inflateInfo(infoList);
        initFragmentTabView();
        bottomTabLayout.addOnTabSelectedListener(new ITabLayout.OnTabSelectedListener<BottomTabInfo<?>>() {
            @Override
            public void onTabSelected(int index, @Nullable BottomTabInfo<?> prevInfo, @NonNull BottomTabInfo<?> nextInfo) {
                tabFrameLayout.setCurrentItem(index);
                TabActivityLogic.this.currentItemIndex = index;
            }
        });
        bottomTabLayout.defaultSelected(infoList.get(currentItemIndex));
    }

    private void initFragmentTabView() {
        TabViewAdapter tabViewAdapter = new TabViewAdapter(activityProvider.getSupportFragmentManager(), infoList);
        tabFrameLayout = activityProvider.findViewById(R.id.fragment_tab_view);
        tabFrameLayout.setAdapter(tabViewAdapter);
    }


    public interface ActivityProvider{
        <T extends View> T findViewById(@IdRes int id);

        Resources getResources();

        FragmentManager getSupportFragmentManager();

        String getString(@StringRes int resId);
    }
}
