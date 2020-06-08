package com.trasim.myapp.screens.common.successDialog

import android.content.Context
import android.os.Bundle
import androidx.navigation.fragment.navArgs
import com.trasim.myapp.R
import com.trasim.myapp.base.fragment.MyAppDialogFragment
import com.trasim.myapp.screens.common.successDialog.handlers.SuccessDialogHandler
import com.trasim.base.screens.components.BaseViews
import kotlinx.android.synthetic.main.issues__success_dialog_fragment.*

class SuccessDialogFragment : MyAppDialogFragment<Nothing, Nothing, SuccessDialogFragment.Views, SuccessDialogHandler>() {

    val args: SuccessDialogFragmentArgs by navArgs()

    override fun setFragmentLayout() = R.layout.issues__success_dialog_fragment

    override fun initiateViews(): Views = Views()

    inner class Views : BaseViews {
        override fun setupViewModel() {
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {

            if(args.isIssueSuccess){
                showNextB.text = getString(R.string.issues__show_next_issue)
                goBackB.text = getString(R.string.issues__back_to_list_of_issues)
                successDialogTitleTV.text = getString(R.string.issue_detail__room_succesfuly_marked)
            } else {
                showNextB.text = getString(R.string.report_problem__report_next_problem)
                goBackB.text = getString(R.string.report_problem__back_to_room)
                successDialogTitleTV.text = getString(R.string.report_problem__successfully_reported)
            }

            showNextB.setOnClickListener {
                handler.onShowNextIssueButtonClick("")
            }

            goBackB.setOnClickListener {
                handler.onGoBackToIssuesButtonClick()
            }
        }
    }
}