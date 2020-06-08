package com.trasim.myapp.screens.reportProblem.fragments.reportProblem.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.trasim.myapp.R
import com.trasim.myapp.data.entities.issueType.IssueTypeLocal
import com.trasim.myapp.widgets.galleryRow.GalleryRowView
import kotlinx.android.synthetic.main.report_problem__gallery__adapter_view.view.*
import kotlinx.android.synthetic.main.report_problem__notes__adapter_view.view.*
import kotlinx.android.synthetic.main.report_problem__problem_type__adapter_view.view.*
import kotlinx.android.synthetic.main.report_problem__send__adapter_view.view.*
import kotlinx.android.synthetic.main.report_problem__title__adapter_item.view.*

class ReportProblemAdapter(val handler: ReportProblemAdapterHandler) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val adapterItems: MutableList<ReportProblemAdapterItem> = mutableListOf()

    fun setData(newItems: MutableList<ReportProblemAdapterItem>) {
        adapterItems.clear()
        adapterItems.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowType = RowType.values()[viewType]
        return rowType.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: ReportProblemAdapterItem = adapterItems[position]
        val rowType: RowType = RowType.values()[holder.itemViewType]
        rowType.onBindViewHolder(holder, item, this.handler)
    }

    override fun getItemCount() = this.adapterItems.size

    override fun getItemViewType(position: Int): Int {
        return when (adapterItems[position]) {
            is ReportProblemAdapterItem.HeaderItem -> RowType.HEADER.ordinal
            is ReportProblemAdapterItem.TitleItem -> RowType.TITLE.ordinal
            is ReportProblemAdapterItem.SelectItem -> RowType.SELECT.ordinal
            is ReportProblemAdapterItem.GalleryItem -> RowType.GALLERY.ordinal
            is ReportProblemAdapterItem.NotesItem -> RowType.NOTE.ordinal
            is ReportProblemAdapterItem.SendItem -> RowType.SEND.ordinal
        }
    }

    enum class RowType {
        HEADER {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.report_problem__header__adapter_view, parent, false)

                return IssueDetailAdapterViewHolder.HeaderHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: ReportProblemAdapterItem, handler: ReportProblemAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.HeaderHolder)?.let {
                    (adapterItem as? ReportProblemAdapterItem.HeaderItem)?.let {

                    }
                }
            }
        },
        TITLE {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.report_problem__title__adapter_item, parent, false)

                return IssueDetailAdapterViewHolder.TitleHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: ReportProblemAdapterItem, handler: ReportProblemAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.TitleHolder)?.let {
                    (adapterItem as? ReportProblemAdapterItem.TitleItem)?.let {
                        holder.titleTV.text = adapterItem.title
                    }
                }
            }
        },
        SELECT {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.report_problem__problem_type__adapter_view, parent, false)

                return IssueDetailAdapterViewHolder.SelectHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: ReportProblemAdapterItem, handler: ReportProblemAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.SelectHolder)?.let {
                    (adapterItem as? ReportProblemAdapterItem.SelectItem)?.let {
                        holder.buttonHolderCL.setOnClickListener {
                            if (adapterItem.isPhotoBtn){
                                handler.onAddNewImageClick()
                            } else {
                                handler.showSelectDialog(adapterItem.issueTypeLocal)
                            }
                        }

                        holder.buttonIconIV.setImageDrawable(ContextCompat.getDrawable(holder.buttonIconIV.context, adapterItem.iconRes))

                        if (!adapterItem.issueTypeLocal?.name.isNullOrEmpty()) {
                            holder.buttonTextTV.text = adapterItem.issueTypeLocal?.name
                            holder.buttonTextTV.setTextAppearance(holder.buttonTextTV.context, R.style.montserrat_bold__17__black)
                        } else if (adapterItem.isPhotoBtn){
                            holder.buttonTextTV.text = holder.buttonTextTV.context.getString(SelectType.PHOTO.textRes)
                        } else {
                            holder.buttonTextTV.text = holder.buttonTextTV.context.getString(SelectType.PROBLEM.textRes)
                        }
                    }
                }
            }
        },
        NOTE {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.report_problem__notes__adapter_view, parent, false)

                return IssueDetailAdapterViewHolder.NotesHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: ReportProblemAdapterItem, handler: ReportProblemAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.NotesHolder)?.let {
                    (adapterItem as? ReportProblemAdapterItem.NotesItem)?.let {
                        holder.notesET.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(text: Editable?) {
                            }

                            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            }

                            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                handler.getNotes(text.toString())
                            }
                        })
                    }
                }
            }
        },
        SEND {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.report_problem__send__adapter_view, parent, false)

                return IssueDetailAdapterViewHolder.SendHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: ReportProblemAdapterItem, handler: ReportProblemAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.SendHolder)?.let {
                    (adapterItem as? ReportProblemAdapterItem.SendItem)?.let {
                        holder.sendB.setOnClickListener { 
                            handler.sendReport()
                        }
                    }
                }
            }
        },
        GALLERY {
            override fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val context: Context? = parent?.context
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate(R.layout.report_problem__gallery__adapter_view, parent, false)

                return IssueDetailAdapterViewHolder.GalleryHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: ReportProblemAdapterItem, handler: ReportProblemAdapterHandler) {
                (holder as? IssueDetailAdapterViewHolder.GalleryHolder)?.let {
                    (adapterItem as? ReportProblemAdapterItem.GalleryItem)?.let {
                        holder.galleryGRV.resetLayout()
                        holder.galleryGRV.handler = adapterItem.handler
                        holder.galleryGRV.mode = GalleryRowView.Mode.LOCAL_EDITABLE
                        holder.galleryGRV.imageList = adapterItem.urlOrUriList
                    }
                }
            }
        };

        abstract fun createViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder
        abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterItem: ReportProblemAdapterItem, handler: ReportProblemAdapterHandler)
    }

    private sealed class IssueDetailAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        class TitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTV: TextView = itemView.titleTV
        }

        class SelectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val buttonHolderCL: ConstraintLayout = itemView.buttonHolderCL
            val buttonTextTV: AppCompatTextView = itemView.buttonTextTV
            val buttonIconIV: ImageView = itemView.buttonIconIV
        }

        class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val notesET: EditText = itemView.notesET
        }

        class GalleryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val galleryGRV: GalleryRowView = itemView.galleryGRV
        }

        class SendHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val sendB: Button = itemView.sendB
        }
    }

    sealed class ReportProblemAdapterItem {
        class HeaderItem : ReportProblemAdapterItem()
        class TitleItem(val title: String) : ReportProblemAdapterItem()
        class SelectItem(val iconRes: Int, val issueTypeLocal: IssueTypeLocal?, val isPhotoBtn: Boolean) : ReportProblemAdapterItem()
        class GalleryItem(val urlOrUriList: List<String>, val handler: GalleryRowView.GalleryRowHandler) : ReportProblemAdapterItem()
        class NotesItem : ReportProblemAdapterItem()
        class SendItem : ReportProblemAdapterItem()
    }
    
    interface ReportProblemAdapterHandler{
        fun showSelectDialog(selectedOption: IssueTypeLocal?)
        fun showConfirmDeleteDialog()
        fun getNotes(notes: String)
        fun onAddNewImageClick()
        fun sendReport()
    }

    enum class SelectType(val iconRes: Int, val textRes: Int){
        PROBLEM(R.drawable.focus, R.string.report_problem__select),
        PHOTO(R.drawable.camera, R.string.report_problem__take_a_photo);
    }
}