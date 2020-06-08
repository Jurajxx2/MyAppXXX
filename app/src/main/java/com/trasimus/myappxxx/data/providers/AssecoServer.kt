package com.trasim.myapp.data.providers

import com.trasim.myapp.data.entities.issue.remote.IssueTransactions
import com.trasim.myapp.data.entities.issueType.remote.IssueTypeTransactions
import com.trasim.myapp.data.entities.user.remote.UserTransactions
import com.trasim.base.data.remote.Server
import com.trasim.base.data.remote.transaction.channels.CommunicationChannel
import com.trasim.base.data.remote.transaction.channels.http.HttpChannel

class MyAppServer(urlProvider: CommunicationChannel.UrlProvider, private val appModuleInterface: CommunicationChannel.AppModuleInterface) : Server(urlProvider) {

    val userTransactions = UserTransactions(this, this.requestBuilder)
    val issueTransactions = IssueTransactions(this, this.requestBuilder)
    val issueTypeTransactions = IssueTypeTransactions(this, this.requestBuilder)

    override val communicationChannels: HashMap<Int, CommunicationChannel> = createCommunicationChannels()

    override fun createCommunicationChannels(): HashMap<Int, CommunicationChannel> {
        val channels: HashMap<Int, CommunicationChannel> = hashMapOf()
        channels[CommunicationChannelType.HTTP.communicationChannelId] = HttpChannel(this.appModuleInterface)
        return channels
    }

    override fun setCommunicationChannels() = this.communicationChannels
}