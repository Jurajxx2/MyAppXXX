package com.trasim.myapp.widgets.galleryRow

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

abstract class GalleryImagePreView : LinearLayout {
    var handler: GalleryRowView.GalleryRowHandler? = null
    var position = 0
    open fun setup(urlOrUri: String, isLocal: Boolean) {}

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}