package com.trasim.myapp.data.entities.issueType

import com.trasim.myapp.data.providers.MyAppLocalStorage
import com.trasim.myapp.data.providers.MyAppServer

class IssueTypeOperations(localStorage: MyAppLocalStorage, server: MyAppServer) {

    private val queries = localStorage.issueTypeQueries
    private val transactions = server.issueTypeTransactions

    fun sendGetIssueTypes() = this.transactions.sendGetIssueTypesRequest()
    fun saveIssueTypes(issueTypes: List<IssueTypeLocal>) = this.queries.saveIssueTypesTransaction(issueTypes)
    fun getIssueTypes() = this.queries.getIssueTypes()
}