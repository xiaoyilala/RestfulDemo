package com.ice.good.lib.ui.refresh;

public interface IRefresh {

    /**
     * 设置刷新时是否禁止滚动
     * */
    void setDisableRefreshScroll(boolean disableRefreshScroll);

    void refreshFinish();

    void setRefreshListener(IRefresh.RefreshListener refreshListener);

    void setRefreshOverView(OverView overView);

    interface RefreshListener{
        void onRefresh();

        boolean enableRefresh();
    }
}
