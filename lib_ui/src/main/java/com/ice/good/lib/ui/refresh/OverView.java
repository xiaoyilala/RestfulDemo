package com.ice.good.lib.ui.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ice.good.lib.lib.util.DisplayUtil;

public abstract class OverView extends FrameLayout {

    protected RefreshState state = RefreshState.STATE_INIT;

    /**
     * 触发刷新的最小高度
     */
    public int refreshMinHeight;
    /**
     * 最小阻尼
     */
    public float minDamp = 1.6f;
    /**
     * 最大阻尼
     */
    public float maxDamp = 2.2f;

    public OverView(@NonNull Context context) {
        super(context);
        preInit();
    }

    public OverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        preInit();
    }

    public OverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        preInit();
    }

    protected void preInit(){
        refreshMinHeight = DisplayUtil.dp2px(66);
        init();
    }

    protected abstract void init();

    protected abstract void onScroll(int scrollY, int refreshMinHeight);

    /**
     * 显示OverView,可提示用户继续下拉
     */
    protected abstract void onVisible();

    /**
     * 超过刷新距离，可提示用户松开刷新
     */
    public abstract void onOver();

    /**
     * 开始加载
     */
    public abstract void onRefresh();

    /**
     * 加载完成
     */
    public abstract void onFinish();

    public RefreshState getState() {
        return state;
    }

    public void setState(RefreshState state) {
        this.state = state;
    }
}
