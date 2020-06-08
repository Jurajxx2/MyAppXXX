package com.trasim.myapp.screens.common.issuesTypeSelectionBottomSheetDialog.handlers

import com.trasim.myapp.data.entities.issueType.IssueTypeLocal

interface IssueTypeSelectionBottomSheetDialogHandler {
    fun onIssueTypeSelected(selectedItem: IssueTypeLocal)
}