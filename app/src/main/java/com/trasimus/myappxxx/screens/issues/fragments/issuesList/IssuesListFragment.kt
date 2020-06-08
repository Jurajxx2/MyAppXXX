package com.trasim.myapp.screens.issues.fragments.issuesList

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.trasim.myapp.R
import com.trasim.myapp.Session
import com.trasim.myapp.base.fragment.MyAppFragment
import com.trasim.myapp.data.entities.issue.Issue
import com.trasim.myapp.data.entities.issue.IssueState
import com.trasim.myapp.data.entities.issueType.IssueTypeLocal
import com.trasim.myapp.screens.issues.fragments.issuesList.adapters.ArchiveAdapter
import com.trasim.myapp.screens.issues.fragments.issuesList.adapters.IssuesListAdapter
import com.trasim.myapp.screens.issues.fragments.issuesList.handlers.IssueListHandler
import com.trasim.myapp.screens.issues.fragments.issuesList.viewModel.IssueListViewModel
import com.trasim.base.screens.components.BaseViews
import kotlinx.android.synthetic.main.issues_list__issues_list_fragment.*

class IssuesListFragment : MyAppFragment<Nothing, Nothing, IssuesListFragment.Views, IssueListHandler>() {

    val args: IssuesListFragmentArgs by navArgs()

    override fun setFragmentLayout() = R.layout.issues_list__issues_list_fragment

    override fun initiateViews(): Views = Views()

    inner class Views : BaseViews {
        private val viewModel = ViewModelProviders.of(this@IssuesListFragment, IssueListViewModel.InstanceFactory()).get(IssueListViewModel::class.java)
        private val issuesListAdapter = if (!args.isArchive) {
            IssuesListAdapter(object : IssuesListAdapter.IssuesListAdapterHandler {
                override fun onIssueClick(selectedIssueId: String, issues: List<String>) =
                    this@IssuesListFragment.handler.onIssueClick(selectedIssueId, issues)

                override fun onFinishedIssuesClick(finishedIssues: List<String>) =
                    this@IssuesListFragment.handler.onFinishedIssuesClick(finishedIssues)

                override fun showLogoutDialog() = this@IssuesListFragment.handler.showLogoutDialog()
            })
        } else {
            ArchiveAdapter(object : IssuesListAdapter.IssuesListAdapterHandler {
                override fun onIssueClick(selectedIssueId: String, issues: List<String>) =
                    this@IssuesListFragment.handler.onIssueClick(selectedIssueId, issues)

                override fun onFinishedIssuesClick(finishedIssues: List<String>) =
                    this@IssuesListFragment.handler.onFinishedIssuesClick(finishedIssues)

                override fun showLogoutDialog() {}
            })
        }

        override fun setupViewModel() {
            this.viewModel.getIssues().observe(this@IssuesListFragment, Observer { issuesList ->
                    issuesList?.map { localIssue -> Issue(localIssue) }?.let {
                        Thread(Runnable {
                            val issueTypes = Session.application.dataManager.issueTypeOperations.getIssueTypes()
                            this@IssuesListFragment.activity?.runOnUiThread {
                                injectDataToAdapter(issueTypes, it)
                            }
                        }).start()
                    }
            })
        }

        private fun injectDataToAdapter(issueTypes: List<IssueTypeLocal>, issues: List<Issue>) {
            if (!args.isArchive) {
                this.issuesListAdapter.injectData(issueTypes, issues)
            } else {
                this.issuesListAdapter.injectData(issueTypes, issues.filter { it.state == IssueState.DONE || it.state == IssueState.REJECTED })
            }
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {
            val linearLayoutManager = LinearLayoutManager(this@IssuesListFragment.context)
            issuesListRV.adapter = this.issuesListAdapter
            issuesListRV.layoutManager = linearLayoutManager
            issuesListRV.setHasFixedSize(true)

            issueRefreshSRL.setOnRefreshListener {
                this.viewModel.sendGetIssues().observe(this@IssuesListFragment, Observer {
                    this.viewModel.sendGetIssueTypes().observe(this@IssuesListFragment, Observer {
                        issueRefreshSRL.isRefreshing = false
                    })
                })
            }

            if (args.isArchive) {
                goBackFromArchiveTV.visibility = View.VISIBLE
                goBackFromArchiveTV.setOnClickListener {
                    handler.goBackToIssues()
                }
            }
        }
    }
}