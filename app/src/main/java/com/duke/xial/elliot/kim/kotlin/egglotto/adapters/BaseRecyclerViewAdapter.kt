package com.duke.xial.elliot.kim.kotlin.egglotto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class BaseRecyclerViewAdapter(private val layoutId: Int, var items: ArrayList<Int>):
    RecyclerView.Adapter<BaseRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(var view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {  }

    override fun getItemCount(): Int = items.count()

    fun insert(item: Int, position: Int = 0) {
        items.add(position, item)
        notifyItemChanged(position)
    }
}