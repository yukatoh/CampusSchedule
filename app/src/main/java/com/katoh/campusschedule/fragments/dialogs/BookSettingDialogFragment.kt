package com.katoh.campusschedule.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.katoh.campusschedule.R
import com.katoh.campusschedule.models.entity.BookContent
import com.katoh.campusschedule.viewmodels.RealmResultViewModel
import kotlinx.android.synthetic.main.dialog_book_setting.view.*

class BookSettingDialogFragment : DialogFragment() {
    // Activity
    private val activity: AppCompatActivity by lazy {
        getActivity() as AppCompatActivity
    }

    // View models
    private val model: RealmResultViewModel by activityViewModels()

    // Event Listener
    private lateinit var listener: NoticeDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let { activity ->
            val builder = AlertDialog.Builder(activity)
            val inflater = activity.layoutInflater
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val view = inflater.inflate(R.layout.dialog_book_setting, null).apply {
                // Set texts from preferences to EditView
                title.setText(model.tempBook.title)
                author.setText(model.tempBook.author)
                publisher.setText(model.tempBook.publisher)
            }

            builder.setTitle(getString(R.string.message_input_book_property))
                .setView(view)
                .setPositiveButton(getString(R.string.apply)) { dialog, which ->
                    if (view.title.text.isNotBlank()) {
                        val book = BookContent(
                            title = view.title.text.toString(),
                            author = view.author.text.toString(),
                            publisher = view.publisher.text.toString()
                        )
                        model.updateTempBook(book)
                        listener.onPositiveClick(this, book)

                    } else {
                        Toast.makeText(context,
                            getString(R.string.blank_message_booktitle), Toast.LENGTH_SHORT).show()
                    }

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
        fun onPositiveClick(dialog: DialogFragment, book: BookContent)
    }

    fun setNoticeDialogListener(listener: NoticeDialogListener) {
        this.listener = listener
    }

    companion object {
        const val TAG_DEFAULT = "default"
    }
}