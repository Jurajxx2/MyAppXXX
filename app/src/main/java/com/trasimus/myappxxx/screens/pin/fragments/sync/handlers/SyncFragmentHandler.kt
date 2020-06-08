package com.trasim.myapp.screens.pin.fragments.sync.handlers

interface SyncFragmentHandler {
    fun onSynchronisationSuccess()
    fun onSynchronisationFailed()
    fun onCancelClick()
}