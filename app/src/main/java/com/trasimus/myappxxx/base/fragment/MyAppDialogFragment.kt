package com.trasim.myapp.base.fragment

import com.trasim.base.screens.components.BaseParameters
import com.trasim.base.screens.components.BaseState
import com.trasim.base.screens.components.BaseViews
import com.trasim.base.screens.fragment.dialogFragment.BaseDataDialogFragment

abstract class MyAppDialogFragment<PARAMETERS : BaseParameters, STATE : BaseState, VIEWS : BaseViews, HANDLER : Any> : BaseDataDialogFragment<PARAMETERS, STATE, VIEWS, HANDLER>()