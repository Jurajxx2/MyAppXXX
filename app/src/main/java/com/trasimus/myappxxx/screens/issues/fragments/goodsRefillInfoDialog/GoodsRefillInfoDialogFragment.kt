package com.trasim.myapp.screens.issues.fragments.goodsRefillInfoDialog

import android.content.Context
import android.os.Bundle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.trasim.myapp.R
import com.trasim.myapp.base.fragment.MyAppDialogFragment
import com.trasim.myapp.screens.issues.fragments.goodsRefillInfoDialog.adapters.ChecklistChange
import com.trasim.myapp.screens.issues.fragments.goodsRefillInfoDialog.adapters.GoodsRefillInfoAdapter
import com.trasim.myapp.screens.issues.fragments.goodsRefillInfoDialog.handler.GoodsRefillInfoHandler
import com.trasim.base.screens.components.BaseState
import com.trasim.base.screens.components.BaseViews
import kotlinx.android.synthetic.main.issues__goods_refill_info_dialog_fragment.*

class GoodsRefillInfoDialogFragment : MyAppDialogFragment<Nothing, GoodsRefillInfoDialogFragment.State, GoodsRefillInfoDialogFragment.Views, GoodsRefillInfoHandler>() {

    val args: GoodsRefillInfoDialogFragmentArgs by navArgs()

    override fun setFragmentLayout() = R.layout.issues__goods_refill_info_dialog_fragment

    override fun initiateViews(): Views = Views()

    override fun initializeState(parameters: Nothing?): State = State()

    inner class State : BaseState {

        private val changesBundleKey = "changesBundleKey"
        var changes: ChecklistChange? = null

        override fun saveInstanceState(outState: Bundle?) {
            outState?.putSerializable(this.changesBundleKey, changes)
        }

        override fun restoreInstanceState(savedInstanceState: Bundle) {
            this.changes = savedInstanceState.getSerializable(this.changesBundleKey) as ChecklistChange?
        }
    }

    inner class Views : BaseViews {
        override fun setupViewModel() {
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {
            if (state?.changes == null) {
                state?.changes = args.refillInfo
            }

            state?.changes?.let { change ->
                finishGoodsAddB.setOnClickListener {
                    handler.onOkDoneClick(change.checkList)
                }

                val viewManager = LinearLayoutManager(context)
                val viewAdapter = GoodsRefillInfoAdapter(change)

                addGoodsListRV.apply {
                    setHasFixedSize(true)
                    layoutManager = viewManager
                    adapter = viewAdapter
                }
            }
        }
    }
}