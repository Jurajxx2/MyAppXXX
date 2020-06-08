package com.trasim.myapp.widgets.galleryRow

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.trasim.myapp.R
import com.trasim.base.helpers.dpToPixels
import kotlinx.android.synthetic.main.custom__gallery_row_view.view.*


class GalleryRowView : LinearLayout {

    var handler: GalleryRowHandler? = null
    var mode = Mode.REMOTE
    var imageList: List<String>? = null
        set(value) {
            field = value
            field?.let { inflateURLs(it) }
        }
    var imgHeight = 80
    var imgWidth = 80

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(this.context, R.layout.custom__gallery_row_view, this)
    }

    fun resetLayout() {
        galleryHolderRL.removeAllViews()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val gallerySavedState = GallerySavedState(superState)
        this.imageList = gallerySavedState.imageList
        return gallerySavedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is GallerySavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        this.imageList = state.imageList
    }

    private fun inflateURLs(urlOrUriList: List<String>) {
        var idArrayList = arrayListOf<GalleryImagePreView>()

        if (mode == Mode.LOCAL_EDITABLE) {
            val addNewLayout = GalleryAddNew(context)
            addNewLayout.handler = handler
            idArrayList = addNewLayout(addNewLayout, idArrayList)
        }

        for ((index, urlOrUri) in urlOrUriList.withIndex()){
            var addedLayout: GalleryImagePreView?
            addedLayout = GalleryPhoto(context)
            addedLayout.position = index
            addedLayout.handler = handler
            addedLayout.setup(urlOrUri, mode == Mode.LOCAL || mode == Mode.LOCAL_EDITABLE)
            idArrayList = addNewLayout(addedLayout, idArrayList)
        }
    }

    private fun addNewLayout(addedLayout: GalleryImagePreView, idArrayList: ArrayList<GalleryImagePreView>): ArrayList<GalleryImagePreView> {
        val params = RelativeLayout.LayoutParams(dpToPixels(imgHeight, context), dpToPixels(imgWidth, context))
        if (idArrayList.isNotEmpty()) {
            params.marginStart = dpToPixels(10, context)
            params.addRule(RelativeLayout.RIGHT_OF, idArrayList[idArrayList.size - 1].id)
        }
        addedLayout.id = View.generateViewId()
        idArrayList.add(addedLayout)
        galleryHolderRL.addView(addedLayout, params)
        return idArrayList
    }

    interface GalleryRowHandler {
        fun onImageClick(id: String, position: Int)
        fun onAddNewClick()
    }

    enum class Mode {
        LOCAL,
        LOCAL_EDITABLE,
        REMOTE;
    }

    internal class GallerySavedState : BaseSavedState {
        var imageList: List<String> = arrayListOf()

        constructor(superState: Parcelable) : super(superState)
        private constructor(parcel: Parcel) : super(parcel) {
            parcel.readList(this.imageList, String::class.java.classLoader)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeList(this.imageList)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<GallerySavedState?> = object : Parcelable.Creator<GallerySavedState?> {
                override fun createFromParcel(parcel: Parcel): GallerySavedState {
                    return GallerySavedState(parcel)
                }

                override fun newArray(size: Int): Array<GallerySavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}