package com.ice.good.lib.ui.refresh;

public enum RefreshState {

    /**
     * 初始态
     */
    STATE_INIT,
    /**
     * Header展示的状态
     */
    STATE_VISIBLE,
    /**
     * Header超出可刷新距离的状态
     */
    STATE_OVER,
    /**
     * Header超出刷新位置松开手后的状态
     */
    STATE_OVER_RELEASE,
    /**
     * 刷新中的状态
     */
    STATE_REFRESH

}
