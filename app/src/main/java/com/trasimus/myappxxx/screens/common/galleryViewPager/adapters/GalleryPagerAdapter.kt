package com.trasim.myapp.screens.common.galleryViewPager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.trasim.myapp.R
import com.trasim.myapp.Session
import kotlinx.android.synthetic.main.issues__gallery_view_pager_item.view.*
import java.io.File
import java.net.URI


class GalleryPagerAdapter(private val isLocal: Boolean, val urlOrUriList: MutableList<String>) : PagerAdapter() {

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any as ConstraintLayout
    }

    override fun getCount() = urlOrUriList.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(container.context).inflate(R.layout.issues__gallery_view_pager_item, container, false)
        val urlOrUri = urlOrUriList[position]
        val galleryItemIV = itemView.galleryItemIV

        if (isLocal) {
            Glide.with(container.context).load(File(URI(urlOrUri).path)).into(galleryItemIV)
        } else {
            Session.application.sessionStorage.userLocal?.token?.let { token ->
                Session.application.sessionStorage.lastCookie?.let { cookie ->
                    val glideUrl = GlideUrl(urlOrUri, LazyHeaders.Builder().addHeader("Authorization", token).addHeader("Cookie", cookie).build())
                    Glide.with(container.context).load(glideUrl).into(galleryItemIV)
                }
            }
        }

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as ConstraintLayout)
    }

}