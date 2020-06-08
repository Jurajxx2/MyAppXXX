package com.trasim.myapp.data.entities.user

import com.trasim.myapp.data.providers.MyAppLocalStorage
import com.trasim.myapp.data.providers.MyAppServer

class UserOperations(localStorage: MyAppLocalStorage, server: MyAppServer) {

    private val queries = localStorage.userQueries
    private val transactions = server.userTransactions

    fun sendLoginRequest(pin: String) = this.transactions.sendLoginRequest(pin)

    fun saveUser(userLocal: UserLocal) = this.queries.saveUser(userLocal)
    fun getUserSync() = this.queries.getUser()
    fun deleteAllUsers() = this.queries.deleteAllUsers()
}