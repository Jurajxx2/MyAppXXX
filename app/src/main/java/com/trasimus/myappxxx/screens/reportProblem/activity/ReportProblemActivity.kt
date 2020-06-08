package com.trasim.myapp.screens.reportProblem.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.trasim.myapp.BuildConfig
import com.trasim.myapp.R
import com.trasim.myapp.Session
import com.trasim.myapp.base.acitivity.MyAppAutoLogoutActivity
import com.trasim.myapp.data.entities.issueType.IssueTypeLocal
import com.trasim.myapp.screens.common.deleteDialog.handlers.DeleteDialogHandler
import com.trasim.myapp.screens.common.galleryViewPager.GalleryViewPagerFragmentDirections
import com.trasim.myapp.screens.common.galleryViewPager.handlers.GalleryPagerHandler
import com.trasim.myapp.screens.common.issuesTypeSelectionBottomSheetDialog.handlers.IssueTypeSelectionBottomSheetDialogHandler
import com.trasim.myapp.screens.common.successDialog.handlers.SuccessDialogHandler
import com.trasim.myapp.screens.reportProblem.fragments.reportProblem.CameraHandler
import com.trasim.myapp.screens.reportProblem.fragments.reportProblem.ReportProblemFragment
import com.trasim.myapp.screens.reportProblem.fragments.reportProblem.ReportProblemFragmentDirections
import com.trasim.myapp.screens.reportProblem.fragments.reportProblem.handlers.ReportProblemHandler
import com.trasim.base.helpers.compressImage
import com.trasim.base.helpers.formatDateInUTC
import com.trasim.base.helpers.navigateSafe
import com.trasim.base.screens.components.BaseActivityViews
import com.trasim.base.screens.components.BaseParameters
import com.trasim.base.screens.components.BaseState
import kotlinx.android.synthetic.main.report_problem__activity.*
import org.joda.time.DateTime
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

const val NAME__EXTRAS_KEY = "NAME__EXTRAS_KEY"

class ReportProblemActivity : MyAppAutoLogoutActivity<ReportProblemActivity.Parameters, ReportProblemActivity.State, ReportProblemActivity.Views>(), ReportProblemHandler, GalleryPagerHandler, SuccessDialogHandler, DeleteDialogHandler, IssueTypeSelectionBottomSheetDialogHandler {

    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    private val CAMERA_REQUEST_CODE = 3

    private var cameraHandler: CameraHandler? = null
    private var currentPhotoPath: String? = null
    private var photoToDelete: String? = null

    override fun initializeParameters() = Parameters()

    override fun initializeState(parameters: Parameters?): State? = State(parameters)
    override fun initializeViews() = Views()

    inner class Parameters : BaseParameters {

        var name: String? = null

        override fun loadParameters(extras: Bundle?) {
            this.name = extras?.getString(NAME__EXTRAS_KEY)
        }
    }

    inner class State(parameters: Parameters?) : BaseState {

        private val PHOTO_ORIENTATION__BUNDLE_KEY = "PHOTO_ORIENTATION__BUNDLE_KEY"
        private val NAME__BUNDLE_KEY = "NAME__BUNDLE_KEY"
        private val NOTE__BUNDLE_KEY = "NOTE__BUNDLE_KEY"

        private var photoOrientation = 0

        var name: String? = parameters?.name
        var note: String? = ""

        override fun saveInstanceState(outState: Bundle?) {
            outState?.putInt(PHOTO_ORIENTATION__BUNDLE_KEY, this.photoOrientation)
            outState?.putString(NAME__BUNDLE_KEY, this.name)
            outState?.putString(NOTE__BUNDLE_KEY, this.note)
        }

        override fun restoreInstanceState(savedInstanceState: Bundle) {
            this.photoOrientation = savedInstanceState.getInt(PHOTO_ORIENTATION__BUNDLE_KEY)
            this.name = savedInstanceState.getString(NAME__BUNDLE_KEY)
            this.note = savedInstanceState.getString(NOTE__BUNDLE_KEY)
        }
    }

    inner class Views : BaseActivityViews {
        override fun setupViewModel() {}

        override fun modifyViews(context: Context?, bundle: Bundle?) {}

        override fun setNavigationGraph() = R.id.reportProblemNavigationHostF
    }

    override fun setActivityLayout() = R.layout.report_problem__activity

