package com.trasim.myapp.screens.issues.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.trasim.myapp.R
import com.trasim.myapp.Session
import com.trasim.myapp.base.acitivity.MyAppAutoLogoutActivity
import com.trasim.myapp.data.entities.issue.IssueState
import com.trasim.myapp.data.entities.issue.IssueStateNames
import com.trasim.myapp.data.entities.issue.models.checkList.CheckList
import com.trasim.myapp.data.entities.user.Role
import com.trasim.myapp.data.entities.user.UserLocal
import com.trasim.myapp.screens.common.galleryViewPager.handlers.GalleryPagerHandler
import com.trasim.myapp.screens.common.simpleSelectionBottomSheetDialog.handlers.SimpleSelectionBottomSheetDialogHandler
import com.trasim.myapp.screens.common.successDialog.handlers.SuccessDialogHandler
import com.trasim.myapp.screens.issues.fragments.goodsRefillInfoDialog.adapters.ChecklistChange
import com.trasim.myapp.screens.issues.fragments.goodsRefillInfoDialog.handler.GoodsRefillInfoHandler
import com.trasim.myapp.screens.issues.fragments.issueDetail.handlers.IssueDetailFragmentHandler
import com.trasim.myapp.screens.issues.fragments.issuesDetailsViewPager.IssuesDetailViewPagerFragment
import com.trasim.myapp.screens.issues.fragments.issuesDetailsViewPager.IssuesDetailViewPagerFragmentDirections
import com.trasim.myapp.screens.issues.fragments.issuesDetailsViewPager.handlers.IssuesDetailViewPagerFragmentHandler
import com.trasim.myapp.screens.issues.fragments.issuesList.IssuesListFragmentDirections
import com.trasim.myapp.screens.issues.fragments.issuesList.handlers.IssueListHandler
import com.trasim.myapp.screens.issues.fragments.writeStateBottomSheetDialog.WriteStateBottomSheetDialogFragmentDirections
import com.trasim.myapp.screens.issues.fragments.writeStateBottomSheetDialog.handlers.WriteStateBottomSheetDialogHandler
import com.trasim.myapp.widgets.ChecklistElementView
import com.trasim.base.helpers.navigateSafe
import com.trasim.base.screens.components.BaseActivityViews
import kotlinx.android.synthetic.main.issues__activity.*


class IssuesActivity : MyAppAutoLogoutActivity<Nothing, Nothing, IssuesActivity.Views>(), IssueListHandler, WriteStateBottomSheetDialogHandler, IssueDetailFragmentHandler, SimpleSelectionBottomSheetDialogHandler, GoodsRefillInfoHandler, SuccessDialogHandler, IssuesDetailViewPagerFragmentHandler, GalleryPagerHandler {

    override fun setActivityLayout() = R.layout.issues__activity

    override fun initializeViews() = Views()

    inner class Views : BaseActivityViews {
        override fun setupViewModel() {
        }

        override fun modifyViews(context: Context?, bundle: Bundle?) {
        }

        override fun setNavigationGraph() = R.id.issuesNavigationHostF
    }

    override fun onRoomStateClick(currentState: IssueStateNames, allowedStates: ArrayList<String>) {
        val user: UserLocal? = Session.application.sessionStorage.userLocal

        val allowedStatesConverted = mutableListOf<String>()
        allowedStates.forEach {
            when (it) {
                IssueState.NEW.serializedName -> {
                    allowedStatesConverted.add(getString(IssueStateNames.WAITING.resId))
                }
                IssueState.IN_PROGRESS.serializedName -> {
                    allowedStatesConverted.add(getString(IssueStateNames.TAKEN.resId))
                }
                IssueState.DONE.serializedName -> {
                    allowedStatesConverted.addAll(listOf(getString(IssueStateNames.DONE.resId), getString(IssueStateNames.PARTIALLY_DONE.resId)))
                }
                IssueState.REJECTED.serializedName -> {
                    allowedStatesConverted.add(getString(IssueStateNames.DO_NOT_DISTURB.resId))
                }
            }
        }

        user?.let {
            if (it.role == Role.CHAIRMAID.code) {
                this.navController?.navigateSafe(IssuesDetailViewPagerFragmentDirections.startSimpleSelectionBottomSheetDialogFragment(allowedStatesConverted.toTypedArray(), getString(currentState.resId)))

            } else {
                this.navController?.navigateSafe(IssuesDetailViewPagerFragmentDirections.startSimpleSelectionBottomSheetDialogFragment(allowedStatesConverted.toTypedArray(), getString(currentState.resId)))
            }
        }
    }

