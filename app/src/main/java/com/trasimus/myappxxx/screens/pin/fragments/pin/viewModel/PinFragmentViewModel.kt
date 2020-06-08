package com.trasim.myapp.screens.pin.fragments.pin.viewModel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trasim.myapp.Session

class PinFragmentViewModel(session: Session) : AndroidViewModel(session) {

    fun sendLogin(pin: String) = Session.application.dataManager.userOperations.sendLoginRequest(pin)

    class InstanceFactory : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = PinFragmentViewModel(Session.application) as T
    }
}