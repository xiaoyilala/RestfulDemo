package com.ice.good.lib.ui.refresh;

import android.view.GestureDetector;
import android.view.MotionEvent;
/**
 * 按下(Down)
 * 一扔(Fling)
 * 长按(LongPress)
 * 滚动(Scroll)
 * 触摸反馈(ShowPress)
 * 单击抬起(SingleTapUp)
 *
 * https://juejin.cn/post/6844903874151579662
 * */
public class GestureDetectorListener implements GestureDetector.OnGestureListener {
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
