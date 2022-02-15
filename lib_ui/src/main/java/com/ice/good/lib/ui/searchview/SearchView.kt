package com.ice.good.lib.ui.searchview

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.ice.good.lib.lib.util.MainHandler
import com.ice.good.lib.ui.R
import com.ice.good.lib.ui.icfont.IconFontTextView

class SearchView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defStyleAttr:Int=0):RelativeLayout(context, attributeSet, defStyleAttr) {
    companion object{
        const val LEFT = 1
        const val CENTER = 0

        const val DEBOUNCE_TRIGGER_DURATION = 200L
    }

    private var simpleTextWatcher:SimpleTextWatcher? = null

    var editText:EditText? = null

    //搜索小图标 和 默认提示语 ，以及 container
    private var searchIcon:IconFontTextView?=null
    private var hintTv:TextView?=null
    private var searchIconAndHintContainer:LinearLayout?=null

    //右侧清除小图标
    private var clearIcon: IconFontTextView? = null

    //keyword
    private var keywordContainer: LinearLayout? = null
    private var keywordTv: TextView? = null
    private var keywordClearIcon: IconFontTextView? = null

    private val attrs = AttrsParse.parseSearchViewAttrs(context, attributeSet, defStyleAttr)

    private val debounceRunnable = Runnable {
        if (simpleTextWatcher != null) {
            simpleTextWatcher!!.afterTextChanged(editText?.text)
        }
    }

    init {
        //初始化editText  --create-bind property --addView
        initEditText()
        //初始化右侧一键清楚地小按钮   create-bind property --addview
        initClearIcon()
        //初始化 默认的提示语 和 searchIcon  create-bind property --addview
        initSearchIconHintContainer()

        background = attrs.searchBackground
        editText?.addTextChangedListener(object :SimpleTextWatcher(){
            override fun afterTextChanged(s: Editable?) {
                val hasContent = s?.trim()?.length ?: 0 > 0
                changeVisibility(clearIcon, hasContent)
                changeVisibility(searchIconAndHintContainer, !hasContent)

                if(simpleTextWatcher!=null){
                    MainHandler.remove(debounceRunnable)
                    MainHandler.postDelay(DEBOUNCE_TRIGGER_DURATION, debounceRunnable)
                }
            }
        })
    }

    fun getKeyword(): String? {
        return keywordTv?.text.toString()
    }

    fun setDebounceTextChangedListener(simpleTextWatcher: SimpleTextWatcher) {
        this.simpleTextWatcher = simpleTextWatcher
    }

    fun setHintText(hintText: String) {
        hintTv?.text = hintText
    }

    fun setKeyWord(keyword: String?, listener: OnClickListener){
        makeKeywordContainer()
        toggleSearchViewsVisibility(true)

        editText?.text = null
        keywordTv?.text = keyword
        keywordClearIcon?.setOnClickListener {
            //只有一个参数 且可以推断出来 默认用it代替，可以不写出来
            toggleSearchViewsVisibility(false)
            listener.onClick(it)
        }
    }

    fun setClearIconClickListener(listener: OnClickListener) {
        clearIcon?.setOnClickListener {
            editText?.text = null
            changeVisibility(clearIcon, false)
            changeVisibility(searchIcon, true)
            changeVisibility(hintTv, true)
            changeVisibility(searchIconAndHintContainer, true)
            listener.onClick(it)
        }
    }

    private fun makeKeywordContainer() {
        if(keywordContainer!=null) return
        if(!TextUtils.isEmpty(attrs.keywordClearIcon)){
            keywordClearIcon = IconFontTextView(context, null)
            keywordClearIcon?.setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.keywordSize)
            keywordClearIcon?.setTextColor(attrs.keywordColor)
            keywordClearIcon?.text = attrs.keywordClearIcon
            keywordClearIcon?.id = R.id.id_search_keyword_clear_icon
            keywordClearIcon?.setPadding(
                attrs.spacePadding,
                attrs.spacePadding / 2,
                attrs.spacePadding,
                attrs.spacePadding / 2
            )
        }

