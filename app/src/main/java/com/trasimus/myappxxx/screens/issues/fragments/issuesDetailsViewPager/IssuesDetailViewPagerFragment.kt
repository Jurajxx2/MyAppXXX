package com.trasim.myapp.screens.issues.fragments.issuesDetailsViewPager

import android.content.Context
import android.os.Bundle
import androidx.navigation.fragment.navArgs
import com.trasim.myapp.R
import com.trasim.myapp.base.fragment.MyAppFragment
import com.trasim.myapp.data.entities.issue.IssueState
import com.trasim.myapp.data.entities.issue.IssueStateNames
import com.trasim.myapp.data.entities.issue.models.checkList.CheckList
import com.trasim.myapp.screens.issues.fragments.issueDetail.IssueDetailFragment
import com.trasim.myapp.screens.issues.fragments.issuesDetailsViewPager.adapters.ScreenSlidePagerAdapter
import com.trasim.myapp.screens.issues.fragments.issuesDetailsViewPager.handlers.IssuesDetailViewPagerFragmentHandler
import com.trasim.base.screens.components.BaseState
import com.trasim.base.screens.components.BaseViews
import kotlinx.android.synthetic.main.issues_detail_view_pager__fragment.*

class IssuesDetailViewPagerFragment : MyAppFragment<Nothing, IssuesDetailViewPagerFragment.State, IssuesDetailViewPagerFragment.Views, IssuesDetailViewPagerFragmentHandler>() {

    val args: IssuesDetailViewPagerFragmentArgs by navArgs()

    override fun setFragmentLayout() = R.layout.issues_detail_view_pager__fragment

    override fun initiateViews(): Views = Views()

    override fun initializeState(parameters: Nothing?): State = State()

    inner class State : BaseState {

        var fragments: ArrayList<MyAppFragment<*, *, *, *>> = arrayListOf()

        override fun saveInstanceState(outState: Bundle?) {
            outState?.let {
                for ((index, fragment) in fragments.withIndex()) {
                    if (fragment.isAdded) {
                        childFragmentManager.putFragment(it, index.toString(), fragment)
                    }
                }
            }
        }

        override fun restoreInstanceState(savedInstanceState: Bundle) {
            var i = 0
            while (childFragmentManager.getFragment(savedInstanceState, i.toString()) != null) {
                fragments.add(childFragmentManager.getFragment(savedInstanceState, i.toString()) as MyAppFragment<*, *, *, *>)
                i++
            }
        }
    }

    inner class Views : BaseViews {

        var pagerAdapter: ScreenSlidePagerAdapter? = null

        override fun setupViewModel() {
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {

            var selectedIssue = 0

            if (state?.fragments.isNullOrEmpty()) {
                state?.fragments = arrayListOf()
                for ((index, issue) in args.issues.withIndex()) {

                    val fragment = when {
                        args.issues.size == 1 -> {
                            IssueDetailFragment.newInstance(issue, isFirst = true, isLast = true)
                        }
                        index == 0 -> IssueDetailFragment.newInstance(issue, isFirst = true)
                        index == args.issues.size - 1 -> IssueDetailFragment.newInstance(issue, isLast = true)
                        else -> IssueDetailFragment.newInstance(issue)
                    }

                    state?.fragments?.add(fragment)

                    if (args.selectedIssueId == issue) {
                        selectedIssue = index
                    }
                }
            }

            state?.fragments?.let { pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager, it) }

            pagerVP.adapter = pagerAdapter

            pagerVP.currentItem = selectedIssue

            goBackTV.setOnClickListener { this@IssuesDetailViewPagerFragment.handler.onNavigationBackClick() }
        }

    }

    fun onStateSelected(state: String) {
        when (state) {
            getString(IssueStateNames.WAITING.resId) -> {
                (this.state?.fragments?.get(pagerVP.currentItem) as IssueDetailFragment).onStateSelected(IssueState.NEW)
            }
            getString(IssueStateNames.TAKEN.resId) -> {
                (this.state?.fragments?.get(pagerVP.currentItem) as IssueDetailFragment).onStateSelected(IssueState.IN_PROGRESS)
            }
            getString(IssueStateNames.PARTIALLY_DONE.resId) -> {
                (this.state?.fragments?.get(pagerVP.currentItem) as IssueDetailFragment).onStateSelected(IssueState.DONE)
            }
            getString(IssueStateNames.DONE.resId) -> {
                (this.state?.fragments?.get(pagerVP.currentItem) as IssueDetailFragment).onStateSelected(IssueState.DONE)
            }
            getString(IssueStateNames.DONE_HANDYMAN.resId) -> {
                (this.state?.fragments?.get(pagerVP.currentItem) as IssueDetailFragment).onStateSelected(IssueState.DONE)
            }
            getString(IssueStateNames.DO_NOT_DISTURB.resId) -> {
                (this.state?.fragments?.get(pagerVP.currentItem) as IssueDetailFragment).onStateSelected(IssueState.REJECTED)
            }
        }
    }

    fun updateChecklist(checkList: CheckList) {
        (views?.pagerAdapter?.getItem(pagerVP.currentItem) as IssueDetailFragment).onChecklistCheck(checkList)
    }

    fun showNextIssue(markAsDone: Boolean = false) {
        state?.fragments?.let { fragments ->
            if (markAsDone) {
                //val fragment = fragments[pagerVP.currentItem] as IssueDetailFragment
                //fragment.onStateSelected(IssueState.DONE)
                if (fragments.size - 1 == pagerVP.currentItem) {
                    handler.onNavigationBackClick()
                } else {
                    pagerVP.currentItem = pagerVP.currentItem + 1
                }

            } else {
                pagerVP.currentItem = pagerVP.currentItem + 1
            }
        }
    }

    fun showPreviousIssue() {
        pagerVP.currentItem = pagerVP.currentItem - 1
    }
}



