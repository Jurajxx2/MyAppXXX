package com.trasim.myapp.widgets.galleryRow

import android.content.Context
import android.util.AttributeSet
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.trasim.myapp.R
import com.trasim.myapp.Session
import kotlinx.android.synthetic.main.custom__gallery_photo_view.view.*
import java.io.File
import java.net.URI

class GalleryPhoto : GalleryImagePreView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(this.context, R.layout.custom__gallery_photo_view, this)
    }

    override fun setup(urlOrUri: String, isLocal: Boolean) {
        if (isLocal) {
            Glide.with(this).load(File(URI(urlOrUri).path)).into(imagePreviewIV)
        } else {
            Session.application.sessionStorage.userLocal?.token?.let { token ->
                Session.application.sessionStorage.lastCookie?.let { cookie ->
                    val glideUrl = GlideUrl(urlOrUri, LazyHeaders.Builder().addHeader("Authorization", token).addHeader("Cookie", cookie).build())
                    Glide.with(this).load(glideUrl).into(imagePreviewIV)
                }
            }
        }
        imagePreviewIV.setOnClickListener { handler?.onImageClick(urlOrUri, position) }
    }
}