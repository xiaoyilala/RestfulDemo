package com.ice.good.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ice.good.ui.R

class InputItemLayout @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?, defStyle:Int=0):LinearLayout(context, attributeSet, defStyle) {

    private lateinit var titleView: TextView
    private lateinit var editText: EditText
    private var topLine: Line
    private var bottomLine: Line

    private var topPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bottomPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun getTitleView(): TextView {
        return titleView
    }

    fun getEditText(): EditText {
        return editText
    }

    init {
        dividerDrawable = ColorDrawable()
        showDividers = SHOW_DIVIDER_BEGINNING

        orientation = HORIZONTAL

        val a = context.obtainStyledAttributes(attributeSet, R.styleable.InputItemLayout)

        //title
        val title = a.getString(R.styleable.InputItemLayout_title)
        val titleResId = a.getResourceId(R.styleable.InputItemLayout_titleTextAppearance, 0)
        parseTitleStyle(title, titleResId)

        //edittext
        val hint = a.getString(R.styleable.InputItemLayout_hint)
        val inputType = a.getInteger(R.styleable.InputItemLayout_inputType, 0)
        val inputResId = a.getResourceId(R.styleable.InputItemLayout_inputTextAppearance, 0)
        parseInputStyle(hint, inputResId, inputType)

        //上下分割线属性
        val topResId = a.getResourceId(R.styleable.InputItemLayout_topLineAppearance, 0)
        val bottomResId = a.getResourceId(R.styleable.InputItemLayout_bottomLineAppearance, 0)

        topLine = parseLineStyle(topResId)
        bottomLine = parseLineStyle(bottomResId)

        if (topLine.enable) {
            topPaint.color = topLine.color
            topPaint.style = Paint.Style.FILL_AND_STROKE
            topPaint.strokeWidth = topLine.height
        }

        if (bottomLine.enable) {
            bottomPaint.color = bottomLine.color
            bottomPaint.style = Paint.Style.FILL_AND_STROKE
            bottomPaint.strokeWidth = bottomLine.height
        }

        a.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        //巨坑 需要设置 mDivider
        super.onDraw(canvas)

        if (topLine.enable) {
            canvas!!.drawLine(
                topLine.leftMargin,
                0f,
                measuredWidth - topLine.rightMargin,
                0f,
                topPaint
            )
        }

        if (bottomLine.enable) {
            canvas!!.drawLine(
                bottomLine.leftMargin,
                height - bottomLine.height,
                measuredWidth - bottomLine.rightMargin,
                height - bottomLine.height,
                bottomPaint
            )
        }
    }

    private fun parseLineStyle(resId: Int): Line {
        val line = Line()
        val a = context.obtainStyledAttributes(resId, R.styleable.lineAppearance)
        line.color =
            a.getColor(
                R.styleable.lineAppearance_color,
                ContextCompat.getColor(context, R.color.color_d1d2)
            )
        line.height = a.getDimensionPixelOffset(R.styleable.lineAppearance_height, 0).toFloat()
        line.leftMargin =
            a.getDimensionPixelOffset(R.styleable.lineAppearance_leftMargin, 0).toFloat()
        line.rightMargin =
            a.getDimensionPixelOffset(R.styleable.lineAppearance_rightMargin, 0).toFloat()
        line.enable = a.getBoolean(R.styleable.lineAppearance_enable, false)

        a.recycle()

        return line
    }

    inner class Line {
        var color = 0
        var height = 0f
        var leftMargin = 0f
        var rightMargin = 0f;
        var enable: Boolean = false
    }

    private fun parseInputStyle(hint: String?, resId: Int, inputType: Int) {
        val a =
            context.obtainStyledAttributes(resId, R.styleable.inputTextAppearance)

        val hintColor = a.getColor(
            R.styleable.inputTextAppearance_hintColor,
            ContextCompat.getColor(context, R.color.color_d1d2)

        )
        val inputColor = a.getColor(
            R.styleable.inputTextAppearance_inputColor,
            ContextCompat.getColor(context, R.color.color_565)
        )
        //px
        val textSize = a.getDimensionPixelSize(
            R.styleable.inputTextAppearance_textSize,
            applyUnit(TypedValue.COMPLEX_UNIT_SP, 15f)
        )

        val maxInputLength = a.getInteger(R.styleable.InputItemLayout_maxInputLength, 20)


        editText = EditText(context)
        editText.filters = arrayOf(InputFilter.LengthFilter(maxInputLength))//最多可输入的字符数
        editText.setPadding(0,0,0,0)
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        params.weight = 1f
        editText.layoutParams = params

        editText.hint = hint
        editText.setTextColor(inputColor)
        editText.setHintTextColor(hintColor)
        editText.gravity = Gravity.LEFT or (Gravity.CENTER)
        editText.setBackgroundColor(Color.TRANSPARENT)
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())

        /**
         * <enum name="text" value="0"></enum>
         * <enum name="password" value="1"></enum>
         * <enum name="number" value="2"></enum>
         */
        if (inputType == 0){
            editText.inputType = InputType.TYPE_CLASS_TEXT
        } else if (inputType == 1) {
            editText.inputType =
                InputType.TYPE_TEXT_VARIATION_PASSWORD or (InputType.TYPE_CLASS_TEXT)
        } else if (inputType == 2) {
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        addView(editText)

        a.recycle()
    }

    private fun parseTitleStyle(title: String?, resId: Int) {
        val a = context.obtainStyledAttributes(resId, R.styleable.titleTextAppearance)

        val titleColor = a.getColor(
            R.styleable.titleTextAppearance_titleColor,
            resources.getColor(R.color.color_565)
        )

        val titleSize = a.getDimensionPixelSize(
            R.styleable.inputTextAppearance_textSize,
            applyUnit(TypedValue.COMPLEX_UNIT_SP, 15f)
        )

        val minWidth = a.getDimensionPixelOffset(R.styleable.titleTextAppearance_minWidth, 0)


        titleView = TextView(context)
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize.toFloat())
        titleView.setTextColor(titleColor)
        titleView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        titleView.minWidth = minWidth
        titleView.gravity = Gravity.LEFT or Gravity.CENTER
        titleView.text = title

        addView(titleView)

        a.recycle()
    }

    private fun applyUnit(applyUnit: Int, value: Float):Int{
        return TypedValue.applyDimension(applyUnit, value, resources.displayMetrics).toInt()
    }
}