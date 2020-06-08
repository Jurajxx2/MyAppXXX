package com.trasim.myapp.screens.issues.fragments.writeStateBottomSheetDialog.handlers

import com.trasim.myapp.screens.issues.fragments.goodsRefillInfoDialog.adapters.ChecklistChange

interface WriteStateBottomSheetDialogHandler {
    fun onConfirmNumbersButtonClick(checklistChange: ChecklistChange)
}