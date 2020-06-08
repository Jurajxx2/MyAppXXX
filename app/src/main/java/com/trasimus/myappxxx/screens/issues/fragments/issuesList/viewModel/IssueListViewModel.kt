package com.trasim.myapp.screens.issues.fragments.issuesList.viewModel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trasim.myapp.Session

class IssueListViewModel(session: Session) : AndroidViewModel(session) {

    fun getIssues() = Session.application.dataManager.issuesOperations.getIssues()
    fun sendGetIssues() = Session.application.dataManager.issuesOperations.sendGetIssuesRequest()
    fun sendGetIssueTypes() = Session.application.dataManager.issueTypeOperations.sendGetIssueTypes()

    class InstanceFactory : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = IssueListViewModel(Session.application) as T
    }
}