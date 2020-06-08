package com.trasim.myapp.base.fragment

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.trasim.myapp.R
import com.trasim.base.screens.components.BaseParameters
import com.trasim.base.screens.components.BaseState
import com.trasim.base.screens.components.BaseViews
import com.trasim.base.screens.fragment.bottomSheetDialogFragment.BaseDataBottomSheetDialogFragment

abstract class MyAppBottomSheetDialogFragment<PARAMETERS : BaseParameters, STATE : BaseState, VIEWS : BaseViews, HANDLER : Any> :
    BaseDataBottomSheetDialogFragment<PARAMETERS, STATE, VIEWS, HANDLER>() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)
}