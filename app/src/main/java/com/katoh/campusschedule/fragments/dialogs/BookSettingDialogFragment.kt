package com.katoh.campusschedule.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.katoh.campusschedule.R
import com.katoh.campusschedule.viewmodels.RealmResultViewModel

class BookSettingDialogFragment : DialogFragment() {
    // View Models
    private val model: RealmResultViewModel by activityViewModels()

    // Event Listener
    private lateinit var listener: NoticeDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            val inflater = activity.layoutInflater
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val view = inflater.inflate(R.layout.dialog_book_setting, null).apply {
                // Set texts from model to EditView
            }

            builder.setTitle(getString(R.string.term_setting))
                .setView(view)
                .setPositiveButton(getString(R.string.apply)) { dialog, which ->
                    listener.onPositiveClick(this)
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
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

    companion object {
        const val TAG_DEFAULT = "default"
    }
}