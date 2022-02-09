package com.ice.good.lib.ui.tab.common;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;

public interface ITabLayout<Tab extends ViewGroup, D> {

    void inflateInfo(@NonNull List<D> infoList);

    void defaultSelected(@NonNull D defaultInfo);

    Tab findTab(@NonNull D data);

    void addOnTabSelectedListener(OnTabSelectedListener<D> listener);

    public interface OnTabSelectedListener<D>{
        void onTabSelected(int index, @NonNull D prevInfo, @NonNull D selectedInfo);
    }
}
