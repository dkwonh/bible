package com.example.bible

import android.content.Context
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemoAdapter(
    var context: Context,
    private var memoList: ArrayList<String>,
    private var memoId: ArrayList<String>,
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


    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         private val memoText = itemView.findViewById<TextView>(R.id.memo)
         private val memoIdText = itemView.findViewById<TextView>(R.id.date)

        fun bind(str: String, id: String) {
            val line = str.lines().size

            memoText.text = str
            memoIdText.text = id
            memoText.maxLines = when (line.compareTo(5)) {
                1 -> line / 2
                else -> 5
            }

            if (booleanArray[adapterPosition]) {
                memoText.setBackgroundResource(R.drawable.item_selector_choice)
                memoIdText.setBackgroundResource(R.drawable.item_bottom_selector_choice)
            }
            else {
                memoText.setBackgroundResource(R.drawable.item_selector)
                memoIdText.setBackgroundResource(R.drawable.item_bottom_selector)
            }

            itemView.setBackgroundResource(R.color.colorLTGRAY)
            itemView.setOnClickListener { ItemClick(id, str, itemView) }
            itemView.setOnLongClickListener { ItemLongClick(id, str, adapterPosition, itemView) }
        }
    }
}