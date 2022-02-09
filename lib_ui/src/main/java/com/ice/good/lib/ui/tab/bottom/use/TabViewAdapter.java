package com.ice.good.lib.ui.tab.bottom.use;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ice.good.lib.ui.tab.bottom.BottomTabInfo;

import java.util.List;

public class TabViewAdapter {
    private List<BottomTabInfo<?>> infoList;
    private Fragment currentFragment;
    private FragmentManager fragmentManager;

    public TabViewAdapter(FragmentManager fragmentManager, List<BottomTabInfo<?>> infoList) {
        this.infoList = infoList;
        this.fragmentManager = fragmentManager;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void instantiateItem(View container, int position) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(currentFragment!=null){
            transaction.hide(currentFragment);
        }
        String name = container.getId() + ":" + position;
        Fragment fragment = fragmentManager.findFragmentByTag(name);
        if(fragment!=null){
            transaction.show(fragment);
        }else{
            fragment = getItem(position);
            if(!fragment.isAdded()){
                transaction.add(container.getId(), fragment, name);
            }
        }
        currentFragment = fragment;
        transaction.commitNowAllowingStateLoss();
    }

    private Fragment getItem(int position) {
        try {
            return (Fragment) infoList.get(position).fragment.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getCount() {
        return infoList == null ? 0 : infoList.size();
    }
}
