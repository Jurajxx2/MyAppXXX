package com.trasim.myapp.screens.issues.fragments.issueDetail

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.trasim.myapp.R
import com.trasim.myapp.Session
import com.trasim.myapp.base.fragment.MyAppFragment
import com.trasim.myapp.data.entities.issue.Issue
import com.trasim.myapp.data.entities.issue.IssueRemote
import com.trasim.myapp.data.entities.issue.IssueState
import com.trasim.myapp.data.entities.issue.IssueStateNames
import com.trasim.myapp.data.entities.issue.models.checkList.CheckList
import com.trasim.myapp.data.entities.issue.remote.IssueTransactions
import com.trasim.myapp.data.entities.issueType.IssueTypeLocal
import com.trasim.myapp.data.entities.user.Role
import com.trasim.myapp.data.entities.user.UserLocal
import com.trasim.myapp.screens.issues.fragments.issueDetail.adapters.IssueDetailAdapter
import com.trasim.myapp.screens.issues.fragments.issueDetail.handlers.IssueDetailFragmentHandler
import com.trasim.myapp.screens.issues.fragments.issueDetail.viewModel.IssuesDetailViewModel
import com.trasim.myapp.widgets.ChecklistElementView
import com.trasim.myapp.widgets.RoomStateView
import com.trasim.myapp.widgets.galleryRow.GalleryRowView
import com.trasim.base.data.remote.transaction.TransactionResponseHandler
import com.trasim.base.screens.components.BaseParameters
import com.trasim.base.screens.components.BaseState
import com.trasim.base.screens.components.BaseViews
import kotlinx.android.synthetic.main.issues_detail__issues_detail_fragment.*