        keywordTv = TextView(context)
        keywordTv?.setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.keywordSize)
        keywordTv?.setTextColor(attrs.keywordColor)
        keywordTv?.includeFontPadding = false
        keywordTv?.isSingleLine = true
        keywordTv?.ellipsize = TextUtils.TruncateAt.END
        keywordTv?.filters = arrayOf(InputFilter.LengthFilter(attrs.keywordMaxLen))
        keywordTv?.id = R.id.id_search_keyword_text_view
        keywordTv?.setPadding(
            attrs.spacePadding,
            attrs.spacePadding / 2,
            if (keywordClearIcon == null) attrs.spacePadding else 0,
            attrs.spacePadding / 2
        )

        keywordContainer = LinearLayout(context)
        keywordContainer?.orientation = LinearLayout.HORIZONTAL
        keywordContainer?.gravity = Gravity.CENTER
        keywordContainer?.background = attrs.keywordBackground

        keywordContainer?.addView(
            keywordTv,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        if (keywordClearIcon != null) {
            keywordContainer?.addView(
                keywordClearIcon,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }

        val kwParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        kwParams.addRule(CENTER_VERTICAL)
        kwParams.addRule(ALIGN_PARENT_LEFT)
        kwParams.leftMargin = attrs.spacePadding
        kwParams.rightMargin = attrs.spacePadding
        addView(keywordContainer, kwParams)
    }

    private fun initEditText() {
        editText = EditText(context)
        editText?.apply{
            setTextColor(attrs.searchTextColor)
            setBackgroundColor(Color.TRANSPARENT)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.searchTextSize)
            setPadding(attrs.spacePadding, 0, attrs.spacePadding, 0)
            id = R.id.id_search_edit_view
        }
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        params.addRule(CENTER_VERTICAL)
        addView(editText, params)
    }

    private fun initClearIcon() {
        if(TextUtils.isEmpty(attrs.clearIcon))return
        clearIcon = IconFontTextView(context, null)
        clearIcon?.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.clearIconSize)
            text = attrs.clearIcon
            setTextColor(attrs.searchTextColor)
            setPadding(attrs.spacePadding,attrs.spacePadding,attrs.spacePadding,attrs.spacePadding)
            id = R.id.id_search_clear_icon
        }
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.addRule(CENTER_VERTICAL)
        params.addRule(ALIGN_PARENT_RIGHT)
        addView(clearIcon, params)

        //默认隐藏，只有当输入文字才会显示
        changeVisibility(clearIcon, false)
    }

    private fun initSearchIconHintContainer() {
        //hint view --start
        hintTv = TextView(context)
        hintTv?.setTextColor(attrs.hintTextColor)
        hintTv?.setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.hintTextSize)
        hintTv?.isSingleLine = true
        hintTv?.text = attrs.hintText
        hintTv?.id = R.id.id_search_hint_view
        //hint view --end

        //search icon --start
        searchIcon = IconFontTextView(context, null)
        searchIcon?.setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.searchIconSize)
        searchIcon?.setTextColor(attrs.hintTextColor)
        searchIcon?.text = attrs.searchIcon
        searchIcon?.id = R.id.id_search_icon
        searchIcon?.setPadding(attrs.spacePadding, 0, attrs.spacePadding / 2, 0)
        //search icon --end

        //icon hint container--start
        searchIconAndHintContainer = LinearLayout(context)
        searchIconAndHintContainer?.orientation = LinearLayout.HORIZONTAL
        searchIconAndHintContainer?.gravity = Gravity.CENTER

        searchIconAndHintContainer?.addView(searchIcon)
        searchIconAndHintContainer?.addView(hintTv)

        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.addRule(CENTER_VERTICAL)
        when (attrs.gravity) {
            CENTER -> params.addRule(CENTER_IN_PARENT)
            LEFT -> params.addRule(ALIGN_PARENT_LEFT)
            else -> throw IllegalStateException("not support gravity for now.")
        }
        addView(searchIconAndHintContainer, params)
        //icon hint container--end
    }

    private fun toggleSearchViewsVisibility(showKeyword: Boolean) {
        changeVisibility(editText, !showKeyword)
        changeVisibility(clearIcon, false)
        changeVisibility(searchIconAndHintContainer, !showKeyword)
        changeVisibility(searchIcon, !showKeyword)
        changeVisibility(hintTv, !showKeyword)
        changeVisibility(keywordContainer, showKeyword)
    }

    private fun changeVisibility(view: View?, show: Boolean) {
        view?.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        MainHandler.remove(debounceRunnable)
    }
}