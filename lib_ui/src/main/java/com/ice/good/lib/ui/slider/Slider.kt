package com.ice.good.lib.ui.slider

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ice.good.lib.ui.R
import com.ice.good.lib.ui.item.CommonViewHolder
import kotlinx.android.synthetic.main.slider_menu_item.view.*

class Slider @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr:Int = 0):LinearLayout(context, attributeSet, defStyleAttr) {

    private var menuItemAttr: MenuItemAttr
    private val MENU_WIDTH = applyUnit(TypedValue.COMPLEX_UNIT_DIP, 100f)
    private val MENU_HEIGHT = applyUnit(TypedValue.COMPLEX_UNIT_DIP, 45f)
    private val MENU_TEXT_SIZE = applyUnit(TypedValue.COMPLEX_UNIT_SP, 14f)

    private val TEXT_COLOR_NORMAL = Color.parseColor("#666666")
    private val TEXT_COLOR_SELECT = Color.parseColor("#DD3127")

    private val BG_COLOR_NORMAL = Color.parseColor("#F7F8F9")
    private val BG_COLOR_SELECT = Color.parseColor("#ffffff")

    private val MENU_ITEM_LAYOUT_RES_ID = R.layout.slider_menu_item
    private val CONTENT_ITEM_LAYOUT_RES_ID = R.layout.slider_content_item

    private val menuView = RecyclerView(context)
    private val contentView = RecyclerView(context)

    init {
        menuItemAttr = parseMenuItemAttr(attributeSet)

        orientation = HORIZONTAL

        menuView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        menuView.overScrollMode = View.OVER_SCROLL_NEVER
        menuView.itemAnimator = null

        contentView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        contentView.overScrollMode = View.OVER_SCROLL_NEVER
        contentView.itemAnimator = null

        addView(menuView)
        addView(contentView)
    }

    //暴露的方法

    /**
     * 不允许指定LayouManager，默认使用LinearLayoutManager
     * @param layoutResId Menu的item的布局，提供默认值
     * @param itemCount
     * @param onBindView  回调的onBindView
     * @param onItemClick  回调的点击事件
     * */
    fun bindMenuView(layoutResId:Int = MENU_ITEM_LAYOUT_RES_ID,
                     itemCount:Int,
                     onBindView:(CommonViewHolder, Int)->Unit,
                     onItemClick:(CommonViewHolder, Int)->Unit){
        menuView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        menuView.adapter = MenuAdapter(layoutResId, itemCount, onBindView, onItemClick)
    }

    fun bindContentView(layoutResId:Int = CONTENT_ITEM_LAYOUT_RES_ID,
                        itemCount:Int,
                        itemDecoration: RecyclerView.ItemDecoration?,
                        layoutManager: RecyclerView.LayoutManager,
                        onBindView:(CommonViewHolder, Int)->Unit,
                        onItemClick:(CommonViewHolder, Int)->Unit){
        //点击menu 这个方法会多次调用
        if(contentView.layoutManager==null){
            contentView.layoutManager = layoutManager
            contentView.adapter = ContentAdapter(layoutResId)
            itemDecoration?.let {
                contentView.addItemDecoration(it)
            }
        }
        val contentAdapter = contentView.adapter as ContentAdapter
        contentAdapter.update(itemCount, onBindView, onItemClick)
        contentAdapter.notifyDataSetChanged()
        contentView.scrollToPosition(0)
    }

    inner class ContentAdapter(val layoutResId:Int):RecyclerView.Adapter<CommonViewHolder>(){
        private lateinit var onBindView:(CommonViewHolder, Int)->Unit
        private lateinit var onItemClick:(CommonViewHolder, Int)->Unit
        private var count:Int = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
            val itemView = LayoutInflater.from(context).inflate(layoutResId, parent, false)
            val space = width - paddingLeft - paddingRight - menuItemAttr.width
            val layoutManager = (parent as RecyclerView).layoutManager
            var spanCount = 0
            if(layoutManager is GridLayoutManager){
                spanCount = layoutManager.spanCount
            } else if (layoutManager is StaggeredGridLayoutManager) {
                spanCount = layoutManager.spanCount
            }
            if(spanCount>0){
                val itemWidth = space/spanCount

                //创建content itemview  ，设置它的layoutparams 的原因，是防止图片未加载出来之前，列表滑动时 上下闪动的效果
                itemView.layoutParams = RecyclerView.LayoutParams(itemWidth, itemWidth)
            }
            return CommonViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
            onBindView(holder, position)
            holder.itemView.setOnClickListener {
                onItemClick(holder, position)
            }
        }

        override fun getItemCount(): Int {
            return count
        }

        public fun update(itemCount: Int,
                          onBindView:(CommonViewHolder, Int)->Unit,
                          onItemClick:(CommonViewHolder, Int)->Unit){
            this.count = itemCount
            this.onBindView = onBindView
            this.onItemClick = onItemClick
        }
    }

    inner class MenuAdapter(val layoutResId:Int,
                            val count:Int,
                            val onBindView:(CommonViewHolder, Int)->Unit,
                            val onItemClick:(CommonViewHolder, Int)->Unit):RecyclerView.Adapter<CommonViewHolder>(){

        //本次选中的item的位置
        private var currentSelectIndex = 0
        //上一次选中的item的位置
        private var lastSelectIndex = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
            val itemView = LayoutInflater.from(context).inflate(layoutResId, parent, false)
            val params = RecyclerView.LayoutParams(menuItemAttr.width, menuItemAttr.height)
            itemView.layoutParams = params
            itemView.setBackgroundColor(menuItemAttr.normalBackgroundColor)
            itemView.findViewById<TextView>(R.id.slider_menu_item_title)?.setTextColor(menuItemAttr.textColor)
            itemView.findViewById<ImageView>(R.id.slider_menu_item_indicator)?.setImageDrawable(menuItemAttr.indicator)
            return CommonViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
            holder.itemView.setOnClickListener{
                currentSelectIndex = position
                notifyItemChanged(currentSelectIndex)
                notifyItemChanged(lastSelectIndex)
            }

            if(currentSelectIndex == position){
                //写在这里是为了第一次加载的时候 自动点击
                onItemClick(holder, position)
                lastSelectIndex = currentSelectIndex
            }
            applyItemAttr(holder, position)
            onBindView(holder, position)
        }

        private fun applyItemAttr(holder: CommonViewHolder, position: Int) {
            val selected = position == currentSelectIndex
            val titleView:TextView? = holder.itemView.slider_menu_item_title
            val indicatorView:ImageView? = holder.itemView.slider_menu_item_indicator

            indicatorView?.visibility = if(selected) View.VISIBLE else View.GONE
            titleView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, if(selected) menuItemAttr.selectTextSize.toFloat() else menuItemAttr.textSize.toFloat())
            holder.itemView.setBackgroundColor(if (selected) menuItemAttr.selectBackgroundColor else menuItemAttr.normalBackgroundColor)
            titleView?.isSelected = selected
        }

        override fun getItemCount(): Int {
            return count
        }
    }

    private fun parseMenuItemAttr(attributeSet: AttributeSet?): MenuItemAttr {
        val ta =
            context.obtainStyledAttributes(attributeSet, R.styleable.Slider)

        val menuItemHeight = ta.getDimensionPixelOffset(R.styleable.Slider_menuItemHeight, MENU_HEIGHT)
        val menuItemWeight = ta.getDimensionPixelOffset(R.styleable.Slider_menuItemWidth, MENU_WIDTH)
        val menuItemTextSize = ta.getDimensionPixelOffset(R.styleable.Slider_menuItemTextSize, MENU_TEXT_SIZE)
        val menuItemSelectTextSize = ta.getDimensionPixelOffset(R.styleable.Slider_menuItemSelectTextSize, MENU_TEXT_SIZE)

        val menuItemTextColor =
            ta.getColorStateList(R.styleable.Slider_menuItemTextColor) ?: generateColorStateList()

        val menuItemIndicator =
            ta.getDrawable(R.styleable.Slider_menuItemIndicator) ?: ContextCompat.getDrawable(
                context,
                R.drawable.shape_slider_indicator
            )

        val menuItemBackgroundColor = ta.getColor(R.styleable.Slider_menuItemBackgroundColor, BG_COLOR_NORMAL)
        val menuItemBackgroundSelectColor = ta.getColor(R.styleable.Slider_menuItemSelectBackgroundColor, BG_COLOR_SELECT)

        ta.recycle()

        return MenuItemAttr(menuItemWeight,
                    menuItemHeight,
                    menuItemTextColor,
                    menuItemBackgroundSelectColor,
                    menuItemBackgroundColor,
                    menuItemTextSize,
                    menuItemSelectTextSize,
                    menuItemIndicator)
    }

    private fun generateColorStateList():ColorStateList {
        val status = Array(2) { IntArray(2) }
        val colors = IntArray(2)

        colors[0] = TEXT_COLOR_SELECT
        colors[1] = TEXT_COLOR_NORMAL

        status[0] = IntArray(1){android.R.attr.state_selected}
        status[1] = IntArray(1)

        return ColorStateList(status, colors)
    }

    data class MenuItemAttr(val width: Int,
                            val height: Int,
                            val textColor: ColorStateList,
                            val selectBackgroundColor: Int,
                            val normalBackgroundColor: Int,
                            val textSize: Int,
                            val selectTextSize: Int,
                            val indicator: Drawable?)

    private fun applyUnit(unit:Int, value:Float):Int{
        return TypedValue.applyDimension(unit, value, context.resources.displayMetrics).toInt()
    }
}