package com.trasim.myapp.data.entities.internal

import android.content.Context
import com.trasim.myapp.data.entities.internal.local.InternalQueries
import com.trasim.myapp.data.providers.MyAppLocalStorage

class InternalOperations(localStorage: MyAppLocalStorage) {

    private val internalQueries = localStorage.internalQueries

    fun loadAddress(context: Context, connectionMethod: InternalQueries.ConnectionMethod = loadConnectionMethod(context)) =
        when (connectionMethod) {
            InternalQueries.ConnectionMethod.IP -> this.loadIP(context)
            InternalQueries.ConnectionMethod.URL -> this.loadURL(context)
        }

    fun storeAddress(value: String, context: Context, connectionMethod: InternalQueries.ConnectionMethod = loadConnectionMethod(context)) =
        when (connectionMethod) {
            InternalQueries.ConnectionMethod.IP -> this.storeIP(value, context)
            InternalQueries.ConnectionMethod.URL -> this.storeURL(value, context)
        }

    fun storeConnectionMethod(value: InternalQueries.ConnectionMethod, context: Context) = this.internalQueries.storeConnectionMethod(value, context)

    fun loadConnectionMethod(context: Context) = this.internalQueries.loadConnectionMethod(context)

    private fun storeURL(value: String, context: Context) = this.internalQueries.storeURL(value, context)

    private fun loadURL(context: Context) = this.internalQueries.loadURL(context)

    private fun storeIP(value: String, context: Context) = this.internalQueries.storeIP(value, context)

    private fun loadIP(context: Context) = this.internalQueries.loadIP(context)

    fun storePort(value: String, context: Context) = this.internalQueries.storePort(value, context)

    fun loadPort(context: Context) = this.internalQueries.loadPort(context)

    fun storeBackgroundedTime(context: Context) = this.internalQueries.storeBackgroundedTime(context)

    fun deleteBackgroundedTime(context: Context) = this.internalQueries.deleteBackgroundedTime(context)

    fun loadBackgroundedTime(context: Context) = this.internalQueries.loadBackgroundedTime(context)
}