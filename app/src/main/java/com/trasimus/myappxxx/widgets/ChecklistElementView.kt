package com.trasim.myapp.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.trasim.myapp.R
import com.trasim.myapp.data.entities.issue.CheckListType
import com.trasim.myapp.data.entities.issue.models.checkList.CheckList
import kotlinx.android.synthetic.main.custom__checklist_element_view.view.*

class ChecklistElementView : LinearLayout {

    var handler: ChecklistHandler? = null
    var mark: Mark? = null
        set(value) {
            field = value
            setView()
        }
    var itemCount: CheckListType? = null
        set(value) {
            field = value
            setView()
        }
    var room: Room? = null
        set(value) {
            field = value
            setView()
        }
    private var title = ""
    private var subtitle = ""
    var number = 0
        set(value) {
            field = value
            val txt = "$number."
            itemNumberTV.text = txt
        }
    var checklist: CheckList? = null
        set(value) {
            field = value
            checklist?.let {
                setData(it)
                mark = if (it.value == 1) {
                    Mark.CHECKED
                } else {
                    Mark.NON_CHECKED
                }
            }
        }
    var isDisabled: Boolean = false
        set(value) {
            field = value
            if (isDisabled) {
                disableView()
            }
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(this.context, R.layout.custom__checklist_element_view, this)

        markIV.setOnClickListener {
            if (room == Room.CLEANING) {
                if (itemCount == CheckListType.ITEM) {
                    mark = if (mark == Mark.CHECKED) {
                        Mark.NON_CHECKED
                    } else {
                        Mark.CHECKED
                    }
                    setView()
                    mark?.let {
                        checklist?.let { checklist ->
                            handler?.onCheckClick(it, checklist)
                        }
                    }
                } else {
                    if (mark == Mark.NON_CHECKED) {
                        checklist?.let { handler?.openSublist(it) }
                    } else {
                        mark = Mark.NON_CHECKED
                        mark?.let {
                            checklist?.let { checklist ->
                                handler?.onCheckClick(it, checklist)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun disableView() {
        markIV.setOnClickListener(null)
        markIV.isClickable = false

        checklistItemHolderCL.alpha = 0.2f
    }

    private fun setData(checkList: CheckList) {
        title = checkList.name
        subtitle = checkList.description
        setText()
    }

    private fun setText() {
        itemTitleTV.text = title
        itemDescriptionTV.text = subtitle
    }

    private fun setView() {
        room?.let { room ->
            if (room == Room.CLEANING) {
                CheckListState.ENABLED.setBackground()?.let {
                    checklistItemHolderCL.background = ContextCompat.getDrawable(checklistItemHolderCL.context, it)
                }
            }

            mark?.getBackground(room == Room.CLEANING, itemCount)?.let {
                markIV.background = ContextCompat.getDrawable(markIV.context, it)
            }

            mark?.getDrawable(room == Room.CLEANING, itemCount, room)?.let {
                markIV.setImageDrawable(ContextCompat.getDrawable(markIV.context, it))
            } ?: run {
                markIV.setImageDrawable(null)
            }

            mark?.getColorFilter(itemCount, room)?.let {
                markIV.setColorFilter(ContextCompat.getColor(markIV.context, it))
            }
        }
    }

    enum class CheckListState {
        ENABLED {
            override fun setBackground() = R.drawable.white_rounded__background
        },
        DISABLED {
            override fun setBackground(): Int? = null
        };

        abstract fun setBackground(): Int?
    }

    enum class Mark {
        CHECKED {
            override fun getBackground(isEnabled: Boolean, type: CheckListType?) = if (isEnabled){ R.drawable.shape__rounded_blue } else { null }

            override fun getDrawable(isEnabled: Boolean, type: CheckListType?, state: Room) = R.drawable.check_ok

            override fun getColorFilter(type: CheckListType?, state: Room) = state.getColor()
        },
        NON_CHECKED {
            override fun getBackground(isEnabled: Boolean, type: CheckListType?) = if (isEnabled) {
                if (type == CheckListType.ITEM) {
                    R.drawable.shape__rounded_middle_gray
                } else {
                    android.R.color.transparent
                }
            } else {
                null
            }

            override fun getDrawable(isEnabled: Boolean, type: CheckListType?, state: Room) =
                if (type == CheckListType.ITEM || !isEnabled) {
                    null
                } else {
                    R.drawable.more_right
                }

            override fun getColorFilter(type: CheckListType?, state: Room) = if (type == CheckListType.ITEM) { null } else { R.color.more_dark_gray }
        };

        abstract fun getBackground(isEnabled: Boolean, type: CheckListType?): Int?
        abstract fun getDrawable(isEnabled: Boolean, type: CheckListType?, state: Room): Int?
        abstract fun getColorFilter(type: CheckListType?, state: Room): Int?
    }

    enum class Room {
        CLEANING {
            override fun getColor() = R.color.white
        },
        CLEAN {
           override fun getColor() = R.color.positive_green
        },
        PARTIALLY_CLEAN {
            override fun getColor() = R.color.orange
        },
        DO_NOT_DISTURB {
            override fun getColor() = R.color.red
        };

        abstract fun getColor(): Int
    }

    interface ChecklistHandler {
        fun onCheckClick(newState: Mark, checkList: CheckList)
        fun openSublist(checkList: CheckList)
    }
}