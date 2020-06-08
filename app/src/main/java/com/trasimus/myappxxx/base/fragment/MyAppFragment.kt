package com.trasim.myapp.base.fragment

import com.trasim.base.screens.components.BaseParameters
import com.trasim.base.screens.components.BaseState
import com.trasim.base.screens.components.BaseViews
import com.trasim.base.screens.fragment.fragment.BaseDataFragment

abstract class MyAppFragment<PARAMETERS : BaseParameters, STATE : BaseState, VIEWS : BaseViews, HANDLER : Any> : BaseDataFragment<PARAMETERS, STATE, VIEWS, HANDLER>()