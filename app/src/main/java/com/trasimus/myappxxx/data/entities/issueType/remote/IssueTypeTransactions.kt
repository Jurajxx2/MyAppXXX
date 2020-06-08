package com.trasim.myapp.data.entities.issueType.remote

import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.google.gson.JsonArray
import com.trasim.myapp.BuildConfig
import com.trasim.myapp.Session
import com.trasim.myapp.data.entities.issueType.IssueTypeLocal
import com.trasim.myapp.data.entities.issueType.IssueTypeRemote
import com.trasim.base.data.remote.Server
import com.trasim.base.data.remote.transaction.NetworkResponse
import com.trasim.base.data.remote.transaction.processing.http.HttpNetworkTransaction
import com.trasim.base.data.remote.transaction.request.RequestBuilder

class IssueTypeTransactions(private val server: Server, private val requestBuilder: RequestBuilder) {

    fun sendGetIssueTypesRequest(): MutableLiveData<NetworkResponse<Array<IssueTypeRemote>>> {

        val result = MutableLiveData<NetworkResponse<Array<IssueTypeRemote>>>()

        val transaction: HttpNetworkTransaction<JsonArray, Array<IssueTypeRemote>> = object : HttpNetworkTransaction<JsonArray, Array<IssueTypeRemote>>(Array<IssueTypeRemote>::class.java) {

            override fun createRequest(): Request<*> =
                this@IssueTypeTransactions.requestBuilder.buildJsonArrayHttpRequest(Request.Method.GET, "${BuildConfig.END_POINT__API_POSTFIX}issue-types", responseHandler = this.responseHandler)

            override fun onTransactionStarted() {}
            override fun onTransactionUnauthorizedResult() {}
            override fun onTransactionErrorResult(errorCode: Int) {
                NetworkResponse<Array<IssueTypeRemote>>().let {
                    result.postValue(it)
                }
            }

            override fun onTransactionSuccessResult(responseBody: Array<IssueTypeRemote>?) {
                responseBody?.map { IssueTypeLocal(it) }?.let {
                    Session.application.dataManager.issueTypeOperations.saveIssueTypes(it)
                }
                result.postValue(NetworkResponse(responseBody))
            }
        }

        this.server.executeTransaction(Server.CommunicationChannelType.HTTP, transaction)
        return result
    }
}