package com.trasim.myapp.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.trasim.myapp.R
import kotlinx.android.synthetic.main.custom__keyboard_view.view.*

class KeyboardView : LinearLayout {

    var keyboardListener: KeyboardListener? = null
    private val keys by lazy {
        mutableListOf<AppCompatTextView>(
            zeroKeyboardElementB,
            firstKeyboardElementTV,
            secondKeyboardElementTV,
            thirdKeyboardElementTV,
            fourthKeyboardElementTV,
            fifthKeyboardElementTV,
            sixthKeyboardElementTV,
            seventhKeyboardElementTV,
            eighthKeyboardElementTV,
            ninthKeyboardElementTV
        )
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(this.context, R.layout.custom__keyboard_view, this)

        (0..9).toList().forEach { value ->
            this.keys[value].apply {
                this.text = value.toString()
                this.setOnClickListener { this@KeyboardView.keyboardListener?.onNumericKeyClick(value) }
            }
        }

        backKeyboardElementIV.setOnClickListener { this.keyboardListener?.onBackKeyClick() }
        backKeyboardElementIV.setOnLongClickListener {
            this.keyboardListener?.onBackKeyLongClick()
            true
        }
    }

    interface KeyboardListener {
        fun onNumericKeyClick(value: Int)
        fun onBackKeyClick()
        fun onBackKeyLongClick()
    }
}