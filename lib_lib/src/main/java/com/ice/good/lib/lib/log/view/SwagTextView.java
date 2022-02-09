package com.ice.good.lib.lib.log.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.appcompat.widget.AppCompatTextView;


public class SwagTextView extends AppCompatTextView {

    private float lastY;
    private int lastTop;
    private int yDistance;
    private long downEventTime;
    private ClickListener clickListener;

    public SwagTextView(Context context) {
        this(context, null);
    }

    public SwagTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwagTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = event.getY();
                lastTop = getTop();
                downEventTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY();
                yDistance = (int)(moveY - lastY);
                layout(getLeft(), getTop() + yDistance, getRight(), getBottom() + yDistance);

                //修复重新布局后回到原位置的bug
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                if(layoutParams instanceof ViewGroup.MarginLayoutParams){
                    ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin -= yDistance;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(Math.abs(getTop()-lastTop)<=50 && System.currentTimeMillis() - downEventTime < 200 && clickListener !=null){
                    clickListener.onClick();
                }
                break;
        }
        return true;
    }

    public interface ClickListener{
        void onClick();
    }

    public void setClickListener(ClickListener backClickListener) {
        this.clickListener = backClickListener;
    }
}