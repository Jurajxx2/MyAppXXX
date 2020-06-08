package com.trasim.myapp.screens.pin.fragments.settings

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.text.method.DigitsKeyListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.trasim.myapp.R
import com.trasim.myapp.Session
import com.trasim.myapp.base.fragment.MyAppBottomSheetDialogFragment
import com.trasim.myapp.data.entities.internal.local.InternalQueries
import com.trasim.myapp.screens.pin.fragments.settings.handlers.SettingsFragmentHandler
import com.trasim.myapp.widgets.SwitchView
import com.trasim.base.helpers.isIpValid
import com.trasim.base.helpers.isPortValid
import com.trasim.base.helpers.isUrlValid
import com.trasim.base.screens.components.BaseViews
import kotlinx.android.synthetic.main.pin__settings_fragment.*


class SettingsFragment : MyAppBottomSheetDialogFragment<Nothing, Nothing, SettingsFragment.Views, SettingsFragmentHandler>() {

    override fun setFragmentLayout() = R.layout.pin__settings_fragment

    override fun initiateViews(): Views = Views()

    inner class Views : BaseViews {

        private val switchListener = object : SwitchView.SwitchListener {
            override fun onFirstButtonClicked() {
                this@SettingsFragment.context?.let {
                    ipTitleTV.text = it.getString(R.string.settings_fragment__ip_address)
                    this@Views.setupIPAddress(it)
                    applyChangesB.setOnClickListener { this@Views.getApplyChangesMethod(InternalQueries.ConnectionMethod.IP).invoke() }
                }
            }

            override fun onSecondButtonClicked() {
                this@SettingsFragment.context?.let {
                    ipTitleTV.text = context?.getString(R.string.settings_fragment__url_address)
                    this@Views.setupURLAddress(it)
                    applyChangesB.setOnClickListener { this@Views.getApplyChangesMethod(InternalQueries.ConnectionMethod.URL).invoke() }
                }
            }
        }

        override fun setupViewModel() {}

        override fun modifyViews(context: Context?, bundle: Bundle?) {
            context?.let {
                val connectionMethod = Session.application.dataManager.internalOperations.loadConnectionMethod(context)
                this.setupSwitch(connectionMethod)
                this.setupPort(context)
            }

            cancelB.setOnClickListener {
                handler.onCancelClick()
            }
        }

        private fun setupSwitch(connectionMethod: InternalQueries.ConnectionMethod) {

            selectorSW.setup(this.switchListener, R.string.settings_fragment__connection_type__ip, R.string.settings_fragment__connection_type__url)

            if (connectionMethod == InternalQueries.ConnectionMethod.IP) {
                selectorSW.selectFirstButton()
            } else {
                selectorSW.selectSecondButton()
            }
        }

        private fun setupIPAddress(context: Context) =
            addressInputET.apply {
                this.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                this.keyListener = DigitsKeyListener.getInstance("0123456789.")
                this.setText(Session.application.dataManager.internalOperations.loadAddress(context, connectionMethod = InternalQueries.ConnectionMethod.IP))
            }

        private fun setupURLAddress(context: Context) {
            addressInputET.apply {
                this.inputType = InputType.TYPE_CLASS_TEXT
                this.setText(Session.application.dataManager.internalOperations.loadAddress(context, connectionMethod = InternalQueries.ConnectionMethod.URL))
            }
        }

        private fun setupPort(context: Context) = portInputET.setText(Session.application.dataManager.internalOperations.loadPort(context))

        private fun clearErrors() {
            addressInputET.error = null
            portInputET.error = null
        }

        private fun getApplyChangesMethod(connectionMethod: InternalQueries.ConnectionMethod): () -> Unit = {
            this.clearErrors()

            val address = addressInputET.text.toString()

            val isAddressValid = when (connectionMethod) {
                InternalQueries.ConnectionMethod.IP -> isIpValid(address)
                InternalQueries.ConnectionMethod.URL -> isUrlValid(address)
            }

            if (isAddressValid && isPortValid(portInputET.text.toString())) {
                handler.onApplyChangesClick(connectionMethod, address, portInputET.text.toString())
            } else {
                if (!isIpValid(addressInputET.text.toString())) {
                    addressInputET.error = getString(R.string.settings_fragment__wrong_format)
                }

                if (!isPortValid(portInputET.text.toString())) {
                    portInputET.error = getString(R.string.settings_fragment__wrong_format)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        this.view?.let { view ->
            view.post {
                (this.dialog as? BottomSheetDialog)?.behavior?.let {
                    it.peekHeight = view.measuredHeight
                }
            }
        }
    }
}
