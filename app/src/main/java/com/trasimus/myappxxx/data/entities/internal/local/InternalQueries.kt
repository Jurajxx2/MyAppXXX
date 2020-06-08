package com.trasim.myapp.data.entities.internal.local

import android.content.Context
import com.trasim.myapp.BuildConfig
import com.trasim.base.data.local.channels.SharedPreferencesManager
import org.joda.time.DateTime

class InternalQueries(private val sharedPreferencesManager: SharedPreferencesManager) {

    fun storeConnectionMethod(connectionMethod: ConnectionMethod, context: Context) = this.sharedPreferencesManager.storeConnectionMethod(connectionMethod.connectionMethodName, context)

    fun loadConnectionMethod(context: Context): ConnectionMethod {

        val loadedConnectionMethod = this.sharedPreferencesManager.loadConnectionMethod(context)

        val connectionMethod = if (loadedConnectionMethod.isNullOrEmpty()) {
            ConnectionMethod.URL?.apply { this@InternalQueries.storeConnectionMethod(this, context) }
        } else {
            ConnectionMethod.findConnectionTypeByName(loadedConnectionMethod) ?: ConnectionMethod.URL
        }

        return connectionMethod
    }

    fun storeURL(value: String, context: Context) = this.sharedPreferencesManager.storeURL(value, context)

    fun loadURL(context: Context): String {

        val loadedUrl = this.sharedPreferencesManager.loadURL(context)

        val url = if (loadedUrl.isNullOrEmpty()) {
            BuildConfig.SERVER__URL.apply { this@InternalQueries.storeURL(this, context) }
        } else {
            loadedUrl
        }

        return url
    }

    fun storeIP(value: String, context: Context) = this.sharedPreferencesManager.storeIP(value, context)

    fun loadIP(context: Context) = this.sharedPreferencesManager.loadIP(context)

    fun storePort(value: String, context: Context) = this.sharedPreferencesManager.storePort(value, context)

    fun loadPort(context: Context): String {

        val loadedPort = this.sharedPreferencesManager.loadPort(context)

        val port = if (loadedPort.isNullOrEmpty()) {
            BuildConfig.SERVER__PORT.apply { this@InternalQueries.storePort(this, context) }
        } else {
            loadedPort
        }

        return port
    }

    fun storeBackgroundedTime(context: Context) =
        this.sharedPreferencesManager.storeBackgroundedTime(DateTime.now().millis, context)

    fun deleteBackgroundedTime(context: Context) = this.sharedPreferencesManager.deleteBackgroundedTime(context)

    fun loadBackgroundedTime(context: Context): DateTime? {
        val lastBackgroundedTime = this.sharedPreferencesManager.loadBackgroundedTime(context)
        return if (lastBackgroundedTime != 0L) {
            return DateTime(lastBackgroundedTime)
        } else {
            null
        }
    }

    enum class ConnectionMethod(val connectionMethodName: String) {
        IP("ip"), URL("url");

        companion object {
            fun findConnectionTypeByName(name: String) = values().firstOrNull { it.connectionMethodName == name }
        }
    }
}