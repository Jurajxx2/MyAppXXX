package com.trasim.myapp.screens.issues.fragments.issueDetail.viewModel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trasim.myapp.Session
import com.trasim.myapp.data.entities.issue.Issue
import com.trasim.myapp.data.entities.issue.IssueRemote

class IssuesDetailViewModel(session: Session) : AndroidViewModel(session) {

    var issue: Issue? = null

    fun getIssue(id: String) = Session.application.dataManager.issuesOperations.getIssue(id)

    fun updateIssue(issue: IssueRemote) = Session.application.dataManager.issuesOperations.sendUpdateIssue(issue)

    class InstanceFactory : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = IssuesDetailViewModel(Session.application) as T
    }
}