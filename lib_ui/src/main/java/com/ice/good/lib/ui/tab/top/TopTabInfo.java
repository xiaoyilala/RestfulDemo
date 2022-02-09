package com.ice.good.lib.ui.tab.top;

import android.graphics.Bitmap;

public class TopTabInfo<Color> {
    public enum TabType{
        BITMAP, TXT
    }

    public String name;
    public Bitmap defaultBitmap;
    public Bitmap selectedBitmap;

    public Color defaultColor;
    public Color tintColor;
    public TopTabInfo.TabType tabType;

    public TopTabInfo(String name, Bitmap defaultBitmap, Bitmap selectedBitmap) {
        this.name = name;
        this.defaultBitmap = defaultBitmap;
        this.selectedBitmap = selectedBitmap;
        this.tabType = TabType.BITMAP;
    }

    public TopTabInfo(String name, Color defaultColor, Color tintColor) {
        this.name = name;
        this.defaultColor = defaultColor;
        this.tintColor = tintColor;
        this.tabType = TabType.TXT;
    }
}
