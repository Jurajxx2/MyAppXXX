package com.trasim.myapp.widgets.graph

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.LinearLayout
import com.trasim.myapp.R
import kotlinx.android.synthetic.main.custom__graph_view.view.*

class GraphView : LinearLayout {

    var percentageValue: Int = 0
        set(value) {
            field = value
            progressGBV.setPercentageValue(field)
            percentageValueTV.text = String.format(this.context.getString(R.string._percentage_template), field)
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(this.context, R.layout.custom__graph_view, this)
        this.percentageValue = 0
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val graphSavedState = GraphSavedState(superState)
        graphSavedState.percentageValue = this.percentageValue
        return graphSavedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is GraphSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        this.percentageValue = state.percentageValue
    }

    internal class GraphSavedState : BaseSavedState {
        var percentageValue: Int = 0

        constructor(superState: Parcelable) : super(superState)
        private constructor(parcel: Parcel) : super(parcel) {
            this.percentageValue = parcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(this.percentageValue)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<GraphSavedState?> = object : Parcelable.Creator<GraphSavedState?> {
                override fun createFromParcel(parcel: Parcel): GraphSavedState {
                    return GraphSavedState(parcel)
                }

                override fun newArray(size: Int): Array<GraphSavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}