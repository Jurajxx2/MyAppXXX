package com.trasim.myapp.screens.issues.fragments.issueDetail.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trasim.myapp.R
import com.trasim.myapp.data.entities.issue.CheckListType
import com.trasim.myapp.data.entities.issue.IssueState
import com.trasim.myapp.data.entities.issue.models.checkList.CheckList
import com.trasim.myapp.widgets.ChecklistElementView
import kotlinx.android.synthetic.main.issues_detail__task_list_item__row.view.*

class TaskListAdapter(val handler: ChecklistElementView.ChecklistHandler?, val adapterItems: MutableList<CheckList>, val state: IssueState, val isDisabled: Boolean = false) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ChecklistViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.issues_detail__task_list_item__row, parent, false))
    }

    override fun getItemCount() = adapterItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ChecklistViewHolder
        val checklist = adapterItems[position]

        viewHolder.taskItemCLE.number = position + 1
        viewHolder.taskItemCLE.checklist = checklist
        viewHolder.taskItemCLE.handler = handler
        viewHolder.taskItemCLE.itemCount = CheckListType.findStateByName(checklist.type)

        viewHolder.taskItemCLE.isDisabled = isDisabled

        viewHolder.taskItemCLE.room = when (state) {
            IssueState.NEW -> {
                ChecklistElementView.Room.CLEANING
            }
            IssueState.DONE -> {
                ChecklistElementView.Room.CLEAN
            }
            IssueState.IN_PROGRESS -> {
                ChecklistElementView.Room.PARTIALLY_CLEAN
            }
            IssueState.REJECTED -> {
                ChecklistElementView.Room.DO_NOT_DISTURB
            }
        }

    }

    class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskItemCLE: ChecklistElementView = itemView.taskItemCLE
    }
}