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

class MemoAdapter(var context: Context, var memoList : ArrayList<String> ,var memoId : ArrayList<String>, val ItemClick : (String, String, View) -> Unit) : RecyclerView.Adapter<MemoAdapter.Holder>() {
    val booleanArray = SparseBooleanArray(0)
    var selectedStr = SparseArray<String>(0)
    var selectedId = SparseArray<String>(0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_view_memo_item1,parent,false)

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(memoList[position],memoId[position])

        if (booleanArray[position])
            holder.itemView.setBackgroundColor(Color.CYAN)
        else
            holder.itemView.setBackgroundColor(Color.WHITE)
    }

    inner class Holder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val memo = itemView.findViewById<TextView>(R.id.memo)
        private val memoId = itemView.findViewById<TextView>(R.id.date)

        fun bind(str : String, id : String ){

            memo.text = str
            memoId.text = id

            itemView.setOnClickListener { ItemClick(id, str, itemView) }
            itemView.setBackgroundColor(Color.WHITE)

            itemView.setOnLongClickListener {
                if (selectedStr[adapterPosition] == str && selectedId[adapterPosition] == id) {
                booleanArray.put(adapterPosition, false)
                selectedStr.remove(adapterPosition)
                selectedId.remove(adapterPosition)
                itemView.setBackgroundColor(Color.WHITE)
            } else {
                booleanArray.put(adapterPosition, true)
                selectedStr.put(adapterPosition,str)
                selectedId.put(adapterPosition,id)
                itemView.setBackgroundColor(Color.CYAN)
            }
                true }
        }
    }
}