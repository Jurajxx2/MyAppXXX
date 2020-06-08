package com.trasim.myapp.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.StringRes
import com.trasim.myapp.R
import kotlinx.android.synthetic.main.custom__switch_view.view.*

class SwitchView : LinearLayout {

    private var switchListener: SwitchListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(this.context, R.layout.custom__switch_view, this)

        switchLeftRB.setOnClickListener {
            this.switchListener?.onFirstButtonClicked()
        }

        switchRightRB.setOnClickListener {
            this.switchListener?.onSecondButtonClicked()
        }
    }

    fun setup(switchListener: SwitchListener, @StringRes firstButtonText: Int, @StringRes secondButtonText: Int) {
        this.switchListener = switchListener
        switchLeftRB.text = context.getString(firstButtonText)
        switchRightRB.text = context.getString(secondButtonText)
    }

    fun selectFirstButton() = switchLeftRB.performClick()
    fun selectSecondButton() = switchRightRB.performClick()

    interface SwitchListener {
        fun onFirstButtonClicked()
        fun onSecondButtonClicked()
    }
}