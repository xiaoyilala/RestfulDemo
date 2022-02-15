package com.ice.good.lib.ui.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ice.good.lib.lib.log.YLog;

public class RefreshLayout extends FrameLayout implements IRefresh {

    private GestureDetector gestureDetector;
    private AutoScroller autoScroller;
    private IRefresh.RefreshListener refreshListener;
    private OverView overView;

    private int lastY;
    private RefreshState state;
    //刷新时是否禁止滚动
    private boolean disableRefreshScroll;

    public RefreshLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public RefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), gestureDetectorListener);
        autoScroller = new AutoScroller();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!autoScroller.isFinish()){
            return false;
        }

        View head = getChildAt(0);
        if(ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_POINTER_INDEX_MASK){
            //松开手
            if(head.getBottom()>0){
                if(state!= RefreshState.STATE_REFRESH){
                    recover(head.getBottom());
                    return false;
                }
            }
            lastY = 0;
        }

        boolean consumed = gestureDetector.onTouchEvent(ev);
        YLog.it("RefreshLayout", "gestureDetector consumed: " + consumed);

        if((consumed || (state != RefreshState.STATE_INIT && state != RefreshState.STATE_REFRESH)) && head.getBottom() != 0){
            ev.setAction(MotionEvent.ACTION_CANCEL);//让父类接受不到真实的事件
            return super.dispatchTouchEvent(ev);
        }

        if(consumed){
            return true;
        }else{
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        View head = getChildAt(0);
        View child = getChildAt(1);

        if (head != null && child != null) {
            YLog.it("RefreshLayout", "onLayout head-height: " + head.getMeasuredHeight());
            int childTop = child.getTop();
            if(state == RefreshState.STATE_REFRESH){
                head.layout(0, overView.refreshMinHeight-head.getMeasuredHeight(), right, overView.refreshMinHeight);
                child.layout(0, overView.refreshMinHeight, right, overView.refreshMinHeight+head.getMeasuredHeight());
            }else{
                head.layout(0, childTop - head.getMeasuredHeight(), right, childTop);
                child.layout(0, childTop, right, childTop + child.getMeasuredHeight());
            }
            View other;
            //让RefreshLayout节点下两个以上的child能够不跟随手势移动以实现一些特殊效果，如悬浮的效果
            for (int i = 2; i < getChildCount(); ++i) {
                other = getChildAt(i);
                other.layout(0, top, right, bottom);
            }
            YLog.it("RefreshLayout", "onLayout head-bottom:" + head.getBottom());
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    private void recover(int dis) {
        if(refreshListener!=null && dis>overView.refreshMinHeight){
            autoScroller.recover(dis - overView.refreshMinHeight);
            state = RefreshState.STATE_OVER_RELEASE;
        }else {
            autoScroller.recover(dis);
        }
    }

    GestureDetectorListener gestureDetectorListener = new GestureDetectorListener(){
        //e1,e2分别是之前DOWN事件和当前的MOVE事件，distanceX和distanceY就是当前MOVE事件和上一个MOVE事件的位移量。
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //distanceY 两次onScroll回调的距离
            //横向滑动，或刷新被禁止则不处理
            if(Math.abs(distanceX) > Math.abs(distanceY) || (refreshListener!=null && !refreshListener.enableRefresh())){
                return false;
            }

            //刷新时是否禁止滑动
            if(state == RefreshState.STATE_REFRESH && disableRefreshScroll){
                return true;
            }

            View head = getChildAt(0);
            View child = ScrollUtil.findScrollableChild(RefreshLayout.this);
            if(ScrollUtil.childScrolled(child)){
                //如果列表发生了滚动则不处理
                return false;
            }

            //没有刷新或没有达到可以刷新的距离，且头部已经划出或下拉
            if((state != RefreshState.STATE_REFRESH || head.getBottom() <= overView.refreshMinHeight) &&
                    (head.getBottom()>0 || distanceY<=0.0F)){
                //还在滑动中
                if(state != RefreshState.STATE_OVER_RELEASE){
                    int speed;
                    //阻尼计算
                    if(child.getTop()<overView.refreshMinHeight){
                        speed = (int)(lastY/overView.minDamp);
                    }else{
                        speed = (int)(lastY/overView.maxDamp);
                    }
                    //如果是正在刷新状态，则不允许在滑动的时候改变状态
                    boolean bool = moveDown(speed, true);
                    lastY = (int) (-distanceY);
                    return bool;
                }
            }
            return false;
        }
    };

    /**
     * 根据偏移量移动header与child
     *
     * @param offsetY 偏移量
     * @param nonAuto 是否非自动滚动触发
     * @return
     */
    private boolean moveDown(int offsetY, boolean nonAuto) {
        YLog.it("RefreshLayout", "111", "changeState:" + nonAuto);

        View head = getChildAt(0);
        View child = getChildAt(1);
        int childTop = child.getTop()+offsetY;

        YLog.it("RefreshLayout","-----", "moveDown head-bottom:" + head.getBottom() + ",child.getTop():" + child.getTop() + ",offsetY:" + offsetY);
        if(childTop<=0){
            //异常情况的补充
            YLog.it("RefreshLayout", "childTop<=0,mState" + state);
            offsetY = -child.getTop();
            //移动head与child的位置，到原始位置
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (state != RefreshState.STATE_REFRESH) {
                state = RefreshState.STATE_INIT;
            }
        }else if(state == RefreshState.STATE_REFRESH && childTop>overView.refreshMinHeight){
            //如果正在下拉刷新中，禁止继续下拉
            return false;
        } else if (childTop <= overView.refreshMinHeight) {
            //还没超出设定的刷新距离
            if(overView.getState() != RefreshState.STATE_VISIBLE && nonAuto){
                //头部开始显示
                overView.onVisible();
                overView.setState(RefreshState.STATE_VISIBLE);
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if(childTop == overView.refreshMinHeight && state == RefreshState.STATE_OVER_RELEASE){
                YLog.it("RefreshLayout", "refresh，childTop：" + childTop);
                refresh();
            }
        } else {
            if(overView.getState() != RefreshState.STATE_OVER && nonAuto){
                //超出刷新位置
                overView.onOver();
                overView.setState(RefreshState.STATE_OVER);
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
        }
        if(overView!=null){
            overView.onScroll(head.getBottom(), overView.refreshMinHeight);
        }

        return false;
    }

    private void refresh(){
        if(refreshListener!=null){
            state = RefreshState.STATE_REFRESH;
            overView.onRefresh();
            overView.setState(RefreshState.STATE_REFRESH);
            refreshListener.onRefresh();
        }
    }


    @Override
    public void setDisableRefreshScroll(boolean disableRefreshScroll) {
        this.disableRefreshScroll = disableRefreshScroll;
    }

    @Override
    public void refreshFinish() {
        final View head = getChildAt(0);
        YLog.i(this.getClass().getSimpleName(), "refreshFinished head-bottom:" + head.getBottom());
        overView.onFinish();
        overView.setState(RefreshState.STATE_INIT);
        int bottom = head.getBottom();
        if(bottom>0){
            recover(bottom);
        }
        state = RefreshState.STATE_INIT;
    }

    @Override
    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    @Override
    public void setRefreshOverView(OverView overView) {
        if(this.overView !=null){
            removeView(this.overView);
        }
        this.overView = overView;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(this.overView, 0, params);
    }

    /**
     * 借助Scroller实现视图的自动滚动
     * https://juejin.im/post/5c7f4f0351882562ed516ab6
     */
    private class AutoScroller implements Runnable{

        private Scroller scroller;
        private int lastY;
        private boolean finish;

        public AutoScroller() {
            scroller = new Scroller(getContext(), new LinearInterpolator());
            finish = true;
        }

        @Override
        public void run() {
            if(scroller.computeScrollOffset()){
                moveDown(lastY - scroller.getCurrY(), false);
                lastY = scroller.getCurrY();
                post(this);
            }else{
                removeCallbacks(this);
                finish = true;
            }
        }

        void recover(int dis){
            if(dis<=0){
                return;
            }
            removeCallbacks(this);
            lastY = 0;
            finish = false;
            scroller.startScroll(0,0,0, dis, 300);
            post(this);
        }

        boolean isFinish(){
            return finish;
        }
    }
}
