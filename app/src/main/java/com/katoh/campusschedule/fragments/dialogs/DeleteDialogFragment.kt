package com.katoh.campusschedule.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.katoh.campusschedule.R

class DeleteDialogFragment : DialogFragment() {
    // Activity
    private val activity: AppCompatActivity by lazy {
        getActivity() as AppCompatActivity
    }

    private lateinit var listener: NoticeDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(activity)
        return builder
            .setMessage(R.string.confirm_message_delete)
            .setPositiveButton(R.string.yes) { dialog, which ->
                // Send the positive button event back to the host activity
                listener.onPositiveClick(this)
            }
            .setNegativeButton(R.string.no) { _, _ ->  }
            .create()
    }

    // Interface
    interface NoticeDialogListener {
        fun onPositiveClick(dialog: DialogFragment)
    }

    fun setNoticeDialogListener(listener: NoticeDialogListener) {
        this.listener = listener
    }

    // Tags management
    companion object {
        const val TAG_POPUP = "popup"
        const val TAG_CAM = "cam"
    }

}