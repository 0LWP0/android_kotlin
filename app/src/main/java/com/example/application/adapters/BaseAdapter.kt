package com.example.application.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.application.viewholders.BaseViewHolder
import com.example.application.R

class BaseAdapter(context: Context, list: ArrayList<String>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private val mContext = context;
    private val mList = list;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false)
        return BaseViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (mList.isEmpty()) return 0
        return mList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.mText.text = mList.get(position)
    }
}