package com.trasim.myapp.data.entities.issue.remote

import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.trasim.myapp.BuildConfig
import com.trasim.myapp.Session
import com.trasim.myapp.data.entities.issue.IssueLocal
import com.trasim.myapp.data.entities.issue.IssueRemote
import com.trasim.myapp.data.entities.issue.Priority
import com.trasim.myapp.data.entities.issue.models.checkList.CheckList
import com.trasim.myapp.data.entities.issue.models.issueMeta.IssueMeta
import com.trasim.myapp.data.entities.issue.models.permission.Permission
import com.trasim.myapp.data.entities.issue.models.room.Room
import com.trasim.myapp.data.entities.user.UserRemote
import com.trasim.base.data.remote.Server
import com.trasim.base.data.remote.transaction.NetworkResponse
import com.trasim.base.data.remote.transaction.processing.RequestData
import com.trasim.base.data.remote.transaction.processing.http.HttpNetworkTransaction
import com.trasim.base.data.remote.transaction.request.RequestBuilder
import com.trasim.base.data.remote.transaction.request.http.MultipartRequest
import java.util.*

class IssueTransactions(private val server: Server, private val requestBuilder: RequestBuilder) {

    fun sendGetIssuesRequest(): MutableLiveData<NetworkResponse<Array<IssueRemote>>> {

        val result = MutableLiveData<NetworkResponse<Array<IssueRemote>>>()

        val transaction: HttpNetworkTransaction<JsonArray, Array<IssueRemote>> = object : HttpNetworkTransaction<JsonArray, Array<IssueRemote>>(Array<IssueRemote>::class.java) {

            override fun createRequest(): Request<*> =
                this@IssueTransactions.requestBuilder.buildJsonArrayHttpRequest(Request.Method.GET, "${BuildConfig.END_POINT__API_POSTFIX}issues", responseHandler = this.responseHandler)

            override fun onTransactionStarted() {}
            override fun onTransactionUnauthorizedResult() {}
            override fun onTransactionErrorResult(errorCode: Int) {
                NetworkResponse<Array<IssueRemote>>().let {
                    result.postValue(it)
                }
            }

            override fun onTransactionSuccessResult(responseBody: Array<IssueRemote>?) {
                responseBody?.map { IssueLocal(it) }?.let {
                    Session.application.dataManager.issuesOperations.saveIssues(it)
                }
                result.postValue(NetworkResponse(responseBody))
            }
        }

        this.server.executeTransaction(Server.CommunicationChannelType.HTTP, transaction)
        return result
    }

    class UpdateIssueRequestBody(
        id: String,
        name: String,
        description: String,
        note: String?,
        priority: String,
        state: String, room: Room?,
        type: String,
        meta: IssueMeta?, permissions: Array<Permission>?,
        assignee: UserRemote,
        owner: UserRemote?,
        checkout: Boolean,
        checklists: Array<CheckList>) : IssueRemote(id, name, description, note, priority, state, room, type, meta, permissions, assignee, owner, checkout, checklists
    ), RequestData.RequestBody {

        constructor(issueRemote: IssueRemote) : this(
            issueRemote.id,
            issueRemote.name,
            issueRemote.description,
            issueRemote.note,
            issueRemote.priority,
            issueRemote.state, issueRemote.room,
            issueRemote.type,
            issueRemote.meta, issueRemote.permissions,
            issueRemote.assignee,
            issueRemote.owner,
            issueRemote.checkout,
            issueRemote.checklists
        )
    }