class IssueDetailFragment : MyAppFragment<IssueDetailFragment.Parameters, IssueDetailFragment.State, IssueDetailFragment.Views, IssueDetailFragmentHandler>(), RoomStateView.RoomStateHandler,
    GalleryRowView.GalleryRowHandler, ChecklistElementView.ChecklistHandler {

    companion object {
        const val ISSUE_ID_EXTRAS_KEY = "ISSUE_ID_EXTRAS_KEY"
        const val IS_FIRST_EXTRAS_KEY = "IS_FIRST_EXTRAS_KEY"
        const val IS_LAST_EXTRAS_KEY = "IS_LAST_EXTRAS_KEY"

        fun newInstance(issue: String, isFirst: Boolean = false, isLast: Boolean = false): MyAppFragment<*, *, *, *> {
            val fragment = IssueDetailFragment()

            val data = Bundle()
            data.putString(ISSUE_ID_EXTRAS_KEY, issue)
            data.putBoolean(IS_FIRST_EXTRAS_KEY, isFirst)
            data.putBoolean(IS_LAST_EXTRAS_KEY, isLast)

            fragment.arguments = data

            return fragment
        }
    }

    private var user: UserLocal? = Session.application.sessionStorage.userLocal

    override fun initializeParameters(): Parameters = Parameters()

    override fun initializeState(parameters: Parameters?): State = State()

    override fun setFragmentLayout() = R.layout.issues_detail__issues_detail_fragment

    override fun initiateViews(): Views = Views()

    inner class Parameters : BaseParameters {

        var issueId: String? = ""
        var isFirst: Boolean? = false
        var isLast: Boolean? = false

        override fun loadParameters(extras: Bundle?) {
            this.issueId = extras?.getString(ISSUE_ID_EXTRAS_KEY)
            this.isFirst = extras?.getBoolean(IS_FIRST_EXTRAS_KEY, false)
            this.isLast = extras?.getBoolean(IS_LAST_EXTRAS_KEY, false)
        }
    }

    inner class State : BaseState {

        private val issueIdBundleKey = "issueIdBundleKey"
        var issueId: String? = parameters?.issueId

        override fun saveInstanceState(outState: Bundle?) {
            outState?.putString(this.issueIdBundleKey, this.issueId)
        }

        override fun restoreInstanceState(savedInstanceState: Bundle) {
            this.issueId = savedInstanceState.getString(this.issueIdBundleKey)
        }
    }

    inner class Views : BaseViews {

        private val issueDetailAdapterHandler = object : IssueDetailAdapter.IssueDetailAdapterHandler {

            override fun onSetRoomCleanClick() {
                updateState(IssueState.DONE)
                this@IssueDetailFragment.handler.onSetRoomCleanClick()
            }
            override fun onDoNotDisturbClick() {
                rejectTask()
            }
            override fun onTakeOverClick() {
                acceptTask()
            }

            override fun onRoomStateClick(roomState: IssueStateNames) {
                if (views?.getAllowedIssueStates(roomState).isNullOrEmpty()) {
                    Toast.makeText(context, R.string.error__no_permission, Toast.LENGTH_LONG).show()
                } else {
                    views?.getAllowedIssueStates(roomState)?.get(0)?.allowed?.let {
                        this@IssueDetailFragment.handler.onRoomStateClick(roomState, it.toCollection(ArrayList()))
                    }
                }
            }

            override fun onReportProblemClick() =
                this@IssueDetailFragment.handler.onReportIssueClick(this@Views.viewModel.issue?.name ?: "")
            override fun onNextClick() = this@IssueDetailFragment.handler.onNextClick()
            override fun onPrevClick() = this@IssueDetailFragment.handler.onPrevClick()
        }

        val mainAdapter = IssueDetailAdapter(this.issueDetailAdapterHandler)

        private val viewModel = ViewModelProviders.of(this@IssueDetailFragment, IssuesDetailViewModel.InstanceFactory()).get(IssuesDetailViewModel::class.java)

        override fun setupViewModel() {
            state?.issueId?.let { id ->
                this.viewModel.getIssue(id).observe(this@IssueDetailFragment, Observer { issue ->
                    this.viewModel.issue = issue?.convertToMasterType()
                    Thread(Runnable {
                        val issueTypes = Session.application.dataManager.issueTypeOperations.getIssueTypes()
                        this@IssueDetailFragment.activity?.runOnUiThread { setupAdapterItems(issueTypes, this.viewModel.issue) }
                    }).start()
                })
            }
        }

        fun acceptTask() {
            viewModel.issue?.state = IssueState.IN_PROGRESS
            updateIssue(viewModel.issue)
        }

        fun rejectTask() {
            viewModel.issue?.state = IssueState.REJECTED
            updateIssue(viewModel.issue)
        }

        fun updateState(state: IssueState) {
            viewModel.issue?.state = state
            updateIssue(viewModel.issue)
        }

        fun updateChecklist(newState: ChecklistElementView.Mark, checkList: CheckList) {
            viewModel.issue?.checklists?.first { it == checkList }?.value = if (newState == ChecklistElementView.Mark.CHECKED) {
                1
            } else {
                0
            }
            updateIssue(viewModel.issue)
        }

        fun getAllowedIssueStates(currentState: IssueStateNames) = when (currentState) {
            IssueStateNames.WAITING -> {
                viewModel.issue?.permissions?.filter { it.current == IssueState.NEW.serializedName }
            }
            IssueStateNames.TAKEN -> {
                //viewModel.issue?.permissions?.filter { it.current == IssueState.IN_PROGRESS.serializedName }
                viewModel.issue?.permissions?.filter { it.current == "in progress" }
            }
            IssueStateNames.PARTIALLY_DONE, IssueStateNames.DONE, IssueStateNames.DONE_HANDYMAN -> {
                viewModel.issue?.permissions?.filter { it.current == IssueState.DONE.serializedName }
            }
            IssueStateNames.DO_NOT_DISTURB -> {
                viewModel.issue?.permissions?.filter { it.current == IssueState.REJECTED.serializedName }
            }
        }

        private fun updateIssue(issue: Issue?) {
            issue?.let {
                viewModel.updateIssue(IssueTransactions.UpdateIssueRequestBody(IssueRemote(it))).observe(this@IssueDetailFragment, Observer { response ->
                    if (response.responseStatus == TransactionResponseHandler.ResponseStatus.ERROR) {
                        val snackBar = Snackbar.make(issuesDetailHolderCL, R.string.issue_detail__network_error, Snackbar.LENGTH_INDEFINITE)
                        snackBar.setAction(android.R.string.ok) { snackBar.dismiss() }
                        snackBar.show()
                    }
                })
            }
        }

        fun openGallery(urlOrUri: String, position: Int) {
            views?.viewModel?.issue?.meta?.gallery?.map { it.source }?.toMutableList()?.let {
                handler.onImageClick(it, urlOrUri, position)
            }
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {
            val viewManager = LinearLayoutManager(activity)
            issueDetailRV.apply {
                layoutManager = viewManager
                adapter = mainAdapter
            }
        }
    }

    fun setupAdapterItems(issueTypes: List<IssueTypeLocal>, issueFromViewModel: Issue?) {

        val adapterRows = mutableListOf<IssueDetailAdapter.IssueDetailAdapterItem>()
        issueFromViewModel?.let { issue ->

            when (issue.state) {
                IssueState.NEW -> {
                    adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomHeaderItem(issue.name, issue.description, parameters?.isFirst, parameters?.isLast, IssueStateNames.WAITING, issueTypes))
                    issue.note?.let { adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomNotesItem(it)) }
                    user?.let { user ->
                        if (user.role == Role.CHAIRMAID.code) {
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomTakeOverPendingItem())
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.TaskListItem(issue.checklists, this@IssueDetailFragment, IssueState.NEW, true))
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomActionsItem(true))
                        } else {
                            issue.meta?.gallery?.map { it.source }?.toMutableList()
                                ?.let { gallery -> adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.GalleryItem(gallery, this@IssueDetailFragment)) }
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.TakeOverItem())
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomActionsItem(true, isCharmaid = false))
                        }
                    }
                }
                IssueState.IN_PROGRESS -> {
                    adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomHeaderItem(issue.name, issue.description, parameters?.isFirst, parameters?.isLast, IssueStateNames.TAKEN, issueTypes))
                    issue.note?.let { adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomNotesItem(it)) }
                    user?.let { user ->
                        if (user.role == Role.CHAIRMAID.code) {
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomStateItem(IssueStateNames.TAKEN))
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.TaskListItem(issue.checklists, this@IssueDetailFragment, IssueState.NEW))
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomActionsItem())
                        } else {
                            issue.meta?.gallery?.map { it.source }?.toMutableList()
                                ?.let { gallery -> adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.GalleryItem(gallery, this@IssueDetailFragment)) }
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomStateItem(IssueStateNames.TAKEN, isCharmaid = false))
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomActionsItem(isCharmaid = false))
                        }
                    }
                }
                IssueState.DONE -> {
                    user?.let { user ->
                        if (user.role == Role.CHAIRMAID.code) {
                            var isPartiallyDone = false
                            for (checklist in issue.checklists) {
                                if (checklist.value == 0) {
                                    isPartiallyDone = true
                                }
                            }

                            if (isPartiallyDone) {
                                adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomHeaderItem(issue.name, issue.description, parameters?.isFirst, parameters?.isLast, IssueStateNames.PARTIALLY_DONE, issueTypes))
                                issue.note?.let { adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomNotesItem(it)) }
                                adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomStateItem(IssueStateNames.PARTIALLY_DONE))
                                adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.TaskListItem(issue.checklists, this@IssueDetailFragment, IssueState.IN_PROGRESS))
                            } else {
                                adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomHeaderItem(issue.name, issue.description, parameters?.isFirst, parameters?.isLast, IssueStateNames.DONE, issueTypes))
                                issue.note?.let { adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomNotesItem(it)) }
                                adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomStateItem(IssueStateNames.DONE))
                                adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.TaskListItem(issue.checklists, this@IssueDetailFragment, IssueState.DONE))
                            }
                        } else {
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomHeaderItem(issue.name, issue.description, parameters?.isFirst, parameters?.isLast, IssueStateNames.DONE_HANDYMAN, issueTypes))
                            issue.note?.let { adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomNotesItem(it)) }
                            issue.meta?.gallery?.map { it.source }?.toMutableList()
                                ?.let { gallery -> adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.GalleryItem(gallery, this@IssueDetailFragment)) }
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomStateItem(IssueStateNames.DONE_HANDYMAN, isCharmaid = false))
                        }
                    }

                }
                IssueState.REJECTED -> {
                    user?.let { user ->
                        if (user.role == Role.CHAIRMAID.code) {
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomHeaderItem(issue.name, issue.description, parameters?.isFirst, parameters?.isLast, IssueStateNames.DO_NOT_DISTURB, issueTypes))
                            issue.note?.let { adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomNotesItem(it)) }
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.RoomStateItem(IssueStateNames.DO_NOT_DISTURB))
                            adapterRows.add(IssueDetailAdapter.IssueDetailAdapterItem.TaskListItem(issue.checklists, this@IssueDetailFragment, IssueState.REJECTED))
                        }
                    }
                }
            }
        }

        views?.mainAdapter?.setData(adapterRows)
    }

    fun onChecklistCheck(checkList: CheckList) {
        views?.updateChecklist(ChecklistElementView.Mark.CHECKED, checkList)
    }

    override fun onAddNewClick() {
        handler.onAddNewClick()
    }

    override fun onImageClick(id: String, position: Int) {
        views?.openGallery(id, position)
    }

    override fun onCheckClick(newState: ChecklistElementView.Mark, checkList: CheckList) {
        views?.updateChecklist(newState, checkList)
    }

    override fun onRoomStateClick(currentState: IssueStateNames) {
        user?.let {
            views?.getAllowedIssueStates(currentState)?.get(0)?.allowed?.let {
                this@IssueDetailFragment.handler.onRoomStateClick(currentState, it.toCollection(ArrayList()))
            } ?: run {
                Toast.makeText(context, R.string.error__no_permission, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun openSublist(checkList: CheckList) {
        handler.openSublist(checkList)
    }

    fun onStateSelected(state: IssueState) {
        views?.updateState(state)
    }
}