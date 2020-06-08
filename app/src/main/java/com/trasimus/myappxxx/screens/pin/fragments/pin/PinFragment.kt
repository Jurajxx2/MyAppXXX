package com.trasim.myapp.screens.pin.fragments.pin

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.trasim.myapp.R
import com.trasim.myapp.base.fragment.MyAppFragment
import com.trasim.myapp.screens.pin.fragments.pin.handlers.PinFragmentHandler
import com.trasim.myapp.screens.pin.fragments.pin.viewModel.PinFragmentViewModel
import com.trasim.myapp.widgets.KeyboardView
import com.trasim.myapp.widgets.PinView
import com.trasim.base.data.remote.transaction.TransactionResponseHandler
import com.trasim.base.helpers.DoubleClickListener
import com.trasim.base.screens.components.BaseViews
import kotlinx.android.synthetic.main.pin__pin_fragment.*

class PinFragment : MyAppFragment<Nothing, Nothing, PinFragment.Views, PinFragmentHandler>() {

    override fun setFragmentLayout() = R.layout.pin__pin_fragment

    override fun initiateViews(): Views = Views()

    inner class Views : BaseViews {

        private val viewModel = ViewModelProviders.of(this@PinFragment, PinFragmentViewModel.InstanceFactory()).get(PinFragmentViewModel::class.java)

        private val keyboardListener: KeyboardView.KeyboardListener = object : KeyboardView.KeyboardListener {
            override fun onNumericKeyClick(value: Int) = pinPV.addNewPinNumber(value)
            override fun onBackKeyClick() = pinPV.deleteLastPinNumber()
            override fun onBackKeyLongClick() = pinPV.clearPin()
        }

        private val pinViewListener: PinView.PinViewListener = object : PinView.PinViewListener {
            override fun onPinComplete(pin: String) {
                this@Views.viewModel.sendLogin(pin).observe(this@PinFragment, Observer {
                    when (it.responseStatus) {
                        TransactionResponseHandler.ResponseStatus.SUCCESS -> {
                            it.result?.let { userRemote ->
                                pinPV.clearPin()
                                this@PinFragment.handler.onLoginSuccess(userRemote)
                            }
                        }
                        TransactionResponseHandler.ResponseStatus.ERROR -> {
                            pinPV.clearPin()
                            Toast.makeText(this@PinFragment.context, R.string.pin__pin_fragment__pin_failed, Toast.LENGTH_SHORT).show()
                        }
                        TransactionResponseHandler.ResponseStatus.UNAUTHORIZED -> {
                            pinPV.clearPin()
                            Toast.makeText(this@PinFragment.context, R.string.pin__pin_fragment__pin_error, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }

        override fun setupViewModel() {
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {
            pinPV.pinViewListener = this.pinViewListener
            pinKV.keyboardListener = this.keyboardListener

            val doubleClickListener = object : DoubleClickListener() {
                override fun onSingleClick(v: View) {}
                override fun onDoubleClick(v: View) = this@PinFragment.handler.openSettingsFragment()
            }

            myappLogoIV.setOnClickListener(doubleClickListener)
        }
    }
}