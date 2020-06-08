package com.trasim.myapp.data.entities.issue.models.room

import com.google.gson.annotations.SerializedName

class Room(@SerializedName("id") val id: Int, @SerializedName("roomNumber") val roomNumber: String, @SerializedName("roomType") val roomType: String)