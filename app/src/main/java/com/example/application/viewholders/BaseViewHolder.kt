package com.example.application.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R

class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val  mText = itemView.findViewById<TextView>(R.id.item_text);

}