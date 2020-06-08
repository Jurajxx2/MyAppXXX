package com.trasim.myapp

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.trasim.myapp.data.MyAppDataManager
import com.trasim.myapp.data.entities.user.UserLocal
import com.trasim.myapp.routing.MyAppRouter
import com.trasim.myapp.screens._starterAndPermissionsActivity.StarterAndPermissionsActivity
import org.joda.time.DateTime

private const val MAX_BACKGROUND_TIME = 5

class Session : MultiDexApplication() {

    val dataManager: MyAppDataManager by lazy { MyAppDataManager() }
    val router: MyAppRouter by lazy { MyAppRouter() }
    val sessionStorage = SessionStorage()

    private val lifeCycleObserver = object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onApplicationCreated() {
            this@Session.dataManager.connect(this@Session)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onApplicationStarted() {
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onApplicationForegrounded() {
            Session.application.dataManager.internalOperations.loadBackgroundedTime(application)?.let { lastBackgroundedTime ->
                this@Session.sessionStorage.logoutUserIndicator = lastBackgroundedTime.isBefore(DateTime.now().minusMinutes(MAX_BACKGROUND_TIME))
            }

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onApplicationBackgrounded() {
            Session.application.dataManager.internalOperations.storeBackgroundedTime(application)

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onApplicationPaused() {
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onApplicationDestroyed() {
            this@Session.dataManager.disconnect()
        }
    }

    companion object {
        lateinit var application: Session
            private set
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this.lifeCycleObserver)
    }

    fun clearDataAndRestartApplication(activity: FragmentActivity) {
        Thread(Runnable {
            this@Session.dataManager.disconnect()
            Session.application.dataManager.internalOperations.deleteBackgroundedTime(application)
            this.dataManager.userOperations.deleteAllUsers()
            this.dataManager.issuesOperations.deleteAllIssues()
            this.sessionStorage.logoutUserIndicator = false
            activity.finishAffinity()

            val intent = Intent(activity, StarterAndPermissionsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
            this@Session.dataManager.connect(this@Session)
        }).start()
    }

    inner class SessionStorage(var logoutUserIndicator: Boolean = false, var userLocal: UserLocal? = null, var lastCookie: String? = null)
}