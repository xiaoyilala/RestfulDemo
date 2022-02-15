package com.ice.good.lib.ability.share

import android.content.Context
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ice.good.lib.ability.R
import com.ice.good.lib.lib.util.DisplayUtil
import kotlinx.android.synthetic.main.ability_item_share_item.view.*

internal class ShareDialog(context:Context):AlertDialog(context) {

    private val channels = arrayListOf<ResolveInfo>()
    private var shareChannelClickListener: OnShareClickChannelListener? = null

    fun setChannels(list: List<ResolveInfo>, shareClickChannelListener: OnShareClickChannelListener){
        this.shareChannelClickListener = shareClickChannelListener
        channels.clear()
        if(list!=null) {
            channels.addAll(list)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        recyclerView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        recyclerView.adapter = ShareAdapter()
        recyclerView.setBackgroundResource(R.drawable.ability_share_panel_bg)
        val dp2px = DisplayUtil.dp2px(20f)
        recyclerView.setPadding(dp2px, 0, dp2px, dp2px)
        setContentView(recyclerView)
        window?.setGravity(Gravity.BOTTOM)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    internal inner class ShareAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        private val pm = context.packageManager
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object :RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.ability_item_share_item, parent, false)){}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val resolveInfo = channels[position]
            holder.itemView.share_text.text = resolveInfo.loadLabel(pm)
            holder.itemView.share_icon.setImageDrawable(resolveInfo.loadIcon(pm))
            holder.itemView.setOnClickListener {
                shareChannelClickListener?.onClickChannel(resolveInfo)
                dismiss()
            }
        }

        override fun getItemCount(): Int {
            return channels.size
        }

    }

    interface OnShareClickChannelListener{
        fun onClickChannel(resolveInfo: ResolveInfo)
    }

}