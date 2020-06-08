package com.trasim.myapp.data.entities.issue.models.gallery

import com.google.gson.annotations.SerializedName

class GalleryItem(
    @SerializedName("item type")
    val type: String,
    @SerializedName("source")
    val source: String
)
