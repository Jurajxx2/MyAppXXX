package com.trasim.myapp.screens.reportProblem.fragments.reportProblem.viewModel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trasim.myapp.Session
import com.trasim.myapp.data.entities.issue.remote.IssueTransactions

class ReportProblemFragmentViewModel(session: Session) : AndroidViewModel(session) {

    fun sendCreateIssue(name: String, description: String, issueType: String, note: String) =
        Session.application.dataManager.issuesOperations.sendCreateIssue(IssueTransactions.CreateIssueRequestBody(name, description, issueType, note))

    fun sendCreateIssueImage(token: String, id: String, image: ByteArray) =
        Session.application.dataManager.issuesOperations.sendCreateIssueImage(token, id, image)

    fun getIssueTypes() = Session.application.dataManager.issueTypeOperations.getIssueTypes()

    class InstanceFactory : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            ReportProblemFragmentViewModel(Session.application) as T
    }
}