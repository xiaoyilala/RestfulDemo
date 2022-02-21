package com.ice.good.lib.lib.restful.demo.xy;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ParentView extends FrameLayout {
    public ParentView(@NonNull Context context) {
        super(context);
    }

    public ParentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ParentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.e("onLayout", "onLayout");
    }
}
