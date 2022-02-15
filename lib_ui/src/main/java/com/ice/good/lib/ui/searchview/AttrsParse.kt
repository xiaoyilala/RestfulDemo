package com.ice.good.lib.ui.searchview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import com.ice.good.lib.lib.util.DisplayUtil
import com.ice.good.lib.lib.util.ResUtil
import com.ice.good.lib.ui.R

internal object AttrsParse {

    fun parseSearchViewAttrs(context: Context, attributeSet: AttributeSet?, defStyleAttrs:Int):Attrs{
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.searchViewStyle,typedValue,true)
        val defStyleRes = if(typedValue.resourceId!=0) typedValue.resourceId else R.style.headViewStyle

        val ta = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.SearchView,
            defStyleAttrs,
            defStyleRes
        )

        //背景
        val searchBackground = ta.getDrawable(R.styleable.SearchView_search_background)
            ?: ResUtil.getDrawable(R.drawable.shape_search_view_bg)
        //搜索图标
        val searchIcon = ta.getString(R.styleable.SearchView_search_icon)
        val searchIconSize = ta.getDimensionPixelSize(
            R.styleable.SearchView_search_icon_size,
            DisplayUtil.sp2px(16f)
        )

        val spacePadding =
            ta.getDimensionPixelSize(R.styleable.SearchView_space_padding, DisplayUtil.sp2px(4f))

        //清除按钮
        val clearIcon = ta.getString(R.styleable.SearchView_clear_icon)
        val clearIconSize = ta.getDimensionPixelSize(
            R.styleable.SearchView_clear_icon_size,
            DisplayUtil.sp2px(16f)
        )

        //提示语
        val hintText = ta.getString(R.styleable.SearchView_hint_text)
        val hintTextSize = ta.getDimensionPixelSize(
            R.styleable.SearchView_hint_text_size,
            DisplayUtil.sp2px(16f)
        )
        val hintTextColor = ta.getColor(
            R.styleable.SearchView_hint_text_color,
            ResUtil.getColor(R.color.searchView_normal_color)
        )

        //相对位置
        val gravity = ta.getInteger(R.styleable.SearchView_hint_gravity, 1)

        //输入文本
        val searchTextSize = ta.getDimensionPixelSize(
            R.styleable.SearchView_search_text_size,
            DisplayUtil.sp2px(16f)
        )
        val searchTextColor = ta.getColor(
            R.styleable.SearchView_search_text_color,
            ResUtil.getColor(R.color.searchView_normal_color)
        )

        //keyword关键词
        val keywordSize = ta.getDimensionPixelSize(
            R.styleable.SearchView_key_word_size,
            DisplayUtil.sp2px(13f)
        )
        val keywordColor = ta.getColor(R.styleable.SearchView_key_word_color, Color.WHITE)
        val keywordMaxLen = ta.getInteger(R.styleable.SearchView_key_word_max_length, 10)
        val keywordBackground = ta.getDrawable(R.styleable.SearchView_key_word_background)

        //关键词清除图标
        val keywordClearIcon = ta.getString(R.styleable.SearchView_clear_icon)
        val keywordClearIconSize = ta.getDimensionPixelSize(
            R.styleable.SearchView_clear_icon_size,
            DisplayUtil.sp2px(12f)
        )

        ta.recycle()

        return Attrs(searchBackground,searchIcon, searchIconSize.toFloat(),
            spacePadding,
            clearIcon, clearIconSize.toFloat(),
            hintText, hintTextSize.toFloat(),hintTextColor,
            gravity,
            searchTextSize.toFloat(),searchTextColor,
            keywordSize.toFloat(),keywordColor,keywordMaxLen,keywordBackground,
            keywordClearIcon, keywordClearIconSize.toFloat()
        )
    }

    data class Attrs(
        /*search view background*/
        val searchBackground: Drawable?,
        /*search icon 🔍*/
        val searchIcon: String?,
        val searchIconSize: Float,
        val spacePadding: Int,
        /*clearIcon*/
        val clearIcon: String?,
        val clearIconSize: Float,
        /*hint*/
        val hintText: String?,
        val hintTextSize: Float,
        val hintTextColor: Int,
        val gravity: Int,
        /*search text*/
        val searchTextSize: Float,
        val searchTextColor: Int,
        /*keyword*/
        val keywordSize: Float,
        val keywordColor: Int,
        val keywordMaxLen: Int,
        val keywordBackground: Drawable?,
        val keywordClearIcon: String?,
        val keywordClearIconSize: Float
    )
}