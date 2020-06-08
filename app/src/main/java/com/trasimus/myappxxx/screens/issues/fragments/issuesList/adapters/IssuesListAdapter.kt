package com.trasim.myapp.screens.issues.fragments.issuesList.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.trasim.myapp.R
import com.trasim.myapp.Session
import com.trasim.myapp.data.entities.issue.Issue
import com.trasim.myapp.data.entities.issue.IssueState
import com.trasim.myapp.data.entities.issue.Priority
import com.trasim.myapp.data.entities.issueType.IssueTypeLocal
import com.trasim.myapp.widgets.graph.GraphView
import com.trasim.base.helpers.formatDateInUTC
import kotlinx.android.synthetic.main.issues_list__day_summary__adapter_view.view.*
import kotlinx.android.synthetic.main.issues_list__finished_issues__adapter_view.view.*
import kotlinx.android.synthetic.main.issues_list__issue_header__adapter_view.view.*
import kotlinx.android.synthetic.main.issues_list__issue_info__adapter_view.view.*
import kotlinx.android.synthetic.main.issues_list__issue_info__adapter_view.view.indicationStripeIV
import org.joda.time.DateTime
import kotlin.math.roundToInt

private const val HEADER_TIME__DATE_PATTERN = "dd. MMMM YYYY"

