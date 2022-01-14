package com.ice.good.ui.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class IconFontTextView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?, defStyle:Int=0):AppCompatTextView(context, attributeSet, defStyle) {
    init {
        val typeface = Typeface.createFromAsset(context.assets, "fonts/iconfont.ttf")
        setTypeface(typeface)
    }
}