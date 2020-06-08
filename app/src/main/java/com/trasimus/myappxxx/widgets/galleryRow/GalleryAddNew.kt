package com.trasim.myapp.widgets.galleryRow

import android.content.Context
import android.util.AttributeSet
import com.trasim.myapp.R
import kotlinx.android.synthetic.main.custom__gallery_add_new_view.view.*

class GalleryAddNew : GalleryImagePreView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(this.context, R.layout.custom__gallery_add_new_view, this)
        addNewHolderCV.setOnClickListener { handler?.onAddNewClick() }
    }
}