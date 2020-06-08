package com.trasim.myapp.screens.common.simpleSelectionBottomSheetDialog.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.trasim.myapp.R
import com.trasim.myapp.screens.common.simpleSelectionBottomSheetDialog.handlers.SimpleSelectionBottomSheetDialogHandler
import kotlinx.android.synthetic.main.issues__simple_selection_options_list_item.view.*

class OptionsListAdapter(private val selectedItem: String, private val adapterItems: MutableList<String>, val handler: SimpleSelectionBottomSheetDialogHandler) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var previouslySelectedCheckBox: AppCompatCheckBox? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StateViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.issues__simple_selection_options_list_item, parent, false))
    }

    override fun getItemCount() = adapterItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as StateViewHolder
        val option = adapterItems[position]

        viewHolder.optionNameTV.text = option
        if (option == selectedItem) {
            viewHolder.optionSelectionCheckBoxCB.isChecked = true
            previouslySelectedCheckBox = viewHolder.optionSelectionCheckBoxCB
        }

        viewHolder.simpleSelectionItemHolderCL.setOnClickListener {
            handler.onStateSelected(option)
            previouslySelectedCheckBox?.isChecked = false
            viewHolder.optionSelectionCheckBoxCB.isChecked = true
            previouslySelectedCheckBox = viewHolder.optionSelectionCheckBoxCB
        }

        if (position == adapterItems.size - 1) {
            viewHolder.divider.visibility = View.GONE
        }
    }

    class StateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val optionNameTV: TextView = itemView.optionNameTV
        val optionSelectionCheckBoxCB: AppCompatCheckBox = itemView.optionSelectionCheckBoxCB
        val divider: View = itemView.divider
        val simpleSelectionItemHolderCL: ConstraintLayout = itemView.simpleSelectionItemHolderCL
    }
}