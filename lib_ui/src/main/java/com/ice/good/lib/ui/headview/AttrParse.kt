package com.ice.good.lib.ui.headview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import com.ice.good.lib.lib.util.DisplayUtil
import com.ice.good.lib.lib.util.ResUtil
import com.ice.good.lib.ui.R

internal object AttrParse {
    fun parseHeadViewAttrs(context: Context, attributeSet: AttributeSet?, defStyleAttr:Int):Attrs{
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.headViewStyle, typedValue, true)

        val defStyleRes = if(typedValue.resourceId!=0)typedValue.resourceId else R.style.headViewStyle

        // https://blog.lujun.co/2017/05/09/ignored-parameter-defStyleAttr-in-view-construct/
        /* 一个 attribute 值的确定过程大致如下：
         * xml 中查找，若未找到进入第 2 步；
         * xml 中的 style 查找，若未找到进入第 3 步；
         * 若 defStyleAttr 不为 0，由 defStyleAttr 指定的 style 中寻找，若未找到进入第 4 步；
         * 若 defStyleAttr 为 0 或 defStyleAttr 指定的 style 中寻找失败，进入 defStyleRes 指定的 style 中寻找，若寻找失败，进入第 5 步查找；
         * 查找在当前 Theme 中指定的属性值。*/
        val ta = context.obtainStyledAttributes(
            attributeSet, //包含XML中设置的属性
            R.styleable.HeadView,
            defStyleAttr, //为attributeSet中未设置的属性提供默认值，theme中设置的
            defStyleRes// 作为默认的样式，在第三个参数为0或者未找到该属性时，发生作用
        )

        val navIcon = ta.getString(R.styleable.HeadView_nav_icon)
        val navIconColor = ta.getColor(R.styleable.HeadView_nav_icon_color, Color.BLACK)
        val navIconSize = ta.getDimensionPixelSize(R.styleable.HeadView_nav_icon_size, DisplayUtil.sp2px(18f))

        val title = ta.getString(R.styleable.HeadView_title)
        val titleTextSize = ta.getDimensionPixelSize(R.styleable.HeadView_title_text_size, DisplayUtil.sp2px(17f))
        val titleTextSizeWithSubTitle = ta.getDimensionPixelSize(R.styleable.HeadView_title_text_size_with_subTitle, DisplayUtil.sp2px(16f))
        val titleTextColor = ta.getColor(R.styleable.HeadView_title_text_color, ResUtil.getColor(R.color.headView_normal_color))

        val subTitle = ta.getString(R.styleable.HeadView_subtitle)
        val subTitleTextSize = ta.getDimensionPixelSize(R.styleable.HeadView_subTitle_text_size, DisplayUtil.sp2px(14f))
        val subTitleTextColor = ta.getColor(R.styleable.HeadView_subTitle_text_color, ResUtil.getColor(R.color.headView_normal_color))

        val spacePadding = ta.getDimensionPixelSize(R.styleable.HeadView_title_text_size_with_subTitle, 0)

        val btnTextSize = ta.getDimensionPixelSize(R.styleable.HeadView_btn_text_size, DisplayUtil.sp2px(16f))
        val btnTextColor = ta.getColorStateList(R.styleable.HeadView_btn_text_color)

        val lineColor = ta.getColor(R.styleable.HeadView_line_color, Color.parseColor("#eeeeee"))
        val lineHeight = ta.getDimensionPixelSize(R.styleable.HeadView_line_height, 0)

        ta.recycle()
        return Attrs(navIcon, navIconColor, navIconSize.toFloat(),
            title, titleTextSize.toFloat(), titleTextSizeWithSubTitle.toFloat(), titleTextColor,
            subTitle, subTitleTextSize.toFloat(), subTitleTextColor,
            spacePadding,
            btnTextSize.toFloat(), btnTextColor,
            lineColor, lineHeight
        )
    }

    data class Attrs(
        val navIcon:String?,
        val navIconColor:Int,
        val navIconSize:Float,

        val title:String?,
        val titleTextSize:Float,
        val titleTextSizeWithSubTitle:Float,
        val titleTextColor:Int,
        val subTitle:String?,
        val subTitleTextSize:Float,
        val subTitleTextColor:Int,

        val spacePadding:Int,

        val btnTextSize:Float,
        val btnTextColor:ColorStateList?,

        val lineColor:Int,
        val lineHeight:Int

    )
}