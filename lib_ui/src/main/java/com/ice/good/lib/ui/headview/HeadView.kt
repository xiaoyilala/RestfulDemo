package com.ice.good.lib.ui.headview

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.StringRes
import com.ice.good.lib.common.ResUtil
import com.ice.good.lib.ui.R
import com.ice.good.lib.ui.icfont.IconFontButton
import com.ice.good.lib.ui.icfont.IconFontTextView
import java.lang.IllegalStateException
import kotlin.math.max

class HeadView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defStyleAttr: Int=0) : RelativeLayout(context, attributeSet, defStyleAttr){

    //主副标题
    private var titleView:IconFontTextView?=null
    private var subTitleView:IconFontTextView?=null
    private var titleContainer:LinearLayout?=null

    //左右按钮
    private var leftLastViewId = View.NO_ID
    private var rightLastViewId = View.NO_ID
    private val leftViewList = ArrayList<View>()
    private val rightViewList = ArrayList<View>()

    private var attrs = AttrParse.parseHeadViewAttrs(context, attributeSet, defStyleAttr)

    init {
        if(!TextUtils.isEmpty(attrs.title)){
            //1. 生成titleView,设置属性; 2. 生成titleContainer
            setTitle(attrs.title!!)
        }

        if(!TextUtils.isEmpty(attrs.subTitle)){
            //1. 生成titleView,设置属性; 2. 生成titleContainer
            setSubTitle(attrs.subTitle!!)
        }

        if(attrs.lineHeight>0){
            addLineView()
        }

    }