    override fun onIssueClick(selectedIssueId: String, issues: List<String>) {
        this.navController?.navigateSafe(IssuesListFragmentDirections.startIssuesDetailFragment(selectedIssueId, issues.toTypedArray()))
    }

    override fun onFinishedIssuesClick(finishedIssues: List<String>) {
        this.navController?.navigateSafe(IssuesListFragmentDirections.showArchivedIssues())
    }

    override fun onCheckClick(newState: ChecklistElementView.Mark, checkList: CheckList) {
    }

    override fun openSublist(checkList: CheckList) {
        this.navController?.navigateSafe(IssuesDetailViewPagerFragmentDirections.startWriteStateBottomSheetDialogFragment(checkList))
    }

    override fun onConfirmNumbersButtonClick(checklistChange: ChecklistChange) {
        this.navController?.navigateSafe(WriteStateBottomSheetDialogFragmentDirections.startGoodsRefillInfoDialogFragment(checklistChange))
    }

    override fun onNavigationBackClick() {
        this.navController?.navigateUp()
    }

    override fun onTakeOverClick() {
    }

    override fun onDoNotDisturbClick() {
    }

    override fun onSetRoomCleanClick() {
        this.navController?.navigateSafe(IssuesDetailViewPagerFragmentDirections.startSuccessDialogFragment(":)"))
    }

    override fun onReportIssueClick(name: String) = Session.application.router.startReportProblemActivity(name, this)

    override fun onStateSelected(state: String) {
        this.navController?.navigateUp()
        issuesNavigationHostF?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is IssuesDetailViewPagerFragment) {
                    fragment.onStateSelected(state)
                }
            }
        }
    }

    override fun onOkDoneClick(checkList: CheckList) {
        issuesNavigationHostF?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is IssuesDetailViewPagerFragment) {
                    fragment.updateChecklist(checkList)
                }
            }
        }
        this.navController?.navigateUp()
    }

    override fun onGoBackToIssuesButtonClick() {
        this.navController?.navigateUp()
        this.navController?.navigateUp()
    }

    override fun onShowNextIssueButtonClick(additionalInfo: String) {
        this.navController?.navigateUp()
        issuesNavigationHostF?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is IssuesDetailViewPagerFragment) {
                    fragment.showNextIssue(markAsDone = true)
                }
            }
        }
    }

    override fun onImageClick(urlOrUriList: MutableList<String>, id: String, position: Int) {
        this.navController?.navigateSafe(IssuesDetailViewPagerFragmentDirections.startGalleryViewPagerFragment(urlOrUriList.toTypedArray(), position))
    }

    override fun onAddNewClick() {}

    override fun onBackTextClick() {
        this.navController?.navigateUp()
    }

    override fun onDeletePhotoClick(path: String, position: Int) {}

    override fun onNextClick() {
        issuesNavigationHostF?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is IssuesDetailViewPagerFragment) {
                    fragment.showNextIssue()
                }
            }
        }
    }

    override fun onPrevClick() {
        issuesNavigationHostF?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is IssuesDetailViewPagerFragment) {
                    fragment.showPreviousIssue()
                }
            }
        }
    }

    override fun showLogoutDialog() {
        AlertDialog.Builder(this).setTitle(R.string.issues_list__issues_list_activity__logout_title).setMessage(R.string.issues_list__issues_list_activity__logout_message).setPositiveButton(R.string.issues_list__issues_list_activity__logout_positive_button) { _, _ ->
            Session.application.clearDataAndRestartApplication(this)
        }.setNegativeButton(R.string.issues_list__issues_list_activity__logout_negative_button, null).show()
    }

    override fun goBackToIssues() {
        this.navController?.navigateUp()
    }
}
