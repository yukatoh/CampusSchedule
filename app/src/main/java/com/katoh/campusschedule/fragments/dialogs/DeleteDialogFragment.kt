package com.katoh.campusschedule.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.katoh.campusschedule.R
import java.lang.IllegalStateException

class DeleteDialogFragment : DialogFragment() {
    // Use this instance of the interface to deliver action events
    private lateinit var listener: NoticeDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.confirm_message_delete)
                .setPositiveButton(R.string.yes) { dialog, which ->
                    // Send the positive button event back to the host activity
                    listener.onPositiveClick(this)
                }
                .setNegativeButton(R.string.no) { _, _ ->  }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface NoticeDialogListener {
        fun onPositiveClick(dialog: DialogFragment)
    }

    fun setNoticeDialogListener(listener: NoticeDialogListener) {
        this.listener = listener
    }

    companion object {
        const val TAG_POPUP = "popup"
        const val TAG_CAM = "cam"
    }
}