package com.ice.good.lib.lib.restful.demo.knowledge

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.ice.good.lib.lib.restful.demo.R

class FitSystemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fit_system)
        //验证沉浸式状态栏的效果，需要将系统的状态栏改成透明色
        window.statusBarColor = Color.TRANSPARENT

        // 注意点1：仅仅在根布局FrameLayout下设置fitsSystemWindows是没有效果的
        /*<FrameLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#ff66ff"
        android:fitsSystemWindows="true">
        </FrameLayout>*/

        //注意点2：如果把根布局改为 CoordinatorLayout或者ConstraintLayout，可以成功实现沉浸式状态栏效果
        //是因为CoordinatorLayout的源码调用了
        // setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //CoordinatorLayout会对它的子view做出偏移statusBar的距离，不让statusBar挡住控件

        //注意点3：为了解决2的问题，CoordinatorLayout 里的 CollapsingToolbarLayout 可以是控件延伸到状态栏，同时需要
        //CoordinatorLayout CollapsingToolbarLayout 以及 CollapsingToolbarLayout里的子控件都添加fitsSystemWindows属性

        //知识点4：如果不能使用上面的新的控件就手动调用setSystemUiVisibility()
        val frameLayout = findViewById<FrameLayout>(R.id.root_layout)
        frameLayout.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE
                or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        val button = findViewById<Button>(R.id.button)
        ViewCompat.setOnApplyWindowInsetsListener(button) { view, insets ->
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.topMargin = insets.systemWindowInsetTop
            insets
        }
    }
}