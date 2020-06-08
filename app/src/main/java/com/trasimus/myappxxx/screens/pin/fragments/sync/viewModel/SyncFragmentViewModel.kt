package com.trasim.myapp.screens.pin.fragments.sync.viewModel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trasim.myapp.Session

class SyncFragmentViewModel(session: Session) : AndroidViewModel(session) {

    fun sendGetIssues() = Session.application.dataManager.issuesOperations.sendGetIssuesRequest()
    fun sendGetIssueTypes() = Session.application.dataManager.issueTypeOperations.sendGetIssueTypes()

    class InstanceFactory : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = SyncFragmentViewModel(Session.application) as T
    }
}