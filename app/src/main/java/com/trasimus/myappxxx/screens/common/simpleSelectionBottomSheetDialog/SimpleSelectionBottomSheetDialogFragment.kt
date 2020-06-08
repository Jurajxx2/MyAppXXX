package com.trasim.myapp.screens.common.simpleSelectionBottomSheetDialog

import android.content.Context
import android.os.Bundle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.trasim.myapp.R
import com.trasim.myapp.base.fragment.MyAppBottomSheetDialogFragment
import com.trasim.myapp.screens.common.simpleSelectionBottomSheetDialog.adapters.OptionsListAdapter
import com.trasim.myapp.screens.common.simpleSelectionBottomSheetDialog.handlers.SimpleSelectionBottomSheetDialogHandler
import com.trasim.base.screens.components.BaseViews
import kotlinx.android.synthetic.main.issues__simple_selection_bottom_sheet_dialog_fragment.*


class SimpleSelectionBottomSheetDialogFragment : MyAppBottomSheetDialogFragment<Nothing, Nothing, SimpleSelectionBottomSheetDialogFragment.Views, SimpleSelectionBottomSheetDialogHandler>() {

    val args: SimpleSelectionBottomSheetDialogFragmentArgs by navArgs()

    override fun setFragmentLayout() = R.layout.issues__simple_selection_bottom_sheet_dialog_fragment

    override fun initiateViews(): Views = Views()

    inner class Views : BaseViews {
        override fun setupViewModel() {
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {

            val viewManager = LinearLayoutManager(context)
            val viewAdapter = OptionsListAdapter(args.selectedOption, args.options.toMutableList(), handler)

            if (args.isIssueSuccess){
                simpleSelectionTitleTV.text = getString(R.string.issue_detail__change_room_state)
            } else {
                simpleSelectionTitleTV.text = getString(R.string.report_problem__problem_type)
            }

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