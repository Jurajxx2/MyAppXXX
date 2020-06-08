package com.trasim.myapp.screens.common.galleryViewPager

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.trasim.myapp.R
import com.trasim.myapp.base.fragment.MyAppFragment
import com.trasim.myapp.screens.common.galleryViewPager.adapters.GalleryPagerAdapter
import com.trasim.myapp.screens.common.galleryViewPager.handlers.GalleryPagerHandler
import com.trasim.myapp.widgets.galleryRow.GalleryRowView
import com.trasim.base.screens.components.BaseViews
import kotlinx.android.synthetic.main.issues__gallery_view_pager__fragment.*

class GalleryViewPagerFragment : MyAppFragment<Nothing, Nothing, GalleryViewPagerFragment.Views, GalleryPagerHandler>(), GalleryRowView.GalleryRowHandler {

    val args: GalleryViewPagerFragmentArgs by navArgs()

    private lateinit var urlOrUriList: MutableList<String>

    override fun setFragmentLayout() = R.layout.issues__gallery_view_pager__fragment

    override fun initiateViews(): Views = Views()

    inner class Views : BaseViews {
        override fun setupViewModel() {
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {

            urlOrUriList = args.urlOrUriList.toMutableList()

            if (args.isDeletable) {
                deleteIV.setOnClickListener {
                    handler.onDeletePhotoClick(urlOrUriList[galleryVP.currentItem], galleryVP.currentItem)
                }
            } else {
                deleteIV.visibility = View.GONE
            }

            val selectedPhoto = args.position

            val pagerAdapter = GalleryPagerAdapter(args.isDeletable, urlOrUriList)
            galleryVP.adapter = pagerAdapter

            galleryVP.currentItem = selectedPhoto

            photoPreviewsGRV.imgWidth = 40
            photoPreviewsGRV.imgHeight = 40
            photoPreviewsGRV.handler = this@GalleryViewPagerFragment
            photoPreviewsGRV.mode = if (args.isDeletable) {
                GalleryRowView.Mode.LOCAL
            } else {
                GalleryRowView.Mode.REMOTE
            }
            photoPreviewsGRV.imageList = urlOrUriList


            goBackTV.setOnClickListener {
                handler.onBackTextClick()
            }

            nextIV.setOnClickListener {
                galleryVP.currentItem = galleryVP.currentItem + 1
            }

            prevIV.setOnClickListener {
                galleryVP.currentItem = galleryVP.currentItem - 1
            }
        }
    }

    override fun onAddNewClick() {}

    override fun onImageClick(id: String, position: Int) {
        galleryVP.currentItem = position
    }
}