package com.trasim.myapp.widgets

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.trasim.myapp.R
import kotlinx.android.synthetic.main.custom__pin_view.view.*

class PinView : LinearLayout {

    private var pin = ""
    private val pinViews by lazy {
        mutableListOf<AppCompatTextView>(
            firstPinNumberTV,
            secondPinNumberTV,
            thirdPinNumberTV,
            fourthPinNumberTV
        )
    }

    var pinViewListener: PinViewListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(this.context, R.layout.custom__pin_view, this)
        this.fillPinValue()
    }

    fun addNewPinNumber(number: Int) {
        if (this.pin.length < this.pinViews.size) {
            this.pin = this.pin.plus(number)
            this.fillPinValue()
        }
    }

    fun deleteLastPinNumber() {
        if (this.pin.isNotEmpty()) {
            this.pin = this.pin.substring(0, this.pin.length - 1)
            this.fillPinValue()
        }
    }

    fun clearPin() {
        this.pin = ""
        this.fillPinValue()
    }

    private fun fillPinValue() {
        this.pinViews.forEachIndexed { index, pinView ->
            pinView.text = if (this.pin.length > index) {
                this.pin[index].toString()
            } else {
                ""
            }
        }

        if (this.pin.length == this.pinViews.size) {
            this.pinViewListener?.onPinComplete(this.pin)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val graphSavedState = PinSavedState(superState)
        graphSavedState.pin = this.pin
        return graphSavedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is PinSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        this.pin = state.pin
        this.fillPinValue()
    }

    internal class PinSavedState : BaseSavedState {
        var pin: String = ""

        constructor(superState: Parcelable) : super(superState)
        private constructor(parcel: Parcel) : super(parcel) {
            this.pin = parcel.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(this.pin)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<PinSavedState?> = object : Parcelable.Creator<PinSavedState?> {
                override fun createFromParcel(parcel: Parcel): PinSavedState {
                    return PinSavedState(parcel)
                }

                override fun newArray(size: Int): Array<PinSavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    interface PinViewListener {
        fun onPinComplete(pin: String)
    }
}