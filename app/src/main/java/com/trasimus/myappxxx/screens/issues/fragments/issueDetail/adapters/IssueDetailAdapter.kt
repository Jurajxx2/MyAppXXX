package com.trasim.myapp.screens.issues.fragments.issueDetail.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trasim.myapp.R
import com.trasim.myapp.data.entities.issue.IssueState
import com.trasim.myapp.data.entities.issue.IssueStateNames
import com.trasim.myapp.data.entities.issue.models.checkList.CheckList
import com.trasim.myapp.data.entities.issueType.IssueTypeLocal
import com.trasim.myapp.widgets.ChecklistElementView
import com.trasim.myapp.widgets.RoomStateView
import com.trasim.myapp.widgets.galleryRow.GalleryRowView
import kotlinx.android.synthetic.main.issues_detail__gallery__adapter_row.view.*
import kotlinx.android.synthetic.main.issues_detail__room_actions__adapter_view.view.*
import kotlinx.android.synthetic.main.issues_detail__room_info_header__adapter_view.view.*
import kotlinx.android.synthetic.main.issues_detail__room_notes__adapter_view.view.*
import kotlinx.android.synthetic.main.issues_detail__room_state__adapter_view.view.*
import kotlinx.android.synthetic.main.issues_detail__take_over_room_demand__adapter_view.view.*
import kotlinx.android.synthetic.main.issues_detail__task_list__adapter_view.view.*

class IssueDetailAdapter(val handler: IssueDetailAdapterHandler) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), RoomStateView.RoomStateHandler {

    private val adapterItems: MutableList<IssueDetailAdapterItem> = mutableListOf()

    fun setData(newItems: MutableList<IssueDetailAdapterItem>) {
        this.adapterItems.clear()
        this.adapterItems.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowType = RowType.values()[viewType]
        return rowType.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: IssueDetailAdapterItem = adapterItems[position]
        val rowType: RowType = RowType.values()[holder.itemViewType]
        rowType.onBindViewHolder(holder, item, this.handler)
    }

    override fun getItemCount() = this.adapterItems.size

    override fun getItemViewType(position: Int): Int {
        return when (adapterItems[position]) {
            is IssueDetailAdapterItem.RoomHeaderItem -> RowType.ROOM_INFO_HEADER.ordinal
            is IssueDetailAdapterItem.RoomNotesItem -> RowType.ROOM_NOTE.ordinal
            is IssueDetailAdapterItem.RoomTakeOverPendingItem -> RowType.TAKE_OVER_ROOM_DEMAND.ordinal
            is IssueDetailAdapterItem.TakeOverItem -> RowType.TAKE_OVER_TASK_DEMAND.ordinal
            is IssueDetailAdapterItem.RoomStateItem -> RowType.ROOM_STATE.ordinal
            is IssueDetailAdapterItem.GalleryItem -> RowType.GALLERY.ordinal
            is IssueDetailAdapterItem.TaskListItem -> RowType.TASK_LIST.ordinal
            is IssueDetailAdapterItem.RoomActionsItem -> RowType.ROOM_ACTIONS.ordinal
        }
    }

    enum class RowType {
        ROOM_INFO_HEADER {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_detail__room_info_header__adapter_view, parent, false)

