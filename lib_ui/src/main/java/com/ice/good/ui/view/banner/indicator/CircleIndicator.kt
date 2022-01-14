package com.ice.good.ui.view.banner.indicator

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import com.ice.good.lib_common.DisplayUtil
import com.ice.good.ui.R

class CircleIndicator @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defStyleAttr:Int=0)
    :FrameLayout(context, attributeSet, defStyleAttr), IIndicator<FrameLayout>{

    companion object {
        private const val VWC = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    @DrawableRes
    private val pointNormal = R.drawable.shape_point_normal

    @DrawableRes
    private val pointSelected = R.drawable.shape_point_select

    /**
     * 指示点左右内间距
     */
    private var pointLeftRightPadding = 0;

    /**
     * 指示点上下内间距
     */
    private var pointTopBottomPadding = 0

    init {
        pointLeftRightPadding = DisplayUtil.dp2px(5f)
        pointTopBottomPadding = DisplayUtil.dp2px(15f)
    }

    override fun get(): FrameLayout {
        return this
    }

    override fun onInflate(count: Int) {
        removeAllViews()
        if(count<=0){
            return
        }
        val groupView = LinearLayout(context)
        groupView.orientation = LinearLayout.HORIZONTAL
        val imageViewParams = LinearLayout.LayoutParams(VWC, VWC);
        imageViewParams.gravity = Gravity.CENTER_VERTICAL
        imageViewParams.setMargins(pointLeftRightPadding, pointTopBottomPadding, pointLeftRightPadding, pointTopBottomPadding)
        var imageView:ImageView
        for(i in 0 until count){
            imageView = ImageView(context)
            imageView.layoutParams = imageViewParams
            if(i==0){
                imageView.setImageResource(pointSelected)
            }else{
                imageView.setImageResource(pointNormal)
            }
            groupView.addView(imageView)
        }
        val groupViewParams = LayoutParams(VWC, VWC)
        groupViewParams.gravity = Gravity.CENTER or Gravity.BOTTOM
        addView(groupView, groupViewParams)
    }

    override fun onChange(current: Int, count: Int) {
        val viewGroup = getChildAt(0) as ViewGroup
        for(i in 0 until count){
            val imageView = viewGroup.getChildAt(i) as ImageView
            if (i == current) {
                imageView.setImageResource(pointSelected)
            } else {
                imageView.setImageResource(pointNormal)
            }
            imageView.requestLayout()
        }
    }

}