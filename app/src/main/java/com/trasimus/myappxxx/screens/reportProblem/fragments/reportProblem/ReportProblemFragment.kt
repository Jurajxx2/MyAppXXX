package com.trasim.myapp.screens.reportProblem.fragments.reportProblem

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.trasim.myapp.R
import com.trasim.myapp.Session
import com.trasim.myapp.base.fragment.MyAppFragment
import com.trasim.myapp.data.entities.issueType.IssueTypeLocal
import com.trasim.myapp.screens.reportProblem.fragments.reportProblem.adapters.ReportProblemAdapter
import com.trasim.myapp.screens.reportProblem.fragments.reportProblem.handlers.ReportProblemHandler
import com.trasim.myapp.screens.reportProblem.fragments.reportProblem.viewModel.ReportProblemFragmentViewModel
import com.trasim.myapp.widgets.galleryRow.GalleryRowView
import com.trasim.base.data.remote.transaction.TransactionResponseHandler
import com.trasim.base.screens.components.BaseViews
import kotlinx.android.synthetic.main.report_problem__report_problem__fragment.*
import java.io.File


class ReportProblemFragment : MyAppFragment<Nothing, Nothing, ReportProblemFragment.Views, ReportProblemHandler>(), GalleryRowView.GalleryRowHandler, CameraHandler {

    private val photos = mutableListOf<UploadPhoto>()
    private var problem: IssueTypeLocal? = null

    override fun setFragmentLayout() = R.layout.report_problem__report_problem__fragment

    override fun initiateViews(): Views = Views()

