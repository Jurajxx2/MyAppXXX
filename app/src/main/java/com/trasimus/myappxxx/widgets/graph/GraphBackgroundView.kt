package com.trasim.myapp.widgets.graph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.trasim.myapp.R

const val INNER__PADDING = 20
const val BASE_ANGLE__OFFSET = 25f
const val INTERNAL_CIRCLE__OFFSET = 90f
const val GRAPH__FULL_ANGE = 360 - (2 * BASE_ANGLE__OFFSET)

class GraphBackgroundView : View {

    private var currentValueSweepAngle: Float = 0f
        set(value) {
            field = this.convertPercentValueToAngle(value)
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val grayColorPaint: Paint by lazy {
        this.createPaint(R.color.middle_dark_gray)
    }

    private val greenColorPaint: Paint by lazy {
        this.createPaint(R.color.positive_green)
    }

    fun setPercentageValue(percentageValue: Int) {
        this.currentValueSweepAngle = percentageValue.toFloat()
        this.invalidate()
    }

    fun setValue(percentageValue: Float) = percentageValue.takeIf { it in 0f..100f }?.let { percentageValueInRange ->
        this.currentValueSweepAngle = percentageValueInRange
        this.invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            this.drawMaxRange(canvas)
            this.drawCurrentValue(canvas)
        }
    }

    private fun drawCurrentValue(canvas: Canvas) = this.drawPath(sweepAngle = this.currentValueSweepAngle, paint = this.greenColorPaint, canvas = canvas)

    private fun drawMaxRange(canvas: Canvas) = this.drawPath(sweepAngle = GRAPH__FULL_ANGE, paint = this.grayColorPaint, canvas = canvas)

    private fun drawPath(startAngle: Float = 0f, sweepAngle: Float, paint: Paint, canvas: Canvas) {
        val diameter = Math.min(this.width, this.height).toFloat()
        val isPortraitOrientation = this.height >= this.width

        val verticalOffset = if (isPortraitOrientation) {
            this.height / 2 - diameter / 2
        } else {
            0f
        }

        val horizontalOffset = if (isPortraitOrientation) {
            0f
        } else {
            this.width / 2 - diameter / 2
        }

        val ovalRect = RectF(0f + horizontalOffset + INNER__PADDING, 0f + verticalOffset + INNER__PADDING, diameter + horizontalOffset - INNER__PADDING, diameter + verticalOffset - INNER__PADDING)

        Path().apply {
            addArc(ovalRect, startAngle + INTERNAL_CIRCLE__OFFSET + BASE_ANGLE__OFFSET, sweepAngle)
            canvas.drawPath(this, paint)
        }
    }

    private fun convertPercentValueToAngle(currentValue: Float) = currentValue * GRAPH__FULL_ANGE / 100

    private fun createPaint(
        @ColorRes
        colorResourceId: Int
    ) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.style = Paint.Style.STROKE
        this.isAntiAlias = true
        this.strokeWidth = 18f
        this.strokeCap = Paint.Cap.ROUND
        this.color = ContextCompat.getColor(this@GraphBackgroundView.context, colorResourceId)
    }
}