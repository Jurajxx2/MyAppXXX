package com.trasim.myapp.screens.issues.fragments.writeStateBottomSheetDialog.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.trasim.myapp.R
import com.trasim.myapp.data.entities.issue.models.checkList.CheckList
import com.trasim.myapp.widgets.CounterView
import kotlinx.android.synthetic.main.issues__write_state_list_item.view.*

class GoodsListAdapter(val adapterItems: CheckList, val changes: MutableList<Int>?, val handler: CounterView.CounterHandler) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GoodsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.issues__write_state_list_item, parent, false))
    }

    override fun getItemCount() = adapterItems.meta.list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as GoodsViewHolder
        val checkList = adapterItems.meta.list[position]
        val change = changes?.get(position)

        viewHolder.goodsTitleTV.text = checkList.name
        viewHolder.goodsSubtitleTV.text = checkList.description
        viewHolder.maxQuantityTV.text = String.format(viewHolder.maxQuantityTV.context.getString(R.string._issues_max_quantity_template), checkList.maxQuantity.toString())
        change?.let {
            viewHolder.goodsCounterC.currentValue = it
        } ?: run {
            viewHolder.goodsCounterC.currentValue = 0
        }
        viewHolder.goodsCounterC.position = position
        viewHolder.goodsCounterC.minValue = 0
        viewHolder.goodsCounterC.maxValue = checkList.value

        viewHolder.goodsCounterC.handler = handler

        if (position == adapterItems.meta.list.size - 1) {
            viewHolder.divider.visibility = View.GONE
        }
    }

    class GoodsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val goodsTitleTV: AppCompatTextView = itemView.goodsTitleTV
        val goodsSubtitleTV: AppCompatTextView = itemView.goodsSubtitleTV
        val goodsCounterC: CounterView = itemView.goodsCounterC
        val maxQuantityTV: AppCompatTextView = itemView.maxQuantityTV
        val divider: View = itemView.divider
    }
}