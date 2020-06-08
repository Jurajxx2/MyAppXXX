package com.trasim.myapp.data.entities.issue.models.permission

import com.google.gson.annotations.SerializedName

class Permission(
    @SerializedName("current")
    val current: String,
    @SerializedName("allowed")
    val allowed: Array<String>
)