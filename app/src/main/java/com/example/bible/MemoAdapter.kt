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

class MemoAdapter(
    var context: Context,
    var memoList: ArrayList<String>,
    var memoId: ArrayList<String>,
    val ItemClick: (String, String, View) -> Unit,
    val ItemLongClick: (String, String, Int, View) -> Boolean
) : RecyclerView.Adapter<MemoAdapter.Holder>() {
    val booleanArray = SparseBooleanArray(0)
    var selectedStr = SparseArray<String>(0)
    var selectedId = SparseArray<String>(0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_view_memo_item1, parent, false)

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(memoList[position], memoId[position])

        if (booleanArray[position])
            holder.itemView.setBackgroundColor(Color.CYAN)
        else
            holder.itemView.setBackgroundColor(Color.rgb(243, 243, 243))
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val memo = itemView.findViewById<TextView>(R.id.memo)
        private val memoId = itemView.findViewById<TextView>(R.id.date)

        fun bind(str: String, id: String) {
            val line = str.lines().size

            memo.text = str
            memoId.text = id
            memo.maxLines = when (line.compareTo(5)) {
                1 -> line / 2
                else -> 5
            }


            itemView.setOnClickListener { ItemClick(id, str, itemView) }
            itemView.setOnLongClickListener { ItemLongClick(id, str, adapterPosition, itemView) }
            itemView.setBackgroundColor(Color.rgb(243, 243, 243))
        }
    }
}