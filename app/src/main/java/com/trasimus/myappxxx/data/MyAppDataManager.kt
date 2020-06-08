package com.trasim.myapp.data

import android.content.Context
import com.trasim.myapp.Session
import com.trasim.myapp.data.entities.internal.InternalOperations
import com.trasim.myapp.data.entities.issue.IssueOperations
import com.trasim.myapp.data.entities.issueType.IssueTypeOperations
import com.trasim.myapp.data.entities.user.UserOperations
import com.trasim.myapp.data.providers.MyAppLocalStorage
import com.trasim.myapp.data.providers.MyAppServer
import com.trasim.base.data.remote.transaction.channels.CommunicationChannel

class MyAppDataManager {

    private val server = MyAppServer(this.createUrlProvider(), this.createJwtTokenProvider())
    private val localStorage = MyAppLocalStorage()

    val userOperations = UserOperations(this.localStorage, this.server)
    val issuesOperations = IssueOperations(this.localStorage, this.server)
    val issueTypeOperations = IssueTypeOperations(this.localStorage, this.server)
    val internalOperations = InternalOperations(this.localStorage)

    private fun createJwtTokenProvider(): CommunicationChannel.AppModuleInterface =
        object : CommunicationChannel.AppModuleInterface {
            override fun setCookie(cookie: String) {
                Session.application.sessionStorage.lastCookie = cookie
            }

            override fun getJwtToken(): String? = this@MyAppDataManager.userOperations.getUserSync()?.token
        }

    private fun createUrlProvider(): CommunicationChannel.UrlProvider =
        object : CommunicationChannel.UrlProvider {
            override fun getUrl(): String =
                "${Session.application.dataManager.internalOperations.loadAddress(Session.application)}:${Session.application.dataManager.internalOperations.loadPort(Session.application)}"

        }

    fun connect(context: Context) = this.server.connect(context)

    fun disconnect() = this.server.disconnect()
}