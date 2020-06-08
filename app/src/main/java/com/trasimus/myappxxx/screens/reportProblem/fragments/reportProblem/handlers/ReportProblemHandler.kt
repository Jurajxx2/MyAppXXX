package com.trasim.myapp.screens.reportProblem.fragments.reportProblem.handlers

import com.trasim.myapp.data.entities.issueType.IssueTypeLocal
import com.trasim.myapp.screens.reportProblem.fragments.reportProblem.CameraHandler

interface ReportProblemHandler {
    fun sendReport()
    fun showConfirmDeleteDialog()
    fun showSelectBottomSheet(options: List<IssueTypeLocal>, selectedOption: IssueTypeLocal?)
    fun onImageClick(photos: List<String>, id: String, position: Int)
    fun onAddNewImageClick(handler: CameraHandler)
    fun onGoBackToRoomPressed()
    fun writeNotes(notes: String)
    fun getNote(): String
    fun getName(): String?
    fun onIssueTypeSelected(issueTypeLocal: IssueTypeLocal)
    fun finishActivity()
}