package com.example.application

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val  mText = itemView.findViewById<TextView>(R.id.item_text);

}