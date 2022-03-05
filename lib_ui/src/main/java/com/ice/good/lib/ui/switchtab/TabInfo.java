package com.ice.good.lib.ui.switchtab;

public class TabInfo {
    private String name;//底部tab 名称
    private int layoutId;// 布局ID
    private int norTextColor;//未选中文字颜色
    private int selectTextColor;//选中文字颜色
    private int norBg;// tab 未选中背景
    private int selectBg;// tab 选中背景

    public TabInfo(String name, int layoutId, int norTextColor, int selectTextColor, int norBg, int selectBg) {
        this.name = name;
        this.layoutId = layoutId;
        this.norTextColor = norTextColor;
        this.selectTextColor = selectTextColor;
        this.norBg = norBg;
        this.selectBg = selectBg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public int getNorBg() {
        return norBg;
    }

    public void setNorBg(int norBg) {
        this.norBg = norBg;
    }

    public int getSelectBg() {
        return selectBg;
    }

    public void setSelectBg(int selectBg) {
        this.selectBg = selectBg;
    }

    public int getNorTextColor() {
        return norTextColor;
    }

    public void setNorTextColor(int norTextColor) {
        this.norTextColor = norTextColor;
    }

    public int getSelectTextColor() {
        return selectTextColor;
    }

    public void setSelectTextColor(int selectTextColor) {
        this.selectTextColor = selectTextColor;
    }
}
