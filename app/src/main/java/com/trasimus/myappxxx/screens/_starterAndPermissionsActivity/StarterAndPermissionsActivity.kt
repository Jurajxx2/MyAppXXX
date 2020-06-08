package com.trasim.myapp.screens._starterAndPermissionsActivity

import com.trasim.myapp.R
import com.trasim.myapp.Session
import com.trasim.myapp.base.acitivity.MyAppActivity

class StarterAndPermissionsActivity : MyAppActivity<Nothing, Nothing, Nothing>() {

    override fun setActivityLayout() = R.layout.starter_and_permissions__activity

    override fun onActivityCreated() {
        super.onActivityCreated()
        Session.application.router.startPinActivity(this)
    }
}