    fun sendUpdateIssue(updateIssueRequestBody: UpdateIssueRequestBody): MutableLiveData<NetworkResponse<IssueRemote>> {

        val result = MutableLiveData<NetworkResponse<IssueRemote>>()

        val transaction: HttpNetworkTransaction<JsonObject, IssueRemote> = object : HttpNetworkTransaction<JsonObject, IssueRemote>(IssueRemote::class.java) {
            override fun createRequest(): Request<*> =
                this@IssueTransactions.requestBuilder.buildJsonObjectHttpRequest(Request.Method.PUT, "${BuildConfig.END_POINT__API_POSTFIX}issues/${updateIssueRequestBody.id}", updateIssueRequestBody, responseHandler = this.responseHandler)

            override fun onTransactionStarted() {}
            override fun onTransactionUnauthorizedResult() {}
            override fun onTransactionErrorResult(errorCode: Int) {
                NetworkResponse<IssueRemote>().let {
                    result.postValue(it)
                }
            }

            override fun onTransactionSuccessResult(responseBody: IssueRemote?) {
                responseBody?.let {
                    Session.application.dataManager.issuesOperations.saveIssue(IssueLocal(it))
                }
                result.postValue(NetworkResponse(responseBody))
            }
        }

        this.server.executeTransaction(Server.CommunicationChannelType.HTTP, transaction)
        return result
    }

    class CreateIssueRequestBody(@SerializedName("name") val name: String, @SerializedName("description") val description: String, @SerializedName("type") val issueType: String, @SerializedName("note") val note: String, @SerializedName("priority") val priority: String = Priority.NORMAL.serializedName, @SerializedName("checklists") val checkLists: Array<CheckList> = arrayOf()
    ) : RequestData.RequestBody

    fun sendCreateIssue(requestBody: CreateIssueRequestBody): MutableLiveData<NetworkResponse<IssueRemote>> {

        val result = MutableLiveData<NetworkResponse<IssueRemote>>()

        val transaction: HttpNetworkTransaction<JsonObject, IssueRemote> = object : HttpNetworkTransaction<JsonObject, IssueRemote>(IssueRemote::class.java) {
            override fun createRequest(): Request<*> =
                this@IssueTransactions.requestBuilder.buildJsonObjectHttpRequest(
                    Request.Method.POST, "${BuildConfig.END_POINT__API_POSTFIX}issues", requestBody, responseHandler = this.responseHandler
                )

            override fun onTransactionStarted() {}
            override fun onTransactionUnauthorizedResult() {}
            override fun onTransactionErrorResult(errorCode: Int) {
                NetworkResponse<IssueRemote>().let {
                    result.postValue(it)
                }
            }

            override fun onTransactionSuccessResult(responseBody: IssueRemote?) {
                responseBody?.let {
                    Session.application.dataManager.issuesOperations.saveIssue(IssueLocal(it))
                }
                result.postValue(NetworkResponse(responseBody))
            }
        }

        this.server.executeTransaction(Server.CommunicationChannelType.HTTP, transaction)
        return result
    }

    fun sendCreateIssueImage(token: String, id: String, imageBytes: ByteArray): MutableLiveData<NetworkResponse<Any?>> {

        val result = MutableLiveData<NetworkResponse<Any?>>()

        val transaction = object : HttpNetworkTransaction<String, Any>(Any::class.java) {

            override fun createRequest(): Request<*> {

                val multipartRequestHeaders = HashMap<String, String>()
                multipartRequestHeaders["Authorization"] = token

                val multipartRequestBody = HashMap<String, MultipartRequest.DataPart>()
                multipartRequestBody["data"] = MultipartRequest.DataPart(fileName = id, content = imageBytes, type = "image/jpeg")

                val request = this@IssueTransactions.requestBuilder.buildMultipartHttpRequest(Request.Method.POST, "${BuildConfig.END_POINT__API_POSTFIX}image/$id", responseHandler = this.responseHandler, multipartRequestData = multipartRequestBody)
                return request
            }

            override fun onTransactionStarted() {
            }

            override fun onTransactionErrorResult(errorCode: Int) {
                NetworkResponse<Any?>().let {
                    result.postValue(it)
                }
            }

            override fun onTransactionSuccessResult(responseBody: Any?) {
                result.postValue(NetworkResponse(responseBody))
            }
        }

        this.server.executeTransaction(Server.CommunicationChannelType.HTTP, transaction)
        return result
    }
}