                return IssueDetailAdapterViewHolder.RoomHeaderHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: IssueDetailAdapterItem, handler: IssueDetailAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.RoomHeaderHolder)?.let {
                    (adapterItem as? IssueDetailAdapterItem.RoomHeaderItem)?.let {
                        holder.roomTitleTV.text = adapterItem.roomName

                        adapterItem.issuesTypes.firstOrNull {
                            it.code == adapterItem.roomDescription
                        }.apply {
                            holder.roomSubtitleTV.text = this?.name ?: adapterItem.roomDescription
                        }

                        val context = holder.roomStateIconOverlayIV.context

                        holder.roomStateIconOverlayIV.visibility = if (adapterItem.roomState != IssueStateNames.TAKEN && adapterItem.roomState != IssueStateNames.WAITING) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }

                        holder.roomStateIconOverlayIV.setImageDrawable(when (adapterItem.roomState) {
                            IssueStateNames.DONE, IssueStateNames.DONE_HANDYMAN -> {
                                ContextCompat.getDrawable(context, R.drawable.done)
                            }
                            IssueStateNames.PARTIALLY_DONE -> {
                                ContextCompat.getDrawable(context, R.drawable.in_progress)
                            }
                            IssueStateNames.DO_NOT_DISTURB -> {
                                ContextCompat.getDrawable(context, R.drawable.no_go)
                            }
                            else -> null
                        })

                        holder.rightIV.setOnClickListener {
                            handler.onNextClick()
                        }

                        holder.leftIV.setOnClickListener {
                            handler.onPrevClick()
                        }

                        adapterItem.isFirst?.let {
                            if (adapterItem.isFirst) {
                                holder.leftIV.visibility = View.INVISIBLE
                            }
                        }

                        adapterItem.isLast?.let {
                            if (adapterItem.isLast) {
                                holder.rightIV.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            }
        },
        ROOM_NOTE {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_detail__room_notes__adapter_view, parent, false)

                return IssueDetailAdapterViewHolder.RoomNotesHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: IssueDetailAdapterItem, handler: IssueDetailAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.RoomNotesHolder)?.let {
                    (adapterItem as? IssueDetailAdapterItem.RoomNotesItem)?.let {
                        holder.roomNoteTV.text = adapterItem.roomNote
                    }
                }
            }
        },
        TAKE_OVER_ROOM_DEMAND {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_detail__take_over_room_demand__adapter_view, parent, false)

                return IssueDetailAdapterViewHolder.RoomTakeOverPendingHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: IssueDetailAdapterItem, handler: IssueDetailAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.RoomTakeOverPendingHolder)?.let {
                    (adapterItem as? IssueDetailAdapterItem.RoomTakeOverPendingItem)?.let {
                        holder.takeOverB.setOnClickListener {
                            handler.onTakeOverClick()
                        }

                        holder.doNotDisturbB.setOnClickListener {
                            handler.onDoNotDisturbClick()
                        }
                    }
                }
            }
        },
        TAKE_OVER_TASK_DEMAND {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_detail__take_over__adapter_view, parent, false)

                return IssueDetailAdapterViewHolder.TakeOverHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: IssueDetailAdapterItem, handler: IssueDetailAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.TakeOverHolder)?.let {
                    (adapterItem as? IssueDetailAdapterItem.TakeOverItem)?.let {
                        holder.takeOverB.setOnClickListener {
                            handler.onTakeOverClick()
                        }
                    }
                }
            }
        },
        ROOM_STATE {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_detail__room_state__adapter_view, parent, false)

                return IssueDetailAdapterViewHolder.RoomStateHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: IssueDetailAdapterItem, handler: IssueDetailAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.RoomStateHolder)?.let {
                    (adapterItem as? IssueDetailAdapterItem.RoomStateItem)?.let {
                        holder.roomStateRSV.isChairmaid = adapterItem.isCharmaid
                        holder.roomStateRSV.state = adapterItem.state
                        holder.roomStateRSV.handler = object : RoomStateView.RoomStateHandler {
                            override fun onRoomStateClick(currentState: IssueStateNames) {
                                handler.onRoomStateClick(currentState)
                            }
                        }
                    }
                }
            }
        },
        GALLERY {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_detail__gallery__adapter_row, parent, false)

                return IssueDetailAdapterViewHolder.GalleryHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: IssueDetailAdapterItem, handler: IssueDetailAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.GalleryHolder)?.let {
                    (adapterItem as? IssueDetailAdapterItem.GalleryItem)?.let {
                        holder.galleryGRV.handler = adapterItem.handler
                        holder.galleryGRV.mode = GalleryRowView.Mode.REMOTE
                        holder.galleryGRV.imageList = adapterItem.imageList
                    }
                }
            }
        },
        TASK_LIST {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_detail__task_list__adapter_view, parent, false)

                return IssueDetailAdapterViewHolder.TaskListHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: IssueDetailAdapterItem, handler: IssueDetailAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.TaskListHolder)?.let {
                    (adapterItem as? IssueDetailAdapterItem.TaskListItem)?.let {

                        val viewManager = LinearLayoutManager(holder.taskListRV.context)
                        val adapterItems = adapterItem.checkList
                        val viewAdapter = TaskListAdapter(adapterItem.handler, adapterItems.toMutableList(), adapterItem.state, adapterItem.isDisabled)

                        holder.taskListRV.apply {
                            layoutManager = viewManager
                            adapter = viewAdapter
                        }
                    }
                }
            }
        },
        ROOM_ACTIONS {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_detail__room_actions__adapter_view, parent, false)

                return IssueDetailAdapterViewHolder.RoomActionsHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: IssueDetailAdapterItem, handler: IssueDetailAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.RoomActionsHolder)?.let {
                    (adapterItem as? IssueDetailAdapterItem.RoomActionsItem)?.let {

                        if (!adapterItem.isCharmaid) {
                            holder.changeStateToCleanB.text = holder.changeStateToCleanB.context.getString(R.string._issues_detail__set_issue_as_resolved)
                        }

                        holder.changeStateToCleanB.setOnClickListener {
                            handler.onSetRoomCleanClick()
                        }

                        holder.reportProblemB.setOnClickListener {
                            handler.onReportProblemClick()
                        }

                        val enabled = !adapterItem.isDisabled
                        holder.changeStateToCleanB.isEnabled = enabled
                        holder.reportProblemB.isEnabled = enabled
                    }
                }
            }
        };

        abstract fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder
        abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: IssueDetailAdapterItem, handler: IssueDetailAdapterHandler)
    }

    override fun onRoomStateClick(currentState: IssueStateNames) {
    }

    private sealed class IssueDetailAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class RoomHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val roomTitleTV: TextView = itemView.roomTitleTV
            val roomSubtitleTV: TextView = itemView.roomSubtitleTV
            val rightIV: ImageView = itemView.rightIV
            val leftIV: ImageView = itemView.leftIV
            val roomStateIconOverlayIV: ImageView = itemView.roomStateIconOverlayIV
        }

        class RoomNotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val roomNoteTV: TextView = itemView.roomNoteTV
        }

        class RoomTakeOverPendingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val takeOverB: Button = itemView.takeOverB
            val doNotDisturbB: Button = itemView.doNotDisturbB
        }

        class TakeOverHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val takeOverB: Button = itemView.takeOverB
        }

        class RoomStateHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val roomStateRSV: RoomStateView = itemView.roomStateRSV
        }

        class GalleryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val galleryGRV: GalleryRowView = itemView.galleryGRV
        }

        class TaskListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val taskListRV: RecyclerView = itemView.taskListRV
            val taskListHolderCL: ConstraintLayout = itemView.taskListHolderCL
            val taskListTitleTV: TextView = itemView.taskListTitleTV
        }

        class RoomActionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val changeStateToCleanB: Button = itemView.changeStateToCleanB
            val reportProblemB: Button = itemView.reportProblemB
            val roomActionsHolderCL: ConstraintLayout = itemView.roomActionsHolderCL
        }
    }

    sealed class IssueDetailAdapterItem {
        class RoomHeaderItem(val roomName: String, val roomDescription: String, val isFirst: Boolean?, val isLast: Boolean?, val roomState: IssueStateNames, val issuesTypes: List<IssueTypeLocal>) : IssueDetailAdapterItem()
        class RoomNotesItem(val roomNote: String) : IssueDetailAdapterItem()
        class RoomTakeOverPendingItem : IssueDetailAdapterItem()
        class TakeOverItem : IssueDetailAdapterItem()
        class RoomStateItem(val state: IssueStateNames, val isCharmaid: Boolean = true) : IssueDetailAdapterItem()
        class GalleryItem(val imageList: MutableList<String>, val handler: GalleryRowView.GalleryRowHandler) : IssueDetailAdapterItem()
        class TaskListItem(val checkList: Array<CheckList>, val handler: ChecklistElementView.ChecklistHandler, val state: IssueState, val isDisabled: Boolean = false) : IssueDetailAdapterItem()
        class RoomActionsItem(val isDisabled: Boolean = false, val isCharmaid: Boolean = true) : IssueDetailAdapterItem()
    }

    interface IssueDetailAdapterHandler {
        fun onSetRoomCleanClick()
        fun onDoNotDisturbClick()
        fun onReportProblemClick()
        fun onTakeOverClick()
        fun onRoomStateClick(roomState: IssueStateNames)
        fun onNextClick()
        fun onPrevClick()
    }
}