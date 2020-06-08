package com.trasim.myapp.data.entities.issue.models.issueMeta

import com.google.gson.annotations.SerializedName
import com.trasim.myapp.data.entities.issue.models.gallery.GalleryItem

class IssueMeta(@SerializedName("gallery") val gallery: Array<GalleryItem>)