open class IssuesListAdapter(val handler: IssuesListAdapterHandler) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val adapterItems: MutableList<IssueListAdapterItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowType = RowType.values()[viewType]
        return rowType.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: IssueListAdapterItem = adapterItems[position]
        val rowType: RowType = RowType.values()[holder.itemViewType]
        rowType.onBindViewHolder(holder, this.handler, item)
    }

    override fun getItemCount() = this.adapterItems.size

    override fun getItemViewType(position: Int): Int {
        val adapterItem = this.adapterItems[position]
        val itemType = when (adapterItem) {
            is IssueListAdapterItem.DaySummaryItem -> RowType.DAY_SUMMARY.ordinal
            is IssueListAdapterItem.IssueListHeaderItem -> RowType.ISSUES_HEADER.ordinal
            is IssueListAdapterItem.FinishedIssuesListItem -> RowType.FINISHED_ISSUES.ordinal
            is IssueListAdapterItem.IssueListItem -> RowType.ISSUE.ordinal
            is IssueListAdapterItem.ArchiveHeaderItem -> RowType.ARCHIVE_HEADER.ordinal
            is IssueListAdapterItem.NoIssuesListItem -> RowType.NO_ISSUES.ordinal
        }
        return itemType
    }

    enum class RowType {
        DAY_SUMMARY {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_list__day_summary__adapter_view, parent, false)

                return IssuesListAdapterViewHolder.DaySummaryViewHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, handler: IssuesListAdapterHandler, adapterItem: IssueListAdapterItem) {
                (holder as? IssuesListAdapterViewHolder.DaySummaryViewHolder)?.let {
                    (adapterItem as? IssueListAdapterItem.DaySummaryItem)?.let {

                        holder.logoutIV.setOnClickListener {
                            handler.showLogoutDialog()
                        }

                        val date = formatDateInUTC(adapterItem.date, HEADER_TIME__DATE_PATTERN)
                        holder.todayProgressBaseInfoTV.text = adapterItem.userName.plus(", $date")
                        val percentageValue = if (adapterItem.allIssuesCount == 0) {
                            0.0
                        } else {
                            (adapterItem.finishedIssuesCount.toDouble() / adapterItem.allIssuesCount.toDouble()) * 100
                        }.roundToInt()

                        holder.todayProgressGV.percentageValue = percentageValue
                    }
                }
            }
        },
        ISSUES_HEADER {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_list__issue_header__adapter_view, parent, false)

                return IssuesListAdapterViewHolder.IssueListHeaderItem(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, handler: IssuesListAdapterHandler, adapterItem: IssueListAdapterItem) {
                (holder as? IssuesListAdapterViewHolder.IssueListHeaderItem)?.let {
                    (adapterItem as? IssueListAdapterItem.IssueListHeaderItem)?.let {
                        if (adapterItem.showCountInfo) {
                            val countInfoTemplate = holder.issuesCategoryHeaderTV.context.resources.getString(adapterItem.sectionNameResourceId) + " " + holder.issuesCategoryHeaderTV.context.resources.getString(R.string._parenthesis_template)
                            holder.issuesCategoryHeaderTV.text = String.format(countInfoTemplate, adapterItem.sectionItemsCount)
                        } else {
                            holder.issuesCategoryHeaderTV.text = holder.issuesCategoryHeaderTV.context.resources.getString(adapterItem.sectionNameResourceId)
                        }
                    }
                }
            }
        },
        ARCHIVE_HEADER {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_list__archive_header__adapter_view, parent, false)

                return IssuesListAdapterViewHolder.ArchiveHeaderItem(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, handler: IssuesListAdapterHandler, adapterItem: IssueListAdapterItem) {
                (holder as? IssuesListAdapterViewHolder.ArchiveHeaderItem)?.let {
                    (adapterItem as? IssueListAdapterItem.ArchiveHeaderItem)?.let {}
                }
            }
        },
        FINISHED_ISSUES {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_list__finished_issues__adapter_view, parent, false)

                return IssuesListAdapterViewHolder.FinishedIssuesHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, handler: IssuesListAdapterHandler, adapterItem: IssueListAdapterItem) {
                (holder as? IssuesListAdapterViewHolder.FinishedIssuesHolder)?.let {
                    (adapterItem as? IssueListAdapterItem.FinishedIssuesListItem)?.let {
                        holder.finishedHolderCV.setOnClickListener {
                            handler.onFinishedIssuesClick(adapterItem.finishedIssuesId)
                        }
                    }
                }
            }
        },
        ISSUE {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_list__issue_info__adapter_view, parent, false)

                return IssuesListAdapterViewHolder.IssueViewHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, handler: IssuesListAdapterHandler, adapterItem: IssueListAdapterItem) {
                (holder as? IssuesListAdapterViewHolder.IssueViewHolder)?.let {
                    (adapterItem as? IssueListAdapterItem.IssueListItem)?.let {
                        holder.taskNameTV.text = adapterItem.issueName

                        adapterItem.issuesTypes.firstOrNull {
                            it.code == adapterItem.issueDescription
                        }.apply {
                            holder.taskDescriptionTV.text = this?.name ?: adapterItem.issueDescription
                        }

                        val checkOutVisibility = if (adapterItem.checkOut) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }

                        holder.badgeHolderLL.visibility = checkOutVisibility
                        holder.indicationStripeIV.visibility = checkOutVisibility
                        holder.issueHolderCV.setOnClickListener {
                            handler.onIssueClick(adapterItem.issueId, adapterItem.allIssuesIds)
                        }
                    }
                }
            }
        },
        NO_ISSUES {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.issues_list__no_more_issues__adapter_view, parent, false)

                return IssuesListAdapterViewHolder.NoIssuesViewHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, handler: IssuesListAdapterHandler, adapterItem: IssueListAdapterItem) {
                (holder as? IssuesListAdapterViewHolder.NoIssuesViewHolder)?.let {
                    (adapterItem as? IssueListAdapterItem.NoIssuesListItem)?.let {}
                }
            }
        };

        abstract fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder
        abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder, handler: IssuesListAdapterHandler, adapterItem: IssueListAdapterItem)
    }

    private sealed class IssuesListAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        class DaySummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val todayProgressGV: GraphView = itemView.todayProgressGV
            val todayProgressBaseInfoTV: TextView = itemView.todayProgressBaseInfoTV
            val logoutIV: ImageView = itemView.logoutIV
        }

        class IssueListHeaderItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val issuesCategoryHeaderTV: TextView = itemView.issuesCategoryHeaderTV
        }

        class ArchiveHeaderItem(itemView: View) : RecyclerView.ViewHolder(itemView)

        class FinishedIssuesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val finishedHolderCV: MaterialCardView = itemView.finishedHolderCV
        }

        class IssueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val issueHolderCV: MaterialCardView = itemView.issueHolderCV
            val indicationStripeIV: ImageView = itemView.indicationStripeIV
            val issueTypeIV: ImageView = itemView.issueTypeIV
            val taskNameTV: TextView = itemView.taskNameTV
            val taskDescriptionTV: TextView = itemView.taskDescriptionTV
            val badgeHolderLL: LinearLayoutCompat = itemView.badgeHolderLL
            val badgeTextTV: TextView = itemView.badgeTextTV
        }

        class NoIssuesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    sealed class IssueListAdapterItem {
        class DaySummaryItem(val finishedIssuesCount: Int, val allIssuesCount: Int, val userName: String, val date: DateTime) : IssueListAdapterItem()
        class IssueListHeaderItem(val sectionNameResourceId: Int, val sectionItemsCount: Int, val showCountInfo: Boolean = true) : IssueListAdapterItem()
        class ArchiveHeaderItem : IssueListAdapterItem()
        class FinishedIssuesListItem(val finishedIssuesId: List<String>) : IssueListAdapterItem()
        class IssueListItem(val issueId: String, val issueName: String, val issueDescription: String, val checkOut: Boolean, val allIssuesIds: List<String>, val issuesTypes: List<IssueTypeLocal>) : IssueListAdapterItem()
        class NoIssuesListItem : IssueListAdapterItem()
    }

    open fun injectData(issueTypes: List<IssueTypeLocal>, issues: List<Issue>) {

        this.adapterItems.clear()

        var finishedIssuesCount = 0

        issues.filter { it.state == IssueState.DONE || it.state == IssueState.REJECTED }.map { it.id }.takeIf { it.isNotEmpty() }?.let { finishedIssuesIds ->
            this.adapterItems.add(IssueListAdapterItem.IssueListHeaderItem(R.string.issues_list__issues_list_fragment__archive_title, finishedIssuesIds.size, false))
            this.adapterItems.add(IssueListAdapterItem.FinishedIssuesListItem(finishedIssuesIds))
            finishedIssuesCount = finishedIssuesIds.count()
        }

        issues.filter { it.state == IssueState.NEW || it.state == IssueState.IN_PROGRESS }.let { openIssues ->

            val highPriorityOpenIssues = openIssues.filter { it.priority == Priority.HIGH }

            highPriorityOpenIssues.let {
                this.adapterItems.add(IssueListAdapterItem.IssueListHeaderItem(R.string.issues_list__issues_list_fragment__priority_issues, highPriorityOpenIssues.size))
                highPriorityOpenIssues.forEach { issue ->
                    this.adapterItems.add(IssueListAdapterItem.IssueListItem(issue.id, issue.name, issue.description, issue.checkout, openIssues.map { it.id }, issueTypes))
                }

                if (highPriorityOpenIssues.isEmpty()) {
                    this.adapterItems.add(IssueListAdapterItem.NoIssuesListItem())
                }
            }

            openIssues.filter { it !in highPriorityOpenIssues }.let { otherIssues ->
                this.adapterItems.add(IssueListAdapterItem.IssueListHeaderItem(R.string.issues_list__issues_list_fragment__other_issues, otherIssues.size))
                otherIssues.forEach { issue ->
                    this.adapterItems.add(IssueListAdapterItem.IssueListItem(issue.id, issue.name, issue.description, issue.checkout, openIssues.map { it.id }, issueTypes))
                }

                if (otherIssues.isEmpty()) {
                    this.adapterItems.add(IssueListAdapterItem.NoIssuesListItem())
                }
            }
        }

        Session.application.sessionStorage.userLocal?.name?.let { userName ->
            this.adapterItems.add(0, IssueListAdapterItem.DaySummaryItem(finishedIssuesCount, issues.map { it.id }.count(), userName, DateTime.now()))
        }

        this.notifyDataSetChanged()
    }

    interface IssuesListAdapterHandler {
        fun onIssueClick(selectedIssueId: String, issues: List<String>)
        fun onFinishedIssuesClick(finishedIssues: List<String>)
        fun showLogoutDialog()
    }
}