package com.example.bible

import android.content.Context
import android.graphics.Color
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LineAdapter(val context: Context, private val lineList: ArrayList<String> ) : RecyclerView.Adapter<LineAdapter.Holder>() {
    val booleanArray = SparseBooleanArray(0)
    var selected = SparseArray<String>(0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_view_line_item1, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return lineList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(lineList[position], context)

        if (booleanArray[position])
            holder.itemView.setBackgroundColor(Color.CYAN)
        else
            holder.itemView.setBackgroundColor(Color.rgb(243,243,243))

    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val line = itemView.findViewById<TextView>(R.id.textView)

        fun bind(str: String, context: Context) {
            line.text = str
            val view = itemView

            itemView.setOnClickListener {
                if (selected[adapterPosition] == str) {
                    booleanArray.put(adapterPosition, false)
                    selected.remove(adapterPosition)
                    view.setBackgroundColor(Color.rgb(243,243,243))
                } else {
                    booleanArray.put(adapterPosition, true)
                    selected.put(adapterPosition, str)
                    view.setBackgroundColor(Color.CYAN)

                }
            }
        }
    }
}