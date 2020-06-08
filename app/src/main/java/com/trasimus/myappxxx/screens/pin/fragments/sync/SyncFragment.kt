package com.trasim.myapp.screens.pin.fragments.sync

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.trasim.myapp.R
import com.trasim.myapp.base.fragment.MyAppFragment
import com.trasim.myapp.data.entities.issue.IssueRemote
import com.trasim.myapp.data.entities.issueType.IssueTypeRemote
import com.trasim.myapp.screens.pin.fragments.sync.handlers.SyncFragmentHandler
import com.trasim.myapp.screens.pin.fragments.sync.viewModel.SyncFragmentViewModel
import com.trasim.base.data.remote.transaction.NetworkResponse
import com.trasim.base.data.remote.transaction.TransactionResponseHandler
import com.trasim.base.screens.components.BaseViews


class SyncFragment : MyAppFragment<Nothing, Nothing, SyncFragment.Views, SyncFragmentHandler>() {

    override fun setFragmentLayout() = R.layout.pin__sync_fragment

    override fun initiateViews(): Views = Views()

    inner class Views : BaseViews {

        private val viewModel = ViewModelProviders.of(this@SyncFragment, SyncFragmentViewModel.InstanceFactory()).get(SyncFragmentViewModel::class.java)

        private val getIssuesTypesObserver: Observer<NetworkResponse<Array<IssueTypeRemote>>> = Observer {
            when (it.responseStatus) {
                TransactionResponseHandler.ResponseStatus.SUCCESS -> {
                    this@SyncFragment.handler.onSynchronisationSuccess()
                }
                TransactionResponseHandler.ResponseStatus.ERROR -> {
                    this@SyncFragment.handler.onSynchronisationFailed()
                }
                TransactionResponseHandler.ResponseStatus.UNAUTHORIZED -> {
                }
            }
        }

        private val getIssuesObserver: Observer<NetworkResponse<Array<IssueRemote>>> = Observer {
            when (it.responseStatus) {
                TransactionResponseHandler.ResponseStatus.SUCCESS -> {
                    this.viewModel.sendGetIssueTypes().observe(this@SyncFragment, this@Views.getIssuesTypesObserver)
                }
                TransactionResponseHandler.ResponseStatus.ERROR -> {
                    this@SyncFragment.handler.onSynchronisationFailed()
                }
                TransactionResponseHandler.ResponseStatus.UNAUTHORIZED -> {
                }
            }
        }

        override fun setupViewModel() {
            this.viewModel.sendGetIssues().observe(this@SyncFragment, this.getIssuesObserver)
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {
        }
    }
}
