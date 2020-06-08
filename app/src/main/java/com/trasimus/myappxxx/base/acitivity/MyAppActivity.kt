package com.trasim.myapp.base.acitivity

import com.trasim.myapp.Session
import com.trasim.base.screens.activity.BaseDataActivity
import com.trasim.base.screens.components.BaseActivityViews
import com.trasim.base.screens.components.BaseParameters
import com.trasim.base.screens.components.BaseState

abstract class MyAppActivity<PARAMETERS : BaseParameters, STATE : BaseState, VIEWS : BaseActivityViews> : BaseDataActivity<PARAMETERS, STATE, VIEWS>() {

    override fun onActivityResumed() {
        super.onActivityResumed()
        this.handleResumeEvent()
    }

    open fun handleResumeEvent() {
        Session.application.sessionStorage.logoutUserIndicator = false
    }
}

abstract class MyAppAutoLogoutActivity<PARAMETERS : BaseParameters, STATE : BaseState, VIEWS : BaseActivityViews> : MyAppActivity<PARAMETERS, STATE, VIEWS>() {

    override fun handleResumeEvent() {
        if (Session.application.sessionStorage.logoutUserIndicator) {
            Session.application.clearDataAndRestartApplication(this)
        }
    }
}