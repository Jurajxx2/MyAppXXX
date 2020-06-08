package com.trasim.myapp.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.trasim.myapp.R
import com.trasim.myapp.data.entities.issue.*
import kotlinx.android.synthetic.main.custom__room_state_view.view.*

class RoomStateView : LinearLayout {

    var isChairmaid = true
    var handler: RoomStateHandler? = null
    var state: IssueStateNames? = null
        set(value) {
            field = value

            val convertedState: IssueState? = when (state) {
                IssueStateNames.WAITING -> {
                    null
                }
                IssueStateNames.TAKEN -> {
                    IssueState.NEW
                }
                IssueStateNames.PARTIALLY_DONE -> {
                    IssueState.IN_PROGRESS
                }
                IssueStateNames.DONE -> {
                    IssueState.DONE
                }
                IssueStateNames.DONE_HANDYMAN -> {
                    IssueState.DONE
                }
                IssueStateNames.DO_NOT_DISTURB -> {
                    IssueState.REJECTED
                }
                null -> {
                    null
                }
            }

            convertedState?.let {
                roomStateViewHolderCL.background = ContextCompat.getDrawable(roomStateViewHolderCL.context, it.getBackground())
                roomStateTV.text = roomStateViewHolderCL.context.getString(it.getTitle(isChairmaid))
                roomStateTV.setTextColor(ContextCompat.getColor(roomStateViewHolderCL.context, it.getColor()))
                arrowUpIV.setColorFilter(ContextCompat.getColor(roomStateViewHolderCL.context, it.getColor()))
                arrowDownIV.setColorFilter(ContextCompat.getColor(roomStateViewHolderCL.context, it.getColor()))
            }
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(this.context, R.layout.custom__room_state_view, this)

        roomStateViewHolderCL.setOnClickListener {
            state?.let { handler?.onRoomStateClick(it) }
        }
    }

    interface RoomStateHandler {
        fun onRoomStateClick(currentState: IssueStateNames)
    }
}