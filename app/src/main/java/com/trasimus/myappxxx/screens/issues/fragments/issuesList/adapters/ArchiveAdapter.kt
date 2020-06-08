package com.trasim.myapp.screens.issues.fragments.issuesList.adapters

import com.trasim.myapp.R
import com.trasim.myapp.data.entities.issue.Issue
import com.trasim.myapp.data.entities.issueType.IssueTypeLocal

class ArchiveAdapter(handler: IssuesListAdapterHandler) : IssuesListAdapter(handler) {

    override fun injectData(issueTypes: List<IssueTypeLocal>, issues: List<Issue>) {
        val allIssuesIds = issues.map { it.id }

        this.adapterItems.clear()

        this.adapterItems.add(IssueListAdapterItem.ArchiveHeaderItem())
        this.adapterItems.add(IssueListAdapterItem.IssueListHeaderItem(R.string.issues_list__issues_list_fragment__archive_title, issues.size, true))

        for (issue in issues) {
            this.adapterItems.add(IssueListAdapterItem.IssueListItem(issue.id, issue.name, issue.description, issue.checkout, allIssuesIds, issueTypes))
        }

        if (issues.isEmpty()) {
            this.adapterItems.add(IssueListAdapterItem.NoIssuesListItem())
        }

        this.notifyDataSetChanged()
    }
}