package com.ice.good.lib.debug

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ice.good.lib.lib.util.DisplayUtil
import kotlinx.android.synthetic.main.dialog_debug_tool.*
import java.lang.reflect.Method

class DebugToolDialogFragment:AppCompatDialogFragment() {
    private val debugTools = arrayOf(DebugTools::class.java)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parent = dialog?.window?.findViewById<ViewGroup>(android.R.id.content)?:container
        val view = inflater.inflate(R.layout.dialog_debug_tool, parent, false)
        dialog?.window?.setLayout(
            (DisplayUtil.getDisplayWidthInPx(view.context) * 0.7f).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.shape_debug_tool)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dividerItemDecoration =
            DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                view.context,
                R.drawable.shape_debug_tool_divider
            )!!
        )

        val functions = mutableListOf<DebugFunction>()
        val size = debugTools.size
        for (index in 0 until size) {
            val clazz = debugTools[index]
            val target = clazz.getConstructor().newInstance()
            val declaredMethods = target.javaClass.declaredMethods
            for(method in declaredMethods){
                var title = ""
                var desc = ""
                var enable = false
                val annotation = method.getAnnotation(YDebug::class.java)
                if(annotation!=null){
                    title = annotation.name
                    desc = annotation.desc
                    enable = true
                }else{
                    method.isAccessible = true
                    title = method.invoke(target) as String
                }
                val func = DebugFunction(title, desc, method, enable, target)
                functions.add(func)
            }
        }

        recycler_view.addItemDecoration(dividerItemDecoration)
        recycler_view.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler_view.adapter = DebugToolAdapter(functions)
    }

    inner class DebugToolAdapter(val list: List<DebugFunction>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val itemView = layoutInflater.inflate(R.layout.item_debug_tool_item, parent, false)
            return object : RecyclerView.ViewHolder(itemView) {}
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val debugFunction = list[position]

            val itemTitle = holder.itemView.findViewById<TextView>(R.id.item_title)
            val itemDesc = holder.itemView.findViewById<TextView>(R.id.item_desc)

            itemTitle.text = debugFunction.name
            if (TextUtils.isEmpty(debugFunction.desc)) {
                itemDesc.visibility = View.GONE
            } else {
                itemDesc.visibility = View.VISIBLE
                itemDesc.text = debugFunction.desc
            }

            if (debugFunction.enable) {
                holder.itemView.setOnClickListener {
                    debugFunction.invoke(activity!!)
                    dismiss()
                }
            }
        }

    }

    data class DebugFunction(
        val name:String,
        val desc:String,
        val method:Method,
        val enable:Boolean,
        val target:Any
    ){
        fun invoke(context: Context){
            method.invoke(target, context)
        }
    }
}