    override fun onAddNewImageClick(handler: CameraHandler){
        this.cameraHandler = handler
        ActivityCompat.requestPermissions((this), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    private fun openCamera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    this@ReportProblemActivity.createImageFile()
                } catch (ex: IOException) {
                    Snackbar.make(reportProblemHolderCL, R.string._report_problem__error_file_not_created, Snackbar.LENGTH_LONG).show()
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(this, BuildConfig.FILE_PROVIDER, it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = formatDateInUTC(DateTime())
        val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", ContextCompat.getExternalFilesDirs(this, Environment.DIRECTORY_PICTURES)[0])
        this.currentPhotoPath = Uri.fromFile(imageFile).path
        return imageFile
    }

    override fun onImageClick(photos: List<String>, id: String, position: Int) {
        this.navController?.navigateSafe(ReportProblemFragmentDirections.startGalleryViewPagerFragment(photos.toTypedArray(), position, true))
    }

    override fun sendReport() {
        this.navController?.navigateSafe(ReportProblemFragmentDirections.startSuccessDialogFragment())
    }

    override fun showConfirmDeleteDialog() {
        this.navController?.navigateSafe(GalleryViewPagerFragmentDirections.startDeleteDialogFragment())
    }

    override fun showSelectBottomSheet(options: List<IssueTypeLocal>, selectedOption: IssueTypeLocal?) {
        this.navController?.navigateSafe(ReportProblemFragmentDirections.startIssueTypeSelectionBottomSheetDialogFragment(options.toTypedArray(), selectedOption))
    }

    override fun onGoBackToRoomPressed() {
        onBackPressed()
    }

    override fun onBackTextClick() {
        this.navController?.navigateUp()
    }

    override fun onDeletePhotoClick(path: String, position: Int) {
        photoToDelete = path
        this.navController?.navigateSafe(GalleryViewPagerFragmentDirections.startDeleteDialogFragment())
    }

    override fun onGoBackToIssuesButtonClick() {
        navController?.navigateUp()
        finish()
    }

    override fun onShowNextIssueButtonClick(additionalInfo: String) {
        navController?.navigateUp()
        Session.application.router.startReportProblemActivity(additionalInfo, this, true)
    }

    override fun onIssueTypeSelected(state: IssueTypeLocal) {
        reportProblemNavigationHostF?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is ReportProblemFragment) {
                    fragment.onProblemTypeSelected(state)
                }
            }
        }
        navController?.navigateUp()
    }

    override fun onDeleteConfirmationClick() {
        navController?.navigateUp()
        navController?.navigateUp()
        Handler().postDelayed({
            reportProblemNavigationHostF?.let { navFragment ->
                navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                    if (fragment is ReportProblemFragment) {
                        photoToDelete?.let { uri ->
                            fragment.onPhotoRemoved(uri)
                        }
                    }
                }
            }
        }, 100)
    }

    override fun onDeleteGoBackClick() {
        photoToDelete = null
        navController?.navigateUp()
    }

    override fun writeNotes(notes: String) {
        state?.note = notes
    }

    override fun getNote(): String = if (this.state?.note != null) {
        this.state?.note as String
    } else {
        ""
    }

    override fun getName(): String? = this.state?.name

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(reportProblemHolderCL, R.string.report_problem__permission_not_granted, Snackbar.LENGTH_INDEFINITE).setAction(R.string.report_problem__try_again) {
                    ActivityCompat.requestPermissions((this), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
                }
                    .show()
            } else {
                openCamera()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val selectedFile = File(currentPhotoPath)
                        val selectedFileByteArrayContent: ByteArray = selectedFile.readBytes()
                        val compressedResult = compressImage(selectedFileByteArrayContent, 50)

                        if (selectedFile.exists()) {
                            selectedFile.delete()
                        }

                        try {
                            val fos = FileOutputStream(selectedFile.path)
                            fos.write(compressedResult)
                            fos.close()
                        }
                        catch (e: IOException) {
                            Snackbar.make(reportProblemHolderCL, "Photo was not correctly saved", Snackbar.LENGTH_LONG).show()
                        }

                        currentPhotoPath?.let {
                            this@ReportProblemActivity.cameraHandler?.onPhotoSaved(it)
                        }
                    }
                }
            }
        }
    }

    override fun finishActivity() {
        Toast.makeText(this, R.string.report_problem__issue_created, Toast.LENGTH_SHORT).show()
        this.finish()
    }
}
