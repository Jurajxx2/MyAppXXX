package com.trasim.myapp.screens.pin.fragments.pin.handlers

import com.trasim.myapp.data.entities.user.UserRemote

interface PinFragmentHandler {
    fun onLoginSuccess(userRemote: UserRemote)
    fun openSettingsFragment()
}