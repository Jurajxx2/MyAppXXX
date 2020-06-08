package com.trasim.myapp.screens.pin.fragments.settings.handlers

import com.trasim.myapp.data.entities.internal.local.InternalQueries

interface SettingsFragmentHandler {
    fun onCancelClick()
    fun onApplyChangesClick(connectionMethod: InternalQueries.ConnectionMethod, address: String, port: String)
}