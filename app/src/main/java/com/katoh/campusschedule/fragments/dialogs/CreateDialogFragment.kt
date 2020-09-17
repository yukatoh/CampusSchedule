package com.katoh.campusschedule.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.katoh.campusschedule.R
import com.katoh.campusschedule.viewmodels.RealmResultViewModel
import com.katoh.campusschedule.models.entity.TermRealmObject
import kotlinx.android.synthetic.main.dialog_create_file.view.*

class CreateDialogFragment : DialogFragment() {
    // View Models
    private val model: RealmResultViewModel by activityViewModels()

    // Event Listeners
    private lateinit var listener: NoticeDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            val inflater = activity.layoutInflater
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val view = inflater.inflate(R.layout.dialog_create_file, null)
            builder.setTitle(getString(R.string.new_create))
                .setView(view)
                // Set the action buttons
                .setPositiveButton(R.string.create) { dialog, which ->
                    if (!view.edit_file_name.text.isNullOrBlank()) {
                        val term = TermRealmObject().apply {
                            termLabel = view.edit_file_name.text.toString()
                            startYear = view.start_year.text.toString()
                            startMonth = view.start_month.text.toString()
                            endYear = view.end_year.text.toString()
                            endMonth = view.end_month.text.toString()
                        }

                        model.createTerm(term)

                        listener.onPositiveClick(this)

                    } else {
                        Toast.makeText(context,
                            R.string.blank_message_term_name, Toast.LENGTH_SHORT).show()
                    }

                }
                .setNegativeButton(R.string.cancel) { dialog, which ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface NoticeDialogListener {
        fun onPositiveClick(dialog: DialogFragment)
    }

    fun setNoticeDialogListener(listener: NoticeDialogListener) {
        this.listener = listener
    }

}