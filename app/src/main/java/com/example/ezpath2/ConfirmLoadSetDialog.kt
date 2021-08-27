package com.example.ezpath2

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat

class ConfirmLoadSetDialog(val setName : String) : AppCompatDialogFragment() {

    lateinit var listener : ConfirmLoadSetDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.confirm_load_path_dialog_layout, null)

        val dialog = builder.setView(view)
            .setNegativeButton("No") { _, _ ->

            }
            .setPositiveButton("Yes") { _, _->
                listener.confirmLoadSet(setName)
            }.show()

//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
//            ContextCompat.getColor(requireContext(),
//            R.color.white
//        ))
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
//            ContextCompat.getColor(requireContext(),
//            R.color.interactive_blue
//        ))
//
//        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
//            ContextCompat.getColor(requireContext(),
//            R.color.black
//        ))

        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            listener = activity as ConfirmLoadSetDialogListener
        } catch(e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement CancelServiceDialogListener")
        }
    }

    interface ConfirmLoadSetDialogListener {
        fun confirmLoadSet(setName : String)
    }

}