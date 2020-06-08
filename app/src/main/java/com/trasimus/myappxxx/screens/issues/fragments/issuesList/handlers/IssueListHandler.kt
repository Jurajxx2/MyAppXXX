package com.trasim.myapp.screens.issues.fragments.issuesList.handlers

interface IssueListHandler {
    fun goBackToIssues()
    fun onIssueClick(selectedIssueId: String, issues: List<String>)
    fun onFinishedIssuesClick(finishedIssues: List<String>)
    fun showLogoutDialog()
}