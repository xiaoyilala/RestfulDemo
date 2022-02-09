package com.ice.good.lib.lib.log.printer;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ice.good.lib.common.DisplayUtil;
import com.ice.good.lib.lib.log.view.SwagTextView;

public class ViewPrinterProvider {

    private static final String TAG_FLOATING_VIEW = "TAG_FLOATING_VIEW";
    private static final String TAG_LOG_VIEW = "TAG_LOG_VIEW";

    private FrameLayout rootView;
    private RecyclerView recyclerView;
    private FrameLayout logView;
    private View floatingView;
    private boolean open;

    public ViewPrinterProvider(FrameLayout rootView, RecyclerView recyclerView) {
        this.rootView = rootView;
        this.recyclerView = recyclerView;
    }

    public void showFloatingView(){
        if(rootView.findViewWithTag(TAG_FLOATING_VIEW)!=null){
            return;
        }
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        View floatingView = genFloatingView();
        floatingView.setTag(TAG_FLOATING_VIEW);
        floatingView.setBackgroundColor(Color.BLACK);
        floatingView.setAlpha(0.6f);
        params.bottomMargin = DisplayUtil.dp2px(100);
        rootView.addView(genFloatingView(), params);
    }

    /**
     * 展示LogView
     */
    public void showLogView(){
        if (rootView.findViewWithTag(TAG_LOG_VIEW) != null) {
            return;
        }
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dp2px(160, rootView.getResources()));
        params.gravity = Gravity.BOTTOM;
        View logView = genLogView();
        logView.setTag(TAG_LOG_VIEW);
        rootView.addView(genLogView(), params);
        open = true;
    }

    /**
     * 关闭LogView
     */
    public void closeLogView() {
        open = false;
        rootView.removeView(genLogView());
    }

    private View genLogView(){
        if(logView!=null){
            return logView;
        }
        FrameLayout logView = new FrameLayout(rootView.getContext());
        logView.setBackgroundColor(Color.BLACK);
        logView.addView(recyclerView);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        TextView closeView = new TextView(rootView.getContext());
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeLogView();
            }
        });
        closeView.setText("Close");
        logView.addView(closeView, params);
        return this.logView = logView;
    }

    private View genFloatingView() {
        if(floatingView!=null){
            return floatingView;
        }
        SwagTextView textView = new SwagTextView(rootView.getContext());
        textView.setPadding(6,3,6,6);
        textView.setClickListener(new SwagTextView.ClickListener() {
            @Override
            public void onClick() {
                if(!open){
                    showLogView();
                }
            }
        });
        textView.setText("YLog");
        floatingView = textView;
        return floatingView;
    }
}
