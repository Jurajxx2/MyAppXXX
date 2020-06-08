package com.trasim.myapp.screens.common.galleryViewPager.handlers

interface GalleryPagerHandler {
    fun onBackTextClick()
    fun onDeletePhotoClick(path: String, position: Int)
}