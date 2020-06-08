package com.trasim.myapp.screens.issues.fragments.writeStateBottomSheetDialog

import android.content.Context
import android.os.Bundle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.trasim.myapp.R
import com.trasim.myapp.base.fragment.MyAppBottomSheetDialogFragment
import com.trasim.myapp.data.entities.issue.models.checkList.CheckList
import com.trasim.myapp.screens.issues.fragments.goodsRefillInfoDialog.adapters.ChecklistChange
import com.trasim.myapp.screens.issues.fragments.writeStateBottomSheetDialog.adapters.GoodsListAdapter
import com.trasim.myapp.screens.issues.fragments.writeStateBottomSheetDialog.handlers.WriteStateBottomSheetDialogHandler
import com.trasim.myapp.widgets.CounterView
import com.trasim.base.screens.components.BaseState
import com.trasim.base.screens.components.BaseViews
import kotlinx.android.synthetic.main.issues__write_state_bottom_sheet_dialog_fragment.*

class WriteStateBottomSheetDialogFragment : MyAppBottomSheetDialogFragment<Nothing, WriteStateBottomSheetDialogFragment.State, WriteStateBottomSheetDialogFragment.Views, WriteStateBottomSheetDialogHandler>(),
    CounterView.CounterHandler {

    val args: WriteStateBottomSheetDialogFragmentArgs by navArgs()

    override fun setFragmentLayout() = R.layout.issues__write_state_bottom_sheet_dialog_fragment

    override fun initiateViews(): Views = Views()

    override fun initializeState(parameters: Nothing?): State = State()

    inner class State : BaseState {

        private val changesBundleKey = "changesBundleKey"
        private val checklistBundleKey = "checklistBundleKey"
        var changes: ArrayList<Int>? = null
        var checkList: CheckList? = null

        override fun saveInstanceState(outState: Bundle?) {
            outState?.putIntegerArrayList(this.changesBundleKey, changes)
            outState?.putSerializable(this.checklistBundleKey, checkList)
        }

        override fun restoreInstanceState(savedInstanceState: Bundle) {
            this.changes = savedInstanceState.getIntegerArrayList(this.changesBundleKey)
            this.checkList = savedInstanceState.getSerializable(this.checklistBundleKey) as CheckList?
        }
    }

    inner class Views : BaseViews {
        override fun setupViewModel() {
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {
            if (state?.checkList == null) {
                state?.checkList = args.checklist
                state?.changes = arrayListOf()
            }

            state?.checkList?.let { checklist ->
                for (singleChecklist in checklist.meta.list) {
                    state?.changes?.add(0)
                }

                countGoodsConfirmB.setOnClickListener {
                    state?.changes?.let { changes ->
                        val checkListChange = ChecklistChange(checklist, changes)
                        handler.onConfirmNumbersButtonClick(checkListChange)
                    }
                }

                val viewManager = LinearLayoutManager(context)
                val viewAdapter = GoodsListAdapter(checklist, state?.changes, this@WriteStateBottomSheetDialogFragment)

                countGoodsListRV.apply {
                    setHasFixedSize(true)
                    layoutManager = viewManager
                    adapter = viewAdapter
                }
            }
        }
    }

    override fun onValueChanged(value: Int, position: Int) {
        state?.changes?.set(position, value)
    }
}