    fun setNavigationIconListener(listener:OnClickListener){
        if(!TextUtils.isEmpty(attrs.navIcon)){
            val navBackView =
                addLeftTextButton(attrs.navIcon!!, R.id.id_head_view_left_back_view)
            navBackView.setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.navIconSize)
            navBackView.setTextColor(attrs.navIconColor)
            navBackView.setOnClickListener(listener)
        }
    }

    fun addLeftTextButton(@StringRes resId: Int, viewId: Int):Button {
        return addLeftTextButton(ResUtil.getString(resId), viewId)
    }

    fun addLeftTextButton(buttonText: String, viewId: Int):Button {
        val button = generateTextButton()
        button.text = buttonText
        button.id = id
        if(leftViewList.isEmpty()){
            button.setPadding(attrs.spacePadding * 2, 0, attrs.spacePadding, 0)
        }else{
            button.setPadding(attrs.spacePadding, 0, attrs.spacePadding, 0)
        }
        addLeftView(button, generateTextButtonLayoutParams())
        return button
    }

    private fun addLeftView(view: View, params: LayoutParams) {
        val viewId = view.id
        if (viewId == View.NO_ID) {
            throw IllegalStateException("left view must has an unique id.")
        }
        if (leftLastViewId == View.NO_ID) {
            params.addRule(ALIGN_PARENT_LEFT, viewId)
        } else {
            params.addRule(RIGHT_OF, leftLastViewId)
        }
        leftLastViewId = viewId
        params.alignWithParent = true  //alignParentIfMissing
        leftViewList.add(view)
        addView(view, params)
    }

    fun setTitle(title: String) {
        makeTitleView()
        titleView?.text = title
        titleView?.visibility = if(TextUtils.isEmpty(title)) View.GONE else View.VISIBLE
    }

    fun setSubTitle(title: String) {
        makeSubTitleView()
        updateTitleViewStyle()
        subTitleView?.text = title
        subTitleView?.visibility = if(TextUtils.isEmpty(title)) View.GONE else View.VISIBLE
    }

    fun setCenterView(view: View) {
        var params = view.layoutParams
        if (params == null) {
            params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        } else if (params !is LayoutParams) {
            params = LayoutParams(params)
        }

        val centerViewParams = params as LayoutParams
        centerViewParams.addRule(RIGHT_OF, leftLastViewId)
        centerViewParams.addRule(LEFT_OF, rightLastViewId)
        params.addRule(CENTER_VERTICAL)
        addView(view, centerViewParams)
    }

    fun addRightTextButton(@StringRes stringRes: Int, viewId: Int): Button {
        return addRightTextButton(ResUtil.getString(stringRes), viewId)
    }

    fun addRightTextButton(buttonText: String, viewId: Int): Button {
        val button = generateTextButton()
        button.text = buttonText
        button.id = viewId
        if (rightViewList.isEmpty()) {
            button.setPadding(attrs.spacePadding, 0, attrs.spacePadding * 2, 0)
        } else {
            button.setPadding(attrs.spacePadding, 0, attrs.spacePadding, 0)
        }

        addRightView(button, generateTextButtonLayoutParams())
        return button
    }

    fun addRightView(
        view: View,
        params: LayoutParams
    ) {
        val viewId = view.id
        if (viewId == View.NO_ID) {
            throw IllegalStateException("right view must has an unique id.")
        }
        if (rightLastViewId == View.NO_ID) {
            params.addRule(ALIGN_PARENT_RIGHT, viewId)
        } else {
            params.addRule(LEFT_OF, rightLastViewId)
        }
        rightLastViewId = viewId
        params.alignWithParent = true  //alignParentIfMissing
        rightViewList.add(view)
        addView(view, params)
    }


    /**
     * 生成titleView
     * */
    private fun makeTitleView() {
        if(titleView==null){
            titleView = IconFontTextView(context, null)
            titleView?.apply{
                gravity = Gravity.CENTER
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
                setTextColor(attrs.titleTextColor)

                updateTitleViewStyle()
                makeTitleContainer()
                titleContainer?.addView(titleView, 0)
            }
        }
    }

    private fun makeSubTitleView() {
        if(subTitleView==null){
            subTitleView = IconFontTextView(context, null)
            subTitleView?.apply{
                gravity = Gravity.CENTER
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
                setTextColor(attrs.subTitleTextColor)

                makeTitleContainer()
                titleContainer?.addView(subTitleView)
            }
        }
    }

    private fun makeTitleContainer() {
        if(titleContainer==null){
            titleContainer = LinearLayout(context)
            titleContainer?.apply{
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER

                val param = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
                param.addRule(CENTER_IN_PARENT)
                this@HeadView.addView(titleContainer, param)
            }
        }
    }

    private fun updateTitleViewStyle() {
        if(titleView!=null){
            if(subTitleView==null || TextUtils.isEmpty(subTitleView!!.text)){
                titleView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.titleTextSize)
                titleView?.typeface = Typeface.DEFAULT_BOLD
            }else{
                titleView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.titleTextSizeWithSubTitle)
                titleView?.typeface = Typeface.DEFAULT
            }
        }
    }

    private fun generateTextButtonLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
    }


    private fun generateTextButton(): Button {
        val button = IconFontButton(context)
        button.setBackgroundResource(0)
        button.minWidth = 0
        button.minimumWidth = 0
        button.minHeight = 0
        button.minimumHeight = 0
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.btnTextSize)
        button.setTextColor(attrs.btnTextColor)
        button.gravity = Gravity.CENTER
        button.includeFontPadding = false
        return button
    }

    private fun addLineView() {
        val view = View(context)
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, attrs.lineHeight)
        layoutParams.addRule(ALIGN_PARENT_BOTTOM)
        view.layoutParams = layoutParams
        view.setBackgroundColor(attrs.lineColor)
        addView(view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(titleContainer!=null){
            //计算出标题栏左侧已占用的空间
            var leftUseSpace = paddingLeft
            for(view in leftViewList){
                leftUseSpace += view.measuredWidth
            }

            //计算出标题栏右侧已占用的空间
            var rightUseSpace = paddingRight
            for(view in rightViewList){
                rightUseSpace += view.measuredWidth
            }

            val titleContainerWidth = titleContainer!!.measuredWidth
            //为了让标题居中，左右空余距离一样
            val remainSpace = measuredWidth - max(leftUseSpace, rightUseSpace) *2
            if(remainSpace < titleContainerWidth){
                val makeMeasureSpec = MeasureSpec.makeMeasureSpec(remainSpace, MeasureSpec.EXACTLY)
                titleContainer!!.measure(makeMeasureSpec, heightMeasureSpec)
            }
        }
    }
}