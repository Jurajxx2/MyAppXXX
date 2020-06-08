package com.trasim.myapp.screens.issues.fragments.goodsRefillInfoDialog.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trasim.myapp.R
import com.trasim.myapp.data.entities.issue.models.checkList.CheckList
import kotlinx.android.synthetic.main.issues__goods_refill_list_item.view.*
import java.io.Serializable

class GoodsRefillInfoAdapter(private val adapterItems: ChecklistChange) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GoodsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.issues__goods_refill_list_item, parent, false))
    }

    override fun getItemCount() = adapterItems.checkList.meta.list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as GoodsViewHolder
        val checkList = adapterItems.checkList.meta.list[position]
        val change = adapterItems.change[position]

        viewHolder.goodsTitleTV.text = checkList.name
        viewHolder.goodsSubtitleTV.text = checkList.description

        viewHolder.goodsCountTV.text = String.format(viewHolder.goodsCountTV.context.getString(R.string.issue_detail__plus_x_pcs), (checkList.value - change).toString())
        if (checkList.value == change) {
            viewHolder.goodsCountTV.alpha = 0.25f
        }

        if (position == adapterItems.checkList.meta.list.size - 1) {
            viewHolder.divider.visibility = View.GONE
        }
    }

    class GoodsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val goodsTitleTV: TextView = itemView.goodsTitleTV
        val goodsSubtitleTV: TextView = itemView.goodsSubtitleTV
        val goodsCountTV: TextView = itemView.goodsCountTV
        val divider: View = itemView.divider
    }
}

class ChecklistChange(val checkList: CheckList, val change: ArrayList<Int>) : Serializable
