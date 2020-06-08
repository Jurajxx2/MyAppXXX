package com.trasim.myapp.screens.pin.activity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.trasim.myapp.R
import com.trasim.myapp.Session
import com.trasim.myapp.base.acitivity.MyAppActivity
import com.trasim.myapp.data.entities.internal.local.InternalQueries
import com.trasim.myapp.data.entities.user.UserRemote
import com.trasim.myapp.screens.pin.fragments.pin.handlers.PinFragmentHandler
import com.trasim.myapp.screens.pin.fragments.settings.handlers.SettingsFragmentHandler
import com.trasim.myapp.screens.pin.fragments.sync.handlers.SyncFragmentHandler
import com.trasim.base.helpers.navigateSafe
import com.trasim.base.screens.components.BaseActivityViews


class PinActivity : MyAppActivity<Nothing, Nothing, PinActivity.Views>(), PinFragmentHandler, SyncFragmentHandler, SettingsFragmentHandler {

    override fun initializeViews() = Views()

    inner class Views : BaseActivityViews {
        override fun setupViewModel() {
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {
        }

        override fun setNavigationGraph() = R.id.pinNavigationHostF
    }

    override fun setActivityLayout() = R.layout.pin__activity

    override fun onLoginSuccess(userRemote: UserRemote) {
        this.navController?.navigateSafe(R.id.action_pinFragment_to_syncFragment)
    }

    override fun openSettingsFragment() {
        this.navController?.navigateSafe(R.id.action_pinFragment_to_settingsFragment)
    }

    override fun onSynchronisationSuccess() = Session.application.router.startIssuesActivity(this)

    override fun onSynchronisationFailed() {
        this.navController?.navigateUp()
        Toast.makeText(this, R.string.pin__sync_fragment__sync_error, Toast.LENGTH_SHORT).show()
    }

    override fun onApplyChangesClick(connectionMethod: InternalQueries.ConnectionMethod, address: String, port: String) {
        Session.application.dataManager.internalOperations.storeConnectionMethod(connectionMethod, this)
        Session.application.dataManager.internalOperations.storeAddress(address, this)
        Session.application.dataManager.internalOperations.storePort(port, this)

        this.navController?.navigateUp()
    }

    override fun onCancelClick() {
        this.navController?.navigateUp()
    }
}