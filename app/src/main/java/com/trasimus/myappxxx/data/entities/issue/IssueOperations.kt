package com.trasim.myapp.data.entities.issue

import com.trasim.myapp.data.entities.issue.remote.IssueTransactions
import com.trasim.myapp.data.providers.MyAppLocalStorage
import com.trasim.myapp.data.providers.MyAppServer

class IssueOperations(localStorage: MyAppLocalStorage, server: MyAppServer) {

    private val queries = localStorage.issueQueries
    private val transactions = server.issueTransactions

    fun sendGetIssuesRequest() = this.transactions.sendGetIssuesRequest()
    fun sendUpdateIssue(issue: IssueRemote) = this.transactions.sendUpdateIssue(IssueTransactions.UpdateIssueRequestBody(issue))
    fun sendCreateIssue(issue: IssueTransactions.CreateIssueRequestBody) = this.transactions.sendCreateIssue(issue)
    fun sendCreateIssueImage(token: String, id: String, imageBytes: ByteArray) =
        this.transactions.sendCreateIssueImage(token, id, imageBytes)

    fun saveIssue(issue: IssueLocal) = this.queries.saveIssue(issue)
    fun saveIssues(issues: List<IssueLocal>) = this.queries.saveIssuesTransaction(issues)
    fun getIssues() = this.queries.getIssues()
    fun getIssue(id: String) = this.queries.getIssue(id)
    fun deleteAllIssues() = this.queries.deleteAllIssues()
}