package com.trasim.myapp.screens.common.deleteDialog

import android.content.Context
import android.os.Bundle
import com.trasim.myapp.R
import com.trasim.myapp.base.fragment.MyAppDialogFragment
import com.trasim.myapp.screens.common.deleteDialog.handlers.DeleteDialogHandler
import com.trasim.base.screens.components.BaseViews
import kotlinx.android.synthetic.main.issues__delete_dialog_fragment.*

class DeleteDialogFragment : MyAppDialogFragment<Nothing, Nothing, DeleteDialogFragment.Views, DeleteDialogHandler>() {


    override fun setFragmentLayout() = R.layout.issues__delete_dialog_fragment

    override fun initiateViews(): Views = Views()

    inner class Views : BaseViews {
        override fun setupViewModel() {
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {
            deleteYesB.setOnClickListener {
                handler.onDeleteConfirmationClick()
            }

            deleteNoB.setOnClickListener {
                handler.onDeleteGoBackClick()
            }
        }
    }
}