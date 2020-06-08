package com.trasim.myapp.widgets

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.LinearLayout
import com.trasim.myapp.R
import kotlinx.android.synthetic.main.custom__counter_view.view.*

class CounterView : LinearLayout {

    var currentValue = 0
        set(value) {
            field = value
            counterTV.text = currentValue.toString()
            manageButtons()
        }
    var minValue = 0
        set(value) {
            field = value
            manageButtons()
        }
    var maxValue = 0
        set(value) {
            field = value
            manageButtons()
        }
    var handler: CounterHandler? = null
    var position = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(this.context, R.layout.custom__counter_view, this)

        plusIV.setOnClickListener {
            add()
        }

        minusIV.setOnClickListener {
            substract()
        }
    }

    private fun add() {
        //minusIV.alpha = 1f
        if (currentValue < maxValue) {
            currentValue++
            counterTV.text = currentValue.toString()
            handler?.onValueChanged(currentValue, position)
        }
        manageButtons()
    }

    private fun substract() {
        //plusIV.alpha = 1f
        if (currentValue > minValue) {
            currentValue--
            counterTV.text = currentValue.toString()
            handler?.onValueChanged(currentValue, position)
        }
        manageButtons()
    }

    private fun manageButtons() {
        plusIV.alpha = if (currentValue == maxValue) {
            0.5f
        } else {
            1f
        }

        minusIV.alpha = if (currentValue == minValue) {
            0.5f
        } else {
            1f
        }
    }

    interface CounterHandler {
        fun onValueChanged(value: Int, position: Int)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val counterSavedState = CounterSavedState(superState)
        counterSavedState.currentValue = this.currentValue
        return counterSavedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is CounterSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        this.currentValue = state.currentValue
        this.maxValue = state.maxValue
        this.minValue = state.minValue
    }

    internal class CounterSavedState : BaseSavedState {
        var currentValue: Int = 0
        var maxValue: Int = 0
        var minValue: Int = 0

        constructor(superState: Parcelable) : super(superState)
        private constructor(parcel: Parcel) : super(parcel) {
            this.currentValue = parcel.readInt()
            this.maxValue = parcel.readInt()
            this.minValue = parcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(this.currentValue)
            out.writeInt(this.maxValue)
            out.writeInt(this.minValue)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<CounterSavedState?> = object : Parcelable.Creator<CounterSavedState?> {
                override fun createFromParcel(parcel: Parcel): CounterSavedState {
                    return CounterSavedState(parcel)
                }

                override fun newArray(size: Int): Array<CounterSavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}