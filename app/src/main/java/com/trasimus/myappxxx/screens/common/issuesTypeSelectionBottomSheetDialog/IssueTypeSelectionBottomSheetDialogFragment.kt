package com.trasim.myapp.screens.common.issuesTypeSelectionBottomSheetDialog

import android.content.Context
import android.os.Bundle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.trasim.myapp.R
import com.trasim.myapp.base.fragment.MyAppBottomSheetDialogFragment
import com.trasim.myapp.screens.common.issuesTypeSelectionBottomSheetDialog.adapters.IssueTypeOptionsListAdapter
import com.trasim.myapp.screens.common.issuesTypeSelectionBottomSheetDialog.handlers.IssueTypeSelectionBottomSheetDialogHandler
import com.trasim.base.screens.components.BaseViews
import kotlinx.android.synthetic.main.issues__simple_selection_bottom_sheet_dialog_fragment.*


class IssueTypeSelectionBottomSheetDialogFragment : MyAppBottomSheetDialogFragment<Nothing, Nothing, IssueTypeSelectionBottomSheetDialogFragment.Views, IssueTypeSelectionBottomSheetDialogHandler>() {

    val args: IssueTypeSelectionBottomSheetDialogFragmentArgs by navArgs()

    override fun setFragmentLayout() = R.layout.issues__simple_selection_bottom_sheet_dialog_fragment

    override fun initiateViews(): Views = Views()

    inner class Views : BaseViews {
        override fun setupViewModel() {
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {

            simpleSelectionTitleTV.text = getString(R.string.report_problem__problem_type)

            val viewManager = LinearLayoutManager(context)
            val viewAdapter = IssueTypeOptionsListAdapter(args.selectedOption, args.options.toMutableList(), handler)

            simpleSelectionOptionsRV.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }
    }

    override fun onStart() {
        super.onStart()
        this.view?.let { view ->
            view.post {
                (this.dialog as? BottomSheetDialog)?.behavior?.let {
                    it.peekHeight = view.measuredHeight
                }
            }
        }
    }
}