package com.example.bible

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class MemoAdapter(var context: Context, var memoList : ArrayList<String> , val ItemClick : (String, View) -> Unit) : RecyclerView.Adapter<MemoAdapter.Holder>() {
    val booleanArray = SparseBooleanArray(0)
    var selected = SparseArray<String>(0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_view_memo_item1,parent,false)

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(memoList[position])

        if (booleanArray[position])
            holder.itemView.setBackgroundColor(Color.CYAN)
        else
            holder.itemView.setBackgroundColor(Color.WHITE)
    }

    inner class Holder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val memo = itemView.findViewById<TextView>(R.id.memo)

        fun bind(str : String ){

            memo.text = str

            itemView.setOnClickListener { ItemClick(str, itemView) }
            itemView.setBackgroundColor(Color.WHITE)

            itemView.setOnLongClickListener { if (selected[adapterPosition] == str) {
                booleanArray.put(adapterPosition, false)
                selected.remove(adapterPosition)
                itemView.setBackgroundColor(Color.WHITE)
            } else {
                booleanArray.put(adapterPosition, true)
                selected.put(adapterPosition, str)
                itemView.setBackgroundColor(Color.CYAN)
            }
                println("$booleanArray :: $selected")
                true }
        }
    }
}