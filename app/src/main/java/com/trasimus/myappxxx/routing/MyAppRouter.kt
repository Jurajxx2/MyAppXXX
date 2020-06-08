package com.trasim.myapp.routing

import android.app.Activity
import android.content.Intent
import com.trasim.myapp.screens.issues.activity.IssuesActivity
import com.trasim.myapp.screens.pin.activity.PinActivity
import com.trasim.myapp.screens.reportProblem.activity.NAME__EXTRAS_KEY
import com.trasim.myapp.screens.reportProblem.activity.ReportProblemActivity
import com.trasim.base.routing.Router

class MyAppRouter : Router() {

    fun startPinActivity(activity: Activity) = super.startActivity(activity, Intent(activity, PinActivity::class.java), true)

    fun startIssuesActivity(activity: Activity) = super.startActivity(activity, Intent(activity, IssuesActivity::class.java), true)

    fun startReportProblemActivity(name: String, activity: Activity, finishStarterActivity: Boolean = false) {
        val intent = Intent(activity, ReportProblemActivity::class.java)
        intent.putExtra(NAME__EXTRAS_KEY, name)
        super.startActivity(activity, intent, finishStarterActivity)
    }
}