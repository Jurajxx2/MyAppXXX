package com.trasim.myapp.data.entities.user.remote

import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.trasim.myapp.BuildConfig
import com.trasim.myapp.Session
import com.trasim.myapp.data.entities.user.UserLocal
import com.trasim.myapp.data.entities.user.UserRemote
import com.trasim.base.data.remote.Server
import com.trasim.base.data.remote.transaction.NetworkResponse
import com.trasim.base.data.remote.transaction.TransactionResponseHandler
import com.trasim.base.data.remote.transaction.processing.RequestData
import com.trasim.base.data.remote.transaction.processing.http.HttpNetworkTransaction
import com.trasim.base.data.remote.transaction.request.RequestBuilder
import com.trasim.base.data.remote.transaction.request.http.JsonObjectRequest

class UserTransactions(private val server: Server, private val requestBuilder: RequestBuilder) {

    class LoginRequestBody(@SerializedName("PIN") val pin: String) : RequestData.RequestBody

    fun sendLoginRequest(pin: String): MutableLiveData<NetworkResponse<UserRemote>> {

        val result = MutableLiveData<NetworkResponse<UserRemote>>()

        val transaction: HttpNetworkTransaction<JsonObject, UserRemote> = object : HttpNetworkTransaction<JsonObject, UserRemote>(UserRemote::class.java) {

            override fun createRequest(): Request<*> =
                this@UserTransactions.requestBuilder.buildJsonObjectHttpRequest(Request.Method.POST, "${BuildConfig.END_POINT__API_POSTFIX}login", LoginRequestBody(pin), this.responseHandler)

            override fun onTransactionStarted() {}
            override fun onTransactionUnauthorizedResult() {
                NetworkResponse<UserRemote>(null, TransactionResponseHandler.ResponseStatus.UNAUTHORIZED).let {
                    result.postValue(it)
                }
            }
            override fun onTransactionErrorResult(errorCode: Int) {
                NetworkResponse<UserRemote>().let {
                    result.postValue(it)
                }
            }

            override fun onTransactionSuccessResult(responseBody: UserRemote?) {
                responseBody?.let { userRemote ->
                    (this.request as JsonObjectRequest).responseHeaders["Authorization"]?.let {
                        userRemote.token = it
                    }

                    UserLocal(userRemote).apply {
                        Session.application.sessionStorage.userLocal = this
                        Session.application.dataManager.userOperations.saveUser(this)
                    }
                }
                result.postValue(NetworkResponse(responseBody))
            }
        }

        this.server.executeTransaction(Server.CommunicationChannelType.HTTP, transaction)

        return result
    }
}