    inner class Views : BaseViews {

        val viewModel = ViewModelProviders.of(this@ReportProblemFragment, ReportProblemFragmentViewModel.InstanceFactory()).get(ReportProblemFragmentViewModel::class.java)

        private val reportProblemAdapterHandler = object : ReportProblemAdapter.ReportProblemAdapterHandler {
            override fun showSelectDialog(selectedOption: IssueTypeLocal?) {
                this@ReportProblemFragment.openIssueTypesBottomSheet(selectedOption)

            }
            override fun showConfirmDeleteDialog() = this@ReportProblemFragment.handler.showConfirmDeleteDialog()
            override fun getNotes(notes: String) = this@ReportProblemFragment.handler.writeNotes(notes)
            override fun onAddNewImageClick() = this@ReportProblemFragment.handler.onAddNewImageClick(this@ReportProblemFragment)
            override fun sendReport() = this@ReportProblemFragment.sendReport()
        }

        private val reportProblemAdapter = ReportProblemAdapter(this.reportProblemAdapterHandler)

        fun addNewPhoto(uri: String) {
            photos.add(UploadPhoto(uri))
            setupAdapterRows()
        }

        fun deletePhoto(photoToDelete: String) {
            val selectedFile = File(photoToDelete)
            if (selectedFile.exists()) {
                selectedFile.delete()
            }
            photos.removeAll { it.photoPath == photoToDelete }
            setupAdapterRows()
        }

        fun selectProblemType(type: IssueTypeLocal) {
            problem = type
            setupAdapterRows()
        }

        private fun setupAdapterRows() {
            val adapterRows = mutableListOf<ReportProblemAdapter.ReportProblemAdapterItem>()
            adapterRows.add(ReportProblemAdapter.ReportProblemAdapterItem.HeaderItem())
            adapterRows.add(ReportProblemAdapter.ReportProblemAdapterItem.TitleItem(this@ReportProblemFragment.resources.getString(R.string.report_problem__problem_type)))
            adapterRows.add(ReportProblemAdapter.ReportProblemAdapterItem.SelectItem(ReportProblemAdapter.SelectType.PROBLEM.iconRes, problem, false))
            if (photos.isEmpty()) {
                adapterRows.add(ReportProblemAdapter.ReportProblemAdapterItem.TitleItem(this@ReportProblemFragment.resources.getString(R.string.report_problem__take_a_photo)))
                adapterRows.add(ReportProblemAdapter.ReportProblemAdapterItem.SelectItem(ReportProblemAdapter.SelectType.PHOTO.iconRes, null, true))
            } else {
                adapterRows.add(ReportProblemAdapter.ReportProblemAdapterItem.TitleItem(String.format(this@ReportProblemFragment.resources.getString(R.string.report_problem__photos_count), photos.size)))
                adapterRows.add(ReportProblemAdapter.ReportProblemAdapterItem.GalleryItem(photos.map { it.photoPath }, this@ReportProblemFragment))
            }

            adapterRows.add(ReportProblemAdapter.ReportProblemAdapterItem.TitleItem(this@ReportProblemFragment.resources.getString(R.string.report_problem__add_note)))
            adapterRows.add(ReportProblemAdapter.ReportProblemAdapterItem.NotesItem())
            adapterRows.add(ReportProblemAdapter.ReportProblemAdapterItem.SendItem())

            views?.reportProblemAdapter?.setData(adapterRows)
        }

        override fun setupViewModel() {}

        override fun modifyViews(context: Context?, bundle: Bundle?) {

            goBackToRoomTV.text = this@ReportProblemFragment.handler.getName()

            goBackToRoomTV.setOnClickListener {
                handler.onGoBackToRoomPressed()
            }

            reportProblemRV.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = reportProblemAdapter
            }

            setupAdapterRows()
        }
    }

    override fun onAddNewClick() = handler.onAddNewImageClick(this@ReportProblemFragment)

    override fun onImageClick(id: String, position: Int) =
        handler.onImageClick(photos.map { it.photoPath }, id, position)

    override fun onPhotoSaved(uri: String) {
        views?.addNewPhoto(uri)
    }

    fun onProblemTypeSelected(problemType: IssueTypeLocal) {
        views?.selectProblemType(problemType)
    }

    fun onPhotoRemoved(photoToDelete: String) {
        views?.deletePhoto(photoToDelete)
    }

    fun sendReport() {
        problem?.let {

            val issueName = if (this@ReportProblemFragment.handler.getName() != null) {
                this@ReportProblemFragment.handler.getName() as String
            } else {
                "Room issue"
            }

            this.enableOverlay()

            this.views?.viewModel?.sendCreateIssue(issueName, it.name, it.code, this@ReportProblemFragment.handler.getNote())?.observe(this, Observer { networkResponse ->
                when (networkResponse.responseStatus) {
                    TransactionResponseHandler.ResponseStatus.SUCCESS -> {
                        networkResponse.result?.let { remoteIssue ->
                            Session.application.sessionStorage.userLocal?.token?.let { token ->
                                val photoToUpload = this.photos.firstOrNull { uploadPhoto -> !uploadPhoto.uploaded }
                                if (photoToUpload != null) {
                                    this@ReportProblemFragment.uploadPhotos(remoteIssue.id, token)
                                } else {
                                    this.disableOverlay()
                                    this.handler.finishActivity()
                                }
                            }
                        }
                    }
                    TransactionResponseHandler.ResponseStatus.ERROR -> {
                        this@ReportProblemFragment.disableOverlay()
                        Toast.makeText(this@ReportProblemFragment.context, R.string.report_problem__unable_to_report_issue, Toast.LENGTH_SHORT).show()
                    }
                    TransactionResponseHandler.ResponseStatus.UNAUTHORIZED -> {
                    }
                }
            })
        }
    }

    private fun uploadPhotos(id: String, token: String) {
        fun photoToByteArray(path: String) = File(path).readBytes()

        val photoToUpload = this.photos.firstOrNull { !it.uploaded }
        if (photoToUpload != null) {
            this.views?.viewModel?.sendCreateIssueImage(token, id, photoToByteArray(photoToUpload.photoPath))?.observe(this@ReportProblemFragment, Observer {
                when (it.responseStatus) {
                    TransactionResponseHandler.ResponseStatus.SUCCESS, TransactionResponseHandler.ResponseStatus.ERROR, TransactionResponseHandler.ResponseStatus.UNAUTHORIZED -> {
                        photoToUpload.uploaded = true
                        this.uploadPhotos(id, token)
                    }
                }
            })
        } else {
            this.disableOverlay()
            this.handler.finishActivity()
        }

        return
    }

    private fun enableOverlay() {
        uploadingInfoHolderCL.visibility = View.VISIBLE
    }

    private fun disableOverlay() {
        uploadingInfoHolderCL.visibility = View.GONE
    }

    private fun openIssueTypesBottomSheet(selectedOption: IssueTypeLocal?) {
        Thread(Runnable {
            this.views?.viewModel?.getIssueTypes()?.let { this@ReportProblemFragment.handler.showSelectBottomSheet(it, selectedOption) }
        }).start()
    }
}

private class UploadPhoto(val photoPath: String, var uploaded: Boolean = false)

interface CameraHandler{
    fun onPhotoSaved(uri: String)
}