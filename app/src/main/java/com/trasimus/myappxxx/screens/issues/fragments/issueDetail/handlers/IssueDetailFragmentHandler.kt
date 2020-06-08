package com.trasim.myapp.screens.issues.fragments.issueDetail.handlers

import com.trasim.myapp.data.entities.issue.IssueStateNames
import com.trasim.myapp.data.entities.issue.models.checkList.CheckList
import com.trasim.myapp.widgets.ChecklistElementView

interface IssueDetailFragmentHandler {
    fun onNavigationBackClick()

    fun onTakeOverClick()

    fun onDoNotDisturbClick()

    fun onSetRoomCleanClick()

    fun onReportIssueClick(name: String)

    fun onImageClick(urlOrUriList: MutableList<String>, id: String, position: Int)

    fun onAddNewClick()

    fun openSublist(checkList: CheckList)

    fun onCheckClick(newState: ChecklistElementView.Mark, checkList: CheckList)

    fun onRoomStateClick(currentState: IssueStateNames, allowedStates: ArrayList<String>)

    fun onNextClick()

    fun onPrevClick()
}