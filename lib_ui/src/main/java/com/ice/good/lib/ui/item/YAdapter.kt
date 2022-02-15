package com.ice.good.lib.ui.item

import android.content.Context
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType

class YAdapter(context: Context):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mInflater = LayoutInflater.from(context)

    private var dataList = mutableListOf<YDataItem<*, out RecyclerView.ViewHolder>>()

    private var recyclerViewRef:WeakReference<RecyclerView>?=null

    /**
     * 在getItemViewType中调用
     * 存储 YDataItem 的position，key是type  value是position
     *
     * 方便在onCreateViewHolder时拿到对应位置的YDataItem
     * 解决使用databinding时的数据问题
     * */
    private val typePositions = SparseIntArray();

    private var headers = SparseArray<View>()
    private var footers = SparseArray<View>()
    private var BASE_ITEM_TYPE_HEADER = 1000000
    private var BASE_ITEM_TYPE_FOOTER = 2000000

    /**
     * 以每种item类型的class.hashcode为 该item的viewType
     * 这里把type存储起来，是为了onCreateViewHolder方法能够为不同类型的item创建不同的viewholder
     */
    override fun getItemViewType(position: Int): Int {
        if(isHeaderPosition(position)){
            return headers.keyAt(position)
        }

        if(isFooterPosition(position)){
            val footPosition = position - getHeaderSize() - getOriginalItemSize()
            return footers.keyAt(footPosition)
        }

        val itemPosition = position - getHeaderSize()
        val dataItem = dataList[itemPosition]
        val type = dataItem.javaClass.hashCode()

        typePositions.put(type, position)
        return type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(headers.indexOfKey(viewType)>=0){
            val view = headers[viewType]
            return object :RecyclerView.ViewHolder(view){}
        }

        if(footers.indexOfKey(viewType)>=0){
            val view = footers[viewType]
            return object :RecyclerView.ViewHolder(view){}
        }

        val position = typePositions.get(viewType)
        val dataItem = dataList[position]
        val vh = dataItem.onCreateViewHolder(parent)
        if(vh!=null) return vh

        var view = dataItem.getItemView(parent)
        if(view==null){
            val layoutRes = dataItem.getItemLayoutRes()
            if (layoutRes < 0) {
                throw RuntimeException("dataItem:" + dataItem.javaClass.name + " must override getItemView or getItemLayoutRes")
            }
            view = mInflater.inflate(layoutRes, parent, false)
        }
        return createViewHolderInner(dataItem.javaClass, view!!)
    }

    /**
     * 根据YDataItem的泛型类型反射出ViewHolder对象
     * */
    private fun createViewHolderInner(
        javaClass: Class<YDataItem<*, out RecyclerView.ViewHolder>>,
        view: View
    ): RecyclerView.ViewHolder {
        //得到该Item的父类类型,即为HiDataItem.class。  class 也是type的一个子类。
        //type的子类常见的有 class，类泛型,ParameterizedType参数泛型 ，TypeVariable字段泛型
        //所以进一步判断它是不是参数泛型
        val superClass = javaClass.genericSuperclass
        if(superClass is ParameterizedType){
            //得到它携带的泛型参数的数组
            val actualTypeArguments = superClass.actualTypeArguments
            for(argument in actualTypeArguments){
                //isAssignableFrom判断是否是argument的父类
                if(argument is Class<*> && RecyclerView.ViewHolder::class.java.isAssignableFrom(argument)){
                    try {
                        //如果是，则使用反射 实例化类上标记的实际的泛型对象
                        //这里需要  try-catch 一把，如果咱们直接在HiDataItem子类上标记 RecyclerView.ViewHolder，抽象类是不允许反射的
                        return argument.getConstructor(View::class.java).newInstance(view) as RecyclerView.ViewHolder
                    }catch (e:Throwable){
                        e.printStackTrace()
                    }
                }
            }
        }
        return object : CommonViewHolder(view){}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(isHeaderPosition(position) || isFooterPosition(position)){
            return
        }

        val itemPosition = position - getHeaderSize()
        val item = getItem(itemPosition)
        item?.onBindData(holder, itemPosition)
    }

    /**
     * @param position dataList中的索引
     * */
    fun getItem(position: Int):YDataItem<*, RecyclerView.ViewHolder>?{
        if(position<0 || position>=dataList.size){
            return null
        }
        return dataList[position] as YDataItem<*, RecyclerView.ViewHolder>
    }

    override fun getItemCount(): Int {
        return dataList.size+getHeaderSize()+getFooterSize()
    }

    fun addHeadView(view:View){
        if(headers.indexOfValue(view)<0){
            headers.put(BASE_ITEM_TYPE_HEADER++, view)
            notifyItemInserted(headers.size()-1)
        }
    }

    fun removeHeadView(view: View){
        val indexOfValue = headers.indexOfValue(view)
        if(indexOfValue<0)
            return
        headers.removeAt(indexOfValue)
        notifyItemRemoved(indexOfValue)
    }

    fun addFooterView(view: View) {
        if (footers.indexOfValue(view) < 0) {
            footers.put(BASE_ITEM_TYPE_FOOTER++, view)
            notifyItemInserted(itemCount)
        }
    }

    fun removeFooterView(view: View) {
        //0 1  2
        val indexOfValue = footers.indexOfValue(view)
        if (indexOfValue < 0) return
        footers.removeAt(indexOfValue)
        //position代表的是在列表中分位置
        notifyItemRemoved(indexOfValue + getHeaderSize() + getOriginalItemSize())
    }

    fun getHeaderSize(): Int {
        return headers.size()
    }

    fun getFooterSize(): Int {
        return footers.size()
    }

    fun getOriginalItemSize(): Int {
        return dataList.size
    }

    private fun isHeaderPosition(position: Int): Boolean {
        return position < headers.size()
    }

    private fun isFooterPosition(position: Int): Boolean {
        return position >= getHeaderSize() + getOriginalItemSize()
    }

    fun addItemAt(index: Int, dataItem: YDataItem<*, out RecyclerView.ViewHolder>, notify:Boolean){
        if(index>=0){
            dataList.add(index, dataItem)
        }else{
            dataList.add(dataItem)
        }

        val notifyIndex = if(index>=0) index else dataList.size-1
        if(notify){
            notifyItemInserted(notifyIndex)
        }
        dataItem.setAdapter(this)
    }

    /**
     * 往现有集合的尾部逐年items集合
     */
    fun addItems(items:List<YDataItem<*, out RecyclerView.ViewHolder>>, notify:Boolean){
        val start = dataList.size
        items.forEach {
            dataList.add(it)
            it.setAdapter(this@YAdapter)
        }
        if(notify){
            notifyItemRangeChanged(start, items.size)
        }
    }

    /**
     * 从指定位置上移除item
     */
    fun removeItemAt(index:Int):YDataItem<*, out RecyclerView.ViewHolder>?{
        if(index >= 0 && index < dataList.size){
            val removeAt = dataList.removeAt(index)
            notifyItemRemoved(index)
            return removeAt
        }else{
            return null
        }
    }

    /**
     * 移除指定item
     */
    fun removeItem(dataItem: YDataItem<*, out RecyclerView.ViewHolder>) {
        val index: Int = dataList.indexOf(dataItem)
        removeItemAt(index)
    }

    /**
     * 指定刷新 某个item的数据
     */
    fun refreshItem(dataItem: YDataItem<*, out RecyclerView.ViewHolder>) {
        val indexOf = dataList.indexOf(dataItem)
        notifyItemChanged(indexOf)
    }

    fun clearItems(){
        dataList.clear()
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerViewRef = WeakReference(recyclerView)
        /**为列表上的item 适配网格布局*/
        val layoutManager = recyclerView.layoutManager
        if(layoutManager is GridLayoutManager){
            val spanCount = layoutManager.spanCount
            layoutManager.spanSizeLookup = object :GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    if(isHeaderPosition(position) || isFooterPosition(position)){
                        return spanCount
                    }
                    val i = position - getHeaderSize()
                    if(i<dataList.size && i>=0){
                        val item = getItem(i)
                        if(item!=null){
                            val spanSize = item.getSpanSize()
                            return if (spanSize <= 0) spanCount else spanSize
                        }
                    }
                    return spanCount
                }

            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerViewRef?.clear()
    }

    open fun getAttachRecyclerView():RecyclerView?{
        return recyclerViewRef?.get()
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        val recyclerView = getAttachRecyclerView()
        if(recyclerView!=null){
            val position = recyclerView.getChildAdapterPosition(holder.itemView)
            val isHeaderFooter = isHeaderPosition(position) || isFooterPosition(position)
            val itemPosition = position - getHeaderSize()
            val dataItem = getItem(itemPosition) ?: return
            val layoutParams = holder.itemView.layoutParams
            if(layoutParams!=null && layoutParams is StaggeredGridLayoutManager.LayoutParams){
                val manager =
                    recyclerView.layoutManager as StaggeredGridLayoutManager?
                if (isHeaderFooter) {
                    layoutParams.isFullSpan = true
                    return
                }

                val spanSize = dataItem.getSpanSize()
                if (spanSize == manager!!.spanCount) {
                    layoutParams.isFullSpan = true
                }
            }
            dataItem.onViewAttachedToWindow(holder)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        if (isHeaderPosition(position) || isFooterPosition(position))
            return
        val itemPosition = position - getHeaderSize()
        val dataItem = getItem(itemPosition) ?: return
        dataItem.onViewDetachedFromWindow(holder